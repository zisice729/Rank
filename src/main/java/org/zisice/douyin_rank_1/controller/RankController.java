package org.zisice.douyin_rank_1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zisice.douyin_rank_1.entity.MerchantRankInfo;
import org.zisice.douyin_rank_1.param.QueryRankParam;
import org.zisice.douyin_rank_1.service.RankService;

import java.util.List;

/**
 * 排行榜查询控制器
 * <p>
 * 提供排行榜数据的 REST API 接口，
 * 支持按城市、榜单类型、类目等维度查询排行榜列表。
 * </p>
 */
@Slf4j
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
    @GetMapping("/list")
    public ResponseEntity<List<MerchantRankInfo>> getRankList(
            @RequestParam(value = "city_id", defaultValue = "000000") String cityId,
            @RequestParam(value = "type", defaultValue = "0") Integer type,
            @RequestParam(value = "category", defaultValue = "0") Integer category) {

        log.info("接收到排行榜查询请求 - cityId:{}, type:{}, category:{}", cityId, type, category);

        // 构建查询参数对象
        QueryRankParam param = new QueryRankParam();
        param.setCityId(cityId);
        param.setType(type);
        param.setCategory(category);

        // 调用服务层查询排行榜数据
        List<MerchantRankInfo> result = rankService.list(param);

        log.info("排行榜查询完成 - 返回数据条数:{}", result.size());

        // 返回查询结果
        return ResponseEntity.ok(result);
    }
}