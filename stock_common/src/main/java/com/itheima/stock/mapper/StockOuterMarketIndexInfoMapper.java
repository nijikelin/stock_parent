package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.OuterMarketDomain;
import com.itheima.stock.pojo.entity.StockOuterMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author 1
* @description 针对表【stock_outer_market_index_info(外盘详情信息表)】的数据库操作Mapper
* @createDate 2025-09-04 11:48:51
* @Entity com.itheima.stock.pojo.entity.StockOuterMarketIndexInfo
*/
public interface StockOuterMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockOuterMarketIndexInfo record);

    int insertSelective(StockOuterMarketIndexInfo record);

    StockOuterMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockOuterMarketIndexInfo record);

    int updateByPrimaryKey(StockOuterMarketIndexInfo record);

    List<OuterMarketDomain> getOuterMarket(@Param("curTime") Date curTime);

    int insertBatch(@Param("list") List<OuterMarketDomain> list);
}
