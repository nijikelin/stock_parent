package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.InnerMarketDomain;
import com.itheima.stock.pojo.entity.StockMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author 1
* @description 针对表【stock_market_index_info(国内大盘数据详情表)】的数据库操作Mapper
* @createDate 2025-09-04 11:48:51
* @Entity com.itheima.stock.pojo.entity.StockMarketIndexInfo
*/
public interface StockMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockMarketIndexInfo record);

    int insertSelective(StockMarketIndexInfo record);

    StockMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockMarketIndexInfo record);

    int updateByPrimaryKey(StockMarketIndexInfo record);
    //根据id获取大盘数据
    List<InnerMarketDomain> getMarketInfo(@Param("marketIds") List<String> marketIds, @Param("timePoint") Date timePoint);

    /**
     * 比较T日和T-1日交易量
     * @param endDate 结束时间
     * @param startDate 开始时间
     * @param inner 大盘编码Id
     * @return
     */
    List<Map> getSumAmtInfo(@Param("endDate") Date endDate, @Param("startDate") Date startDate, @Param("inner") List<String> inner);


    int insertBatch(@Param("infos") List<StockMarketIndexInfo> list);
}
