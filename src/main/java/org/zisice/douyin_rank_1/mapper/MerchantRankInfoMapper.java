package org.zisice.douyin_rank_1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.zisice.douyin_rank_1.entity.MerchantRankInfo;

import java.util.List;

/**
 * 商家排行榜信息数据访问层
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper，提供基础的 CRUD 操作，
 * 同时扩展自定义的 SQL 查询方法。
 * </p>
 */
public interface MerchantRankInfoMapper extends BaseMapper<MerchantRankInfo> {

    /**
     * 执行自定义 SQL 查询排行榜数据
     * <p>
     * 使用动态 SQL 方式查询商家排行榜信息，
     * 支持按日期等条件进行灵活查询。
     * </p>
     *
     * @param sql 自定义 SQL 语句
     * @return 商家排行榜信息列表
     */
    @Select("${sql}")
    List<MerchantRankInfo> listRankInfoBySQL(@Param("sql") String sql);
}