package org.hero.login;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.hero.async.AsyncOperationService;
import org.hero.async.IAsyncOperation;
import org.hero.login.db.IUserDao;
import org.hero.login.db.UserEntity;
import org.hero.story.MySqlSessionFactory;

import java.util.function.Function;

/**
 * @ClassName LoginService
 * @Description 登录服务
 * @Author hanjiale
 * @Date 2022/4/7 11:30
 * @Version 1.0
 */
@Slf4j
public final class LoginService {

    /**
     * 单例对象
     */
    private static final LoginService _instance = new LoginService();

    /**
     * 私有化构造器
     */
    private LoginService(){}

    /**
     * 获取单例对象
     * @return
     */
    public static LoginService getInstance(){
        return _instance;
    }

    /**
     * 用户登录
     * @param userName 用户名称
     * @param password 密码
     * @param callback 回调函数
     */
    public void userLogin(String userName, String password, Function<UserEntity,Void> callback){
        if (userName == null || password == null){
            return;
        }
        //创建异步操纵
        IAsyncOperation asyncOp = new AsyncGetUserByNamme(userName,password) {
            @Override
            public void doFinish() {
                if (callback != null){
                    callback.apply(this.getUserEntity());
                }
            }
        };
        //执行异步操纵
        AsyncOperationService.getInstance().process(asyncOp);

        log.info("用户登录完成，异步执行完毕");
    }

    /**
     * 异步方式获取用户
     */
    private class AsyncGetUserByNamme implements IAsyncOperation{
        /**
         * 用户名称
         */
        private final String userName;
        /**
         * 密码
         */
        private final String password;
        /**
         * 用户实体
         */
        private UserEntity _userEntity = null;

        /**
         * 类参数构造器
         * @param userName
         * @param password
         */
        AsyncGetUserByNamme(String userName,String password){
            this.userName = userName;
            this.password = password;
        }

        /**
         * 获取用户实体
         * @return
         */
        public UserEntity getUserEntity(){
            return _userEntity;
        }

        @Override
        public int bindId() {
            return userName.charAt(userName.length() - 1);
        }

        @Override
        public void doAsync() {
            //这种 写法可以在{}里内容执行完毕后，自动关闭(也就是自动掉sqlSession.close()）
            try(SqlSession sqlSession = MySqlSessionFactory.openSession()){
                // 获取 DAO 对象,
                // 注意: 这个 IUserDao 接口咱们是没有具体实现的,
                // 但如果你听过前面的课,
                // 你可能会猜到这里面究竟发生了什么... :)
                IUserDao dao = sqlSession.getMapper(IUserDao.class);
                //查看当前线程
                log.info("用户登录当前处理线程 ：{}",Thread.currentThread().getName());
                //通过用户名获取用户实体
                UserEntity userEntity = dao.getUserByName(userName);
                if (userEntity != null) {
                    //判断用户密码
                    if (!password.equals(userEntity.password)){
                        //用户密码错误
                        log.error("用户密码错误，userId = {},password = {}",userEntity.userId,password);
                        //throw new RuntimeException("用户密码错误"); 异步里就不要抛异常了
                    }
                } else {
                    //如果用户实体为空，则新建用户
                    userEntity = new UserEntity();
                    userEntity.userName = userName;
                    userEntity.password = password;
                    userEntity.heroAvatar = "Hero_Shaman";//默认使用萨满

                    //将用户实体用户添加到数据库
                    dao.insertInto(userEntity);
                }
                _userEntity = userEntity;
            } catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
    }
}
