package com.rank.service;

import com.xxl.job.core.biz.model.ReturnT;
import com.rank.common.entity.MerchantRankInfo;

import java.util.List;

/**
 * 排行榜服务接口
 * <p>
 * 定义排行榜数据查询和刷新的业务方法，
 * 提供从缓存查询排行榜和从数据库同步数据到缓存的功能。
 * </p>
 */
public interface RankService {

    /**
     * 查询排行榜列表
     * <p>
     * 根据城市ID、榜单类型和类目从 Redis 缓存中获取对应的排行榜数据。
     * </p>
     *
     * @param cityId   城市ID，000000表示全国
     * @param type     榜单类型：0-爆款榜，1-飙升榜
     * @param category 类目：0-全部，1-美食等
     * @return 排行榜商家信息列表
     */
    List<MerchantRankInfo> list(String cityId, Integer type, Integer category);

    /**
     * 刷新排行榜数据
     * <p>
     * 从数据库读取当天的排行榜数据，并同步到 Redis 缓存中，
     * 用于定时任务调用。
     * </p>
     *
     * @param param 任务参数，由 XXL-Job 传入
     * @return 任务执行结果
     */
    ReturnT<String> rankRefresh(String param);
}