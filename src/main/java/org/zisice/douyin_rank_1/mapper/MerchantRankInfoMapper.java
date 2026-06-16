package org.zisice.douyin_rank_1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.zisice.douyin_rank_1.entity.MerchantRankInfo;

import java.util.List;

public interface MerchantRankInfoMapper extends BaseMapper<MerchantRankInfo> {

    @Select("${sql}")
    List<MerchantRankInfo> listRankInfoBySQL(@Param("sql") String sql);
}
