package org.hero.handler;

import io.netty.channel.ChannelHandlerContext;
import org.hero.msg.GameMsgProtocol;
import org.hero.rank.RankItem;
import org.hero.rank.RankService;
import org.hero.story.GameMsgEncoder;

import java.util.Collections;

/**
 * @ClassName GetRankCmdHandler
 * @Description 获取排行榜指令处理器
 * @Author hanjiale
 * @Date 2022/4/9 16:02
 * @Version 1.0
 */
public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd cmd) {
        if (ctx  == null ||cmd == null){
            return;
        }
        RankService.getInstance().getRank((rankItemList) -> {
            if (rankItemList == null){
                rankItemList = Collections.emptyList();
            }

            GameMsgProtocol.GetRankResult.Builder resultBuilder = GameMsgProtocol.GetRankResult.newBuilder();
            for (RankItem rankItem : rankItemList){
                GameMsgProtocol.GetRankResult.RankItem.Builder rankItemBuilder = GameMsgProtocol.GetRankResult.RankItem.newBuilder();
                rankItemBuilder.setRankId(rankItem.rankId);
                rankItemBuilder.setUserId(rankItem.userId);
                rankItemBuilder.setUserName(rankItem.userName);
                rankItemBuilder.setHeroAvatar(rankItem.heroAvatar);
                rankItemBuilder.setWin(rankItem.win);

                resultBuilder.addRankItem(rankItemBuilder);
            }
            GameMsgProtocol.GetRankResult result = resultBuilder.build();

            ctx.writeAndFlush(result);

            return null;
        });
    }
}
