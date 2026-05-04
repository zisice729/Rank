package org.zisice.douyin_rank_1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 */
@Slf4j
@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    /**
     * 查询排行榜列表
     *
     * @param cityId   城市ID，000000表示全国，默认为000000
     * @param type     榜单类型：0-爆款，1-飙升，默认为0
     * @param category 类目：0-全部，1-美食等，默认为0
     * @return 排行榜列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<MerchantRankInfo>> getRankList(
            @RequestParam(value = "city_id", defaultValue = "000000") String cityId,
            @RequestParam(value = "type", defaultValue = "0") Integer type,
            @RequestParam(value = "category", defaultValue = "0") Integer category) {

        log.info("接收到排行榜查询请求 - cityId:{}, type:{}, category:{}", cityId, type, category);

        // 构建查询参数
        QueryRankParam param = new QueryRankParam();
        param.setCityId(cityId);
        param.setType(type);
        param.setCategory(category);

        // 查询排行榜数据
        List<MerchantRankInfo> result = rankService.list(param);

        log.info("排行榜查询完成 - 返回数据条数:{}", result.size());

        return ResponseEntity.ok(result);
    }
}
