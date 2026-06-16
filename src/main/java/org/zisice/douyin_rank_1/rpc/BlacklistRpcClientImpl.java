package org.zisice.douyin_rank_1.rpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlacklistRpcClientImpl implements BlacklistRpcClient {

    // 模拟黑名单数据，实际应从 RPC 服务获取
    private static final List<String> MOCK_BLACKLIST = Arrays.asList(
            "merchant_001", "merchant_002", "merchant_003"
    );

    @Override
    public List<String> getAllBlacklistedMerchantIds() {
        log.info("通过 RPC 获取黑名单数据");
        // 实际项目中这里应该是 RPC 调用
        return MOCK_BLACKLIST;
    }

    @Override
    public boolean isMerchantBlacklisted(String merchantId) {
        log.info("检查商家 {} 是否在黑名单中", merchantId);
        // 实际项目中这里应该是 RPC 调用
        return MOCK_BLACKLIST.contains(merchantId);
    }
}