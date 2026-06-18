package com.rank.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家排行榜信息实体类
 * <p>
 * 对应数据库表：merchant_rank_info，
 * 存储商家在不同维度下的排行榜数据，包括排名、销量等信息。
 * </p>
 */
@Data
@TableName("merchant_rank_info")
public class MerchantRankInfo {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 城市ID，000000表示全国
     */
    @TableField("city_id")
    private String cityId;

    /**
     * 榜单类型：0-爆款榜，1-飙升榜
     */
    @TableField("type")
    private Integer type;

    /**
     * 类目：0-全部，1-美食等
     */
    @TableField("category")
    private Integer category;

    /**
     * 商家ID，唯一标识一个商家
     */
    @TableField("merchant_id")
    private String merchantId;

    /**
     * 排名，数值越小排名越靠前
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 月销量，统计周期内的月销售数量
     */
    @TableField("sale_num_month")
    private Integer saleNumMonth;

    /**
     * 日销量，统计周期内的日销售数量
     */
    @TableField("sale_num_day")
    private Integer saleNumDay;

    /**
     * 统计日期，格式为 yyyy-MM-dd
     */
    @TableField("date")
    private String date;

    /**
     * 是否已删除：0-未删除，1-已删除
     * 使用 MyBatis-Plus 的逻辑删除注解
     */
    @TableField("is_delete")
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间，自动填充插入时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，自动填充插入和更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}