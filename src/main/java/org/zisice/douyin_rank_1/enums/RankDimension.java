package org.zisice.douyin_rank_1.enums;

/**
 * 排行榜维度枚举
 * <p>
 * 定义系统支持的排行榜维度组合，
 * 每个枚举值包含城市ID、榜单类型和类目三个维度信息。
 * </p>
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

    /**
     * 城市ID
     */
    private final String cityId;

    /**
     * 榜单类型：0-爆款榜，1-飙升榜
     */
    private final Integer type;

    /**
     * 类目：0-全部，1-美食等
     */
    private final Integer category;

    /**
     * 构造函数
     *
     * @param cityId   城市ID
     * @param type     榜单类型
     * @param category 类目
     */
    RankDimension(String cityId, Integer type, Integer category) {
        this.cityId = cityId;
        this.type = type;
        this.category = category;
    }

    /**
     * 获取城市ID
     *
     * @return 城市ID
     */
    public String getCityId() {
        return cityId;
    }

    /**
     * 获取榜单类型
     *
     * @return 榜单类型
     */
    public Integer getType() {
        return type;
    }

    /**
     * 获取类目
     *
     * @return 类目
     */
    public Integer getCategory() {
        return category;
    }
}