package com.rank.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rank.common.entity.MerchantRankInfo;
import com.rank.mapper.MerchantRankInfoMapper;
import com.rank.service.RankService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 排行榜服务实现类
 * <p>
 * 实现排行榜数据的查询和刷新功能，
 * 查询时从 Redis 缓存读取数据以保证性能，
 * 刷新时从数据库读取数据并写入 Redis 缓存。
 * </p>
 */
@Service
public class RankServiceImpl implements RankService {

    /**
     * 商家排行榜数据访问层
     */
    @Autowired
    private MerchantRankInfoMapper merchantRankInfoMapper;

    /**
     * Redis 操作模板
     */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Redis 版本号 Key，存储当前数据的日期版本
     */
    private static final String REDIS_VERSION_KEY = "dy:rank:date:refresh";

    /**
     * Redis 数据 Key 格式，包含日期、类型、城市ID、类目四个维度
     */
    private static final String REDIS_DATA_KEY_FORMAT = "dy:rank:date:%s:type:%s:cityId:%s:category:%s";

    /**
     * 缓存过期时间，单位为天
     */
    private static final long CACHE_EXPIRE_DAYS = 7L;

    /**
     * 查询排行榜列表
     * <p>
     * 根据城市ID、榜单类型和类目构建 Redis Key，从缓存中读取排行榜数据，
     * 如果缓存为空则返回空列表，避免空指针异常。
     * </p>
     *
     * @param cityId   城市ID，000000表示全国
     * @param type     榜单类型：0-爆款榜，1-飙升榜
     * @param category 类目：0-全部，1-美食等
     * @return 排行榜商家信息列表
     */
    @Override
    public List<MerchantRankInfo> list(String cityId, Integer type, Integer category) {
        // 1. 获取当前数据版本日期（从 Redis 读取）
        String versionDate = redisTemplate.opsForValue().get(REDIS_VERSION_KEY);
        
        // 2. 根据日期、类型、城市ID、类目构建 Redis Key
        String redisKey = String.format(REDIS_DATA_KEY_FORMAT, versionDate, type, cityId, category);
        
        // 3. 从 Redis 缓存中获取排行榜数据
        String rankStr = redisTemplate.opsForValue().get(redisKey);
        
        // 4. 缓存为空时返回空列表，避免空指针异常
        if (rankStr == null) {
            return List.of();
        }
        
        // 5. 将 JSON 字符串反序列化为对象列表并返回
        return JSON.parseObject(rankStr, new TypeReference<List<MerchantRankInfo>>() {});
    }

    /**
     * 刷新排行榜数据
     * <p>
     * 从数据库读取当天的排行榜数据，按维度分组后写入 Redis 缓存，
     * 同时更新版本号 Key，标记当前数据的日期版本。
     * </p>
     *
     * @param param 任务参数，由 XXL-Job 传入
     * @return 任务执行结果
     */
    @Override
    @Transactional
    public ReturnT<String> rankRefresh(String param) {
        // 1. 获取当前日期（格式：yyyy-MM-dd）
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = format.format(new Date());

        // 2. 构建查询 SQL，查询当天的排行榜数据
        String selectSQL = "select * from merchant_rank_info where date = '" + currentTime + "'";
        List<MerchantRankInfo> rankInfoList = merchantRankInfoMapper.listRankInfoBySQL(selectSQL);

        // 3. 如果查询结果为空，返回失败
        if (CollectionUtils.isEmpty(rankInfoList)) {
            return ReturnT.FAIL;
        }

        // 4. 按维度分组，每个维度对应一个 Redis Key
        Map<String, List<MerchantRankInfo>> rankInfoMap = rankInfoList.stream()
                .collect(Collectors.groupingBy(rankInfo ->
                        String.format(REDIS_DATA_KEY_FORMAT, currentTime, rankInfo.getType(), rankInfo.getCityId(), rankInfo.getCategory())
                ));

        // 5. 将每个维度的数据写入 Redis 缓存，过期时间为7天
        rankInfoMap.forEach((redisKey, redisValue) ->
                redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(redisValue), 7L, TimeUnit.DAYS)
        );

        // 6. 更新版本号 Key，标记当前数据日期
        redisTemplate.opsForValue().set(REDIS_VERSION_KEY, currentTime, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
        
        // 7. 返回成功
        return ReturnT.SUCCESS;
    }
}