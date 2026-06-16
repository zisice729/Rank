package org.zisice.douyin_rank_1.rpc;

import java.util.List;

public interface BlacklistRpcClient {

    /**
     * 获取所有黑名单商家ID
     *
     * @return 黑名单商家ID列表
     */
    List<String> getAllBlacklistedMerchantIds();

    /**
     * 检查商家是否在黑名单中
     *
     * @param merchantId 商家ID
     * @return 是否在黑名单
     */
    boolean isMerchantBlacklisted(String merchantId);
}