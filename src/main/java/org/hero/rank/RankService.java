package org.hero.rank;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.hero.async.AsyncOperationService;
import org.hero.async.IAsyncOperation;
import org.hero.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @ClassName RankService
 * @Description 排行榜服务
 * @Author hanjiale
 * @Date 2022/4/9 16:22
 * @Version 1.0
 */
@Slf4j
public final class RankService {

    /**
     * 单例对象
     */
    private static final RankService _instance = new RankService();
    /**
     * 私有化默认构造器
     */
    private RankService(){

    }

    /**
     * 获取单例对象
     * @return
     */
    public static RankService getInstance(){
        return _instance;
    }

    /**
     * 获取排名对象
     * @param callback
     */
    public void getRank(Function<List<RankItem>,Void> callback){

        if (callback == null){
            return;
        }

        IAsyncOperation asyncOp = new AsyncGetRank(){
            @Override
            public void doFinish() {
                callback.apply(this.getRankItemList());
            }
        };

        AsyncOperationService.getInstance().process(asyncOp);
    }

    /**
     * 异步方式获取排名列表
     */
    private class AsyncGetRank implements IAsyncOperation{
        /**
         * 排名列表
         */
        private List<RankItem> _rankItemList = null;

        /**
         * 获取排名列表
         * @return
         */
        public List<RankItem> getRankItemList(){
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try(final Jedis redis = RedisUtil.getRedis()){
                //获取字符串值集合
                Set<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);

                List<RankItem> rankItemList = new ArrayList<>();
                int rankId = 0;
                for (Tuple t : valSet){
                    //获取用户id
                    int userId = Integer.parseInt(t.getElement());

                    //获取用户基本信息
                    String jsonStr = redis.hget("User_" + userId, "BasicInfo");
                    if (jsonStr == null || jsonStr.isEmpty()){
                        continue;
                    }

                    JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                    RankItem rankItem = new RankItem();
                    rankItem.userId = userId;
                    rankItem.rankId = ++ rankId;
                    rankItem.userName = jsonObj.getString("userName");
                    rankItem.heroAvatar = jsonObj.getString("heroAvatar");
                    rankItem.win = (int)t.getScore();
                    rankItemList.add(rankItem);
                }
                _rankItemList = rankItemList;
            } catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
    }

    /**
     * 刷新排行榜
     * @param winnerId 赢家id
     * @param loserId 输家id
     */
    public void refreshRank(int winnerId,int loserId){
        try(Jedis redis = RedisUtil.getRedis()){
            //增加用户输赢次数
            redis.hincrBy("User_" + winnerId,"Win",1);
            redis.hincrBy("User_" + loserId,"Lose",-1);

            //看看赢家赢了多少次
            String winStr = redis.hget("User_" + winnerId,"Win");
            int winInt = Integer.parseInt(winStr);

            //修改排行榜
            redis.zadd("Rank",winInt,String.valueOf(winnerId));
        } catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

}
