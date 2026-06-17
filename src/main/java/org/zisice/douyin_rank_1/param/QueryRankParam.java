package org.zisice.douyin_rank_1.param;

import lombok.Data;

/**
 * 排行榜查询参数类
 * <p>
 * 封装排行榜查询的条件参数，包括城市ID、榜单类型和类目。
 * </p>
 */
@Data
public class QueryRankParam {

    /**
     * 城市ID，000000表示全国
     */
    private String cityId;

    /**
     * 榜单类型：0-爆款榜，1-飙升榜
     */
    private Integer type;

    /**
     * 类目：0-全部，1-美食等
     */
    private Integer category;
}