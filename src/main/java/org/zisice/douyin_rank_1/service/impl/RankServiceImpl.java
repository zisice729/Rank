package org.zisice.douyin_rank_1.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.zisice.douyin_rank_1.entity.MerchantRankInfo;
import org.zisice.douyin_rank_1.mapper.MerchantRankInfoMapper;
import org.zisice.douyin_rank_1.param.QueryRankParam;
import org.zisice.douyin_rank_1.service.RankService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 排行榜服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final MerchantRankInfoMapper merchantRankInfoMapper;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis Key 前缀
     */
    private static final String REDIS_KEY_PREFIX = "dy:rank:date:";

    /**
     * 缓存过期时间（7天）
     */
    private static final long CACHE_EXPIRE_DAYS = 7L;

    /**
     * 日期格式化器（yyyy-MM-dd）
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 中午12点的小时值
     */
    private static final int NOON_HOUR = 12;

    @Override
    public List<MerchantRankInfo> list(QueryRankParam param) {
        String selectDate = getQueryDate();
        String redisKey = String.format("%s%s", REDIS_KEY_PREFIX, selectDate);
        String rankStr = redisTemplate.opsForValue().get(redisKey);
        List<MerchantRankInfo> allRankList;

        if (StringUtils.isNotEmpty(rankStr)) {
            allRankList = JSON.parseObject(rankStr, new TypeReference<List<MerchantRankInfo>>() {});
            return filterRank(param, allRankList);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<MerchantRankInfo> listRankInfo(String selectSQL) {
        return merchantRankInfoMapper.listRankInfoBySQL(selectSQL);
    }

    @Override
    public ReturnT<String> rankRefresh(String param) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = format.format(new Date());
        String redisKey = String.format("%s%s", REDIS_KEY_PREFIX, currentTime);

        String selectSQL = "select * from merchant_rank_info where date = '" + currentTime + "'";
        List<MerchantRankInfo> rankInfoList = listRankInfo(selectSQL);

        if (rankInfoList == null || rankInfoList.isEmpty()) {
            log.warn("排行榜数据为空，不更新缓存，保留旧数据。日期: {}", currentTime);
            return new ReturnT<>(ReturnT.FAIL_CODE, "排行榜数据为空，不更新缓存，保留旧数据");
        }

        redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(rankInfoList), CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
        return ReturnT.SUCCESS;
    }

    /**
     * 根据参数过滤排行榜数据：只过滤，不排序，保留数仓原始 sort
     *
     * @param param      查询参数
     * @param allRankList 全部排行榜数据
     * @return 过滤后的排行榜数据
     */
    private List<MerchantRankInfo> filterRank(QueryRankParam param, List<MerchantRankInfo> allRankList) {
        return allRankList.stream()
                .filter(item ->
                        item.getCityId().equals(param.getCityId()) &&
                        item.getType().equals(param.getType()) &&
                        item.getCategory().equals(param.getCategory())
                )
                .collect(Collectors.toList());
    }

    /**
     * 动态计算查询日期
     * 12点前查询前天的数据，12点后查询昨天的数据
     *
     * @return 查询日期（格式：yyyy-MM-dd）
     */
    private String getQueryDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        int hour = now.getHour();
        LocalDate queryDate;

        if (hour >= NOON_HOUR) {
            queryDate = today.minusDays(1);
        } else {
            queryDate = today.minusDays(2);
        }

        return queryDate.format(DATE_FORMATTER);
    }
}
