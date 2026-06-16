package org.zisice.douyin_rank_1.service;

import java.util.List;

public interface BlacklistService {

    /**
     * 刷新黑名单缓存
     */
    void refreshCache();

    /**
     * 检查商家是否在黑名单中
     *
     * @param merchantId 商家ID
     * @return 是否在黑名单
     */
    boolean isBlacklisted(String merchantId);

    /**
     * 获取所有黑名单商家ID
     *
     * @return 黑名单商家ID列表
     */
    List<String> getAllBlacklistedMerchantIds();
}