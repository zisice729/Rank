package org.zisice.douyin_rank_1.task;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.zisice.douyin_rank_1.service.BlacklistService;

@Slf4j
@RequiredArgsConstructor
public class BlacklistSyncTask {

    private final BlacklistService blacklistService;

    @XxlJob("blacklistSync")
    public void syncBlacklist() {
        log.info("开始执行黑名单同步任务");
        blacklistService.refreshCache();
        log.info("黑名单同步任务执行完成");
    }
}