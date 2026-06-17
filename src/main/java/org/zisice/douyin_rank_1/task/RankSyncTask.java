package org.zisice.douyin_rank_1.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zisice.douyin_rank_1.service.RankService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 排行榜数据同步任务
 * <p>
 * 使用 XXL-Job 定时任务框架，定期从数据库同步排行榜数据到 Redis 缓存，
 * 确保缓存数据的时效性。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RankSyncTask {

    /**
     * 排行榜服务接口
     */
    private final RankService rankService;

    /**
     * 日期格式化工具
     */
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
        String currentTime = FORMAT.format(new Date());
        log.info("开始执行排行榜同步任务，当前日期：{}", currentTime);
        return rankService.rankRefresh(param);
    }
}