package org.zisice.douyin_rank_1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.zisice.douyin_rank_1.entity.MerchantRankInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商家排行榜信息 Mapper 接口
 */
public interface MerchantRankInfoMapper extends BaseMapper<MerchantRankInfo> {

    /**
     * 根据SQL查询排行榜数据
     *
     * @param selectSQL 查询SQL
     * @return 排行榜列表
     */
    @Select("${selectSQL}")
    List<MerchantRankInfo> listRankInfoBySQL(@Param("selectSQL") String selectSQL);

    /**
     * 查询排行榜列表
     *
     * @param cityId   城市ID，000000表示全国
     * @param type     榜单类型：0-爆款，1-飙升
     * @param category 类目：0-全部，1-美食等
     * @param date     统计日期
     * @return 排行榜列表（按排名升序）
     */
    List<MerchantRankInfo> queryRankList(
            @Param("cityId") String cityId,
            @Param("type") Integer type,
            @Param("category") Integer category,
            @Param("date") String date
    );
}
