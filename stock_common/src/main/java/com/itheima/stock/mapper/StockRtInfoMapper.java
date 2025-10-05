package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.pojo.entity.StockRtInfo;
import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author 1
* @description 针对表【stock_rt_info(个股详情信息表)】的数据库操作Mapper
* @createDate 2025-09-04 11:48:51
* @Entity com.itheima.stock.pojo.entity.StockRtInfo
*/
public interface StockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockRtInfo record);

    int insertSelective(StockRtInfo record);

    StockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockRtInfo record);

    int updateByPrimaryKey(StockRtInfo record);

    List<StockUpdownDomain> getNewsStockInfo(@Param("curDate") Date curDate);

    List<StockUpdownDomain> getStock4(@Param("curDate") Date curDate);


    List<Map> getUpDownCount(@Param("curDate") Date curDate, @Param("openDate") Date openDate, @Param("flag") int flag);

    List<Map> getstockUpDownCount(@Param("curDate") Date curDate);

    List<Stock4MinuteDomain> getStock4Minute(@Param("lastDateTime") Date lastDateTime, @Param("openDateTime") Date openDateTime, @Param("code") String code);

    /**
     *统计某只股票的日K线
     * @param code 股票代码
     * @return
     */
    List<Stock4EvrDayDomain> getStock4EveDay(@Param("code") String code, @Param("startDateTime") Date startDateTime, @Param("endDateTime") Date endDateTime);

    int insertBatch(@Param("list") List<StockRtInfo> list);

    List<StockSearchDomain> getSearchInfo(@Param("code") String code);

    StockSecondDomain getSecondDetail(@Param("code") String code, @Param("curTime") Date lastDateTime);


    List<Map<String, Object>> getScreenSecond(@Param("code") String code);
}
