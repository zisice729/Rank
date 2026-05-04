package org.zisice.douyin_rank_1.service;

import com.xxl.job.core.biz.model.ReturnT;
import org.zisice.douyin_rank_1.entity.MerchantRankInfo;
import org.zisice.douyin_rank_1.param.QueryRankParam;

import java.util.List;

/**
 * 排行榜服务接口
 */
public interface RankService {

    /**
     * 查询排行榜列表
     *
     * @param param 查询参数（cityId, type, category）
     * @return 排行榜列表
     */
    List<MerchantRankInfo> list(QueryRankParam param);

    /**
     * 根据SQL查询排行榜数据
     *
     * @param selectSQL 查询SQL
     * @return 排行榜列表
     */
    List<MerchantRankInfo> listRankInfo(String selectSQL);

    /**
     * 刷新排行榜数据到 Redis
     *
     * @param currentTime 当前日期（格式：yyyy-MM-dd）
     * @return 刷新结果
     */
    ReturnT<String> rankRefresh(String currentTime);
}
