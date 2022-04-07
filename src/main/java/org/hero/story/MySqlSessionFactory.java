package org.hero.story;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;


/**
 * @ClassName MySqlSessionFactory
 * @Description mysql回话工厂
 * @Author hanjiale
 * @Date 2022/4/7 11:01
 * @Version 1.0
 */
public class MySqlSessionFactory {

    /**
     * mybatis sql 回话工厂
     */
    private static SqlSessionFactory _sqlSessionFactory;

    /**
     * 私有化构造方法，防止实例化
     */
    private MySqlSessionFactory(){}

    /**
     * 初始化
     */
    public static void init(){
        try{
            _sqlSessionFactory =(new SqlSessionFactoryBuilder()).build(Resources.getResourceAsStream("MyBatisConfig.xml"));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 开启mysql回话
     * @return
     */
    public static SqlSession openSession(){
        if (_sqlSessionFactory == null){
            throw new RuntimeException("回话工厂尚未初始化");
        }
        return _sqlSessionFactory.openSession(true);
    }

}
