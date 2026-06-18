package com.rank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.rank.common.entity.MerchantRankInfo;
import com.rank.service.RankService;

import java.util.List;

/**
 * 排行榜查询控制器
 * <p>
 * 提供排行榜数据的 REST API 接口，
 * 支持按城市、榜单类型、类目等维度查询排行榜列表。
 * </p>
 */
@RestController
@RequestMapping("/rank")
public class RankController {

    /**
     * 排行榜服务接口
     */
    @Autowired
    private RankService rankService;

    /**
     * 查询排行榜列表接口
     * <p>
     * 根据城市ID、榜单类型和类目查询对应的排行榜数据，
     * 数据从 Redis 缓存中读取，保证查询性能。
     * </p>
     *
     * @param cityId   城市ID，000000表示全国，默认为000000
     * @param type     榜单类型：0-爆款榜，1-飙升榜，默认为0
     * @param category 类目：0-全部，1-美食等，默认为0
     * @return 排行榜商家信息列表
     */
    @PostMapping("/list")
    public ResponseEntity<List<MerchantRankInfo>> getRankList(
            @RequestParam(value = "city_id", defaultValue = "000000") String cityId,
            @RequestParam(value = "type", defaultValue = "0") Integer type,
            @RequestParam(value = "category", defaultValue = "0") Integer category) {
        
        // 1. 直接调用服务层查询排行榜数据（参数透传）
        List<MerchantRankInfo> result = rankService.list(cityId, type, category);
        
        // 2. 返回查询结果
        return ResponseEntity.ok(result);
    }
}