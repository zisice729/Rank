package org.zisice.douyin_rank_1.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zisice.douyin_rank_1.service.RankService;

/**
 * 排行榜数据同步定时任务（XXL-Job）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RankSyncTask {

    private final RankService rankService;

    /**
     * XXL-Job 任务入口
     *
     * @param param XXL-Job 传入的参数
     * @return 任务执行结果
     */
    @XxlJob("rankRefresh")
    public ReturnT<String> rankRefresh(String param) {
        log.info("开始执行排行榜数据刷新任务，param:{}", param);

        try {
            // 调用 Service 的 rankRefresh 方法
            ReturnT<String> result = rankService.rankRefresh(param);

            return result;
        } catch (Exception e) {
            log.error("排行榜数据刷新任务执行失败，error:{}", e.getMessage(), e);
            XxlJobHelper.handleFail("排行榜数据刷新失败：" + e.getMessage());
            return new ReturnT<>(ReturnT.FAIL_CODE, "排行榜数据刷新失败：" + e.getMessage());
        }
    }
}
