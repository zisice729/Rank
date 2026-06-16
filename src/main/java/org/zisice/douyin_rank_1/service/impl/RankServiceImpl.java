package org.zisice.douyin_rank_1.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zisice.douyin_rank_1.entity.MerchantRankInfo;
import org.zisice.douyin_rank_1.mapper.MerchantRankInfoMapper;
import org.zisice.douyin_rank_1.service.BlacklistService;
import org.zisice.douyin_rank_1.service.RankService;
import org.zisice.douyin_rank_1.param.QueryRankParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final MerchantRankInfoMapper merchantRankInfoMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final BlacklistService blacklistService;

    private static final String REDIS_VERSION_KEY = "dy:rank:date:refresh";
    private static final String REDIS_DATA_KEY_FORMAT = "dy:rank:date:%s:type:%s:cityId:%s:category:%s";
    private static final long CACHE_EXPIRE_DAYS = 7L;

    @Override
    public List<MerchantRankInfo> list(QueryRankParam param) {
        String versionDate = redisTemplate.opsForValue().get(REDIS_VERSION_KEY);
        String redisKey = String.format(REDIS_DATA_KEY_FORMAT, versionDate, param.getType(), param.getCityId(), param.getCategory());
        String rankStr = redisTemplate.opsForValue().get(redisKey);
        
        List<MerchantRankInfo> result = JSON.parseObject(rankStr, new TypeReference<List<MerchantRankInfo>>() {});
        
        // 过滤黑名单商家
        return result.stream()
                .filter(item -> !blacklistService.isBlacklisted(item.getMerchantId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReturnT<String> rankRefresh(String param) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = format.format(new Date());

        String selectSQL = "select * from merchant_rank_info where date = '" + currentTime + "'";
        List<MerchantRankInfo> rankInfoList = merchantRankInfoMapper.listRankInfoBySQL(selectSQL);

        if (CollectionUtils.isEmpty(rankInfoList)) {
            return ReturnT.FAIL;
        }

        Map<String, List<MerchantRankInfo>> rankInfoMap = rankInfoList.stream()
                .collect(Collectors.groupingBy(rankInfo ->
                        String.format(REDIS_DATA_KEY_FORMAT, currentTime, rankInfo.getType(), rankInfo.getCityId(), rankInfo.getCategory())
                ));

        rankInfoMap.forEach((redisKey, redisValue) ->
                redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(redisValue), 7L, TimeUnit.DAYS)
        );

        redisTemplate.opsForValue().set(REDIS_VERSION_KEY, currentTime, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
        return ReturnT.SUCCESS;
    }
}