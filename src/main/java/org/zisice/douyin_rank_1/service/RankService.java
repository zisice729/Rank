package org.zisice.douyin_rank_1.service;

import com.xxl.job.core.biz.model.ReturnT;
import org.zisice.douyin_rank_1.entity.MerchantRankInfo;
import org.zisice.douyin_rank_1.param.QueryRankParam;

import java.util.List;

public interface RankService {
    List<MerchantRankInfo> list(QueryRankParam param);
    ReturnT<String> rankRefresh(String param);
}
