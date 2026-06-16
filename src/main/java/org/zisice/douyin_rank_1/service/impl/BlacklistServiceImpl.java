package org.zisice.douyin_rank_1.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.zisice.douyin_rank_1.rpc.BlacklistRpcClient;
import org.zisice.douyin_rank_1.service.BlacklistService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {

    private final BlacklistRpcClient blacklistRpcClient;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_BLACKLIST_KEY = "dy:rank:blacklist";
    private static final long CACHE_EXPIRE_MINUTES = 30L;

    @Override
    public void refreshCache() {
        log.info("开始刷新黑名单缓存");
        try {
            List<String> blacklistedMerchantIds = blacklistRpcClient.getAllBlacklistedMerchantIds();
            redisTemplate.delete(REDIS_BLACKLIST_KEY);
            
            if (CollectionUtils.isNotEmpty(blacklistedMerchantIds)) {
                redisTemplate.opsForSet().add(REDIS_BLACKLIST_KEY, blacklistedMerchantIds.toArray(new String[0]));
                redisTemplate.expire(REDIS_BLACKLIST_KEY, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            }
            log.info("黑名单缓存刷新完成，共 {} 个商家", blacklistedMerchantIds.size());
        } catch (Exception e) {
            log.error("刷新黑名单缓存失败", e);
            // 降级：保留旧缓存，不抛出异常
        }
    }

    @Override
    public boolean isBlacklisted(String merchantId) {
        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(REDIS_BLACKLIST_KEY, merchantId);
            return isMember != null && isMember;
        } catch (Exception e) {
            log.error("检查黑名单失败，merchantId: {}", merchantId, e);
            // 降级：RPC 调用失败时，尝试直接调用 RPC
            return blacklistRpcClient.isMerchantBlacklisted(merchantId);
        }
    }

    @Override
    public List<String> getAllBlacklistedMerchantIds() {
        try {
            Set<String> members = redisTemplate.opsForSet().members(REDIS_BLACKLIST_KEY);
            return members != null ? new ArrayList<>(members) : List.of();
        } catch (Exception e) {
            log.error("获取黑名单列表失败", e);
            // 降级：直接从 RPC 获取
            return blacklistRpcClient.getAllBlacklistedMerchantIds();
        }
    }
}