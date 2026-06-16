package org.zisice.douyin_rank_1.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zisice.douyin_rank_1.service.RankService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankSyncTask {

    private final RankService rankService;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @XxlJob("rankRefresh")
    public ReturnT<String> rankRefresh(String param) {
        String currentTime = FORMAT.format(new Date());
        return rankService.rankRefresh(param);
    }
}
