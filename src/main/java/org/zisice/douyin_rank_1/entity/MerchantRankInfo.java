package org.zisice.douyin_rank_1.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家排行榜信息实体类
 * 对应数据库表：merchant_rank_info
 */
@Data
@TableName("merchant_rank_info")
public class MerchantRankInfo {

    /**
     * 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 城市ID，000000表示全国
     */
    @TableField("city_id")
    private String cityId;

    /**
     * 榜单类型：0-爆款，1-飙升
     */
    @TableField("type")
    private Integer type;

    /**
     * 类目：0-全部，1-美食等
     */
    @TableField("category")
    private Integer category;

    /**
     * 商家ID
     */
    @TableField("merchant_id")
    private String merchantId;

    /**
     * 排名
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 月销量
     */
    @TableField("sale_num_month")
    private Integer saleNumMonth;

    /**
     * 日销量
     */
    @TableField("sale_num_day")
    private Integer saleNumDay;

    /**
     * 统计日期
     */
    @TableField("date")
    private String date;

    /**
     * 是否已删除：0-未删除，1-已删除
     */
    @TableField("is_delete")
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}