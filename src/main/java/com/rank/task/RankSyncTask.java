package com.rank.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rank.service.RankService;

/**
 * 排行榜数据同步任务
 * <p>
 * 使用 XXL-Job 定时任务框架，定期从数据库同步排行榜数据到 Redis 缓存，
 * 确保缓存数据的时效性。
 * </p>
 */
@Component
public class RankSyncTask {

    /**
     * 排行榜服务接口
     */
    @Autowired
    private RankService rankService;

    /**
     * XXL-Job 定时任务执行方法
     * <p>
     * 任务名为 "rankRefresh"，由 XXL-Job 调度中心配置执行频率，
     * 调用 RankService 的 rankRefresh 方法同步数据。
     * </p>
     *
     * @param param 任务参数，由 XXL-Job 调度中心传入
     * @return 任务执行结果
     */
    @XxlJob("rankRefresh")
    public ReturnT<String> rankRefresh(String param) {
        // 直接调用服务层的刷新方法，将任务参数透传
        return rankService.rankRefresh(param);
    }
}