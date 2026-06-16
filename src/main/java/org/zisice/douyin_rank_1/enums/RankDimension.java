package org.zisice.douyin_rank_1.enums;

/**
 * 排行榜维度枚举
 */
public enum RankDimension {

    /**
     * 全国+全部类目+爆款榜
     */
    NATIONAL_HOT("000000", 0, 0),

    /**
     * 全国+全部类目+飙升榜
     */
    NATIONAL_RISING("000000", 1, 0),

    /**
     * 北京+美食+爆款榜
     */
    BEIJING_FOOD_HOT("100000", 0, 1),

    /**
     * 上海+全部类目+爆款榜
     */
    SHANGHAI_HOT("310000", 0, 0),

    /**
     * 广州+全部类目+爆款榜
     */
    GUANGZHOU_HOT("440100", 0, 0),

    /**
     * 深圳+全部类目+爆款榜
     */
    SHENZHEN_HOT("440300", 0, 0);

    private final String cityId;
    private final Integer type;
    private final Integer category;

    RankDimension(String cityId, Integer type, Integer category) {
        this.cityId = cityId;
        this.type = type;
        this.category = category;
    }

    public String getCityId() {
        return cityId;
    }

    public Integer getType() {
        return type;
    }

    public Integer getCategory() {
        return category;
    }
}
