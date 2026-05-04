package org.zisice.douyin_rank_1.param;

import lombok.Data;

/**
 * 排行榜查询参数
 */
@Data
public class QueryRankParam {

    /**
     * 城市ID，000000表示全国
     */
    private String cityId;

    /**
     * 榜单类型：0-爆款，1-飙升
     */
    private Integer type;

    /**
     * 类目：0-全部，1-美食等
     */
    private Integer category;
}
