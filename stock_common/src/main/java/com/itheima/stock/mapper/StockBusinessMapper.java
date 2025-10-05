package com.itheima.stock.mapper;

import com.itheima.stock.pojo.domain.InnerMarketDomain;
import com.itheima.stock.pojo.domain.StockDescribeDomain;
import com.itheima.stock.pojo.entity.StockBusiness;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 1
* @description 针对表【stock_business(主营业务表)】的数据库操作Mapper
* @createDate 2025-09-04 11:48:51
* @Entity com.itheima.stock.pojo.entity.StockBusiness
*/
public interface StockBusinessMapper {

    int deleteByPrimaryKey(String id);

    int insert(StockBusiness record);

    int insertSelective(StockBusiness record);

    StockBusiness selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StockBusiness record);

    int updateByPrimaryKey(StockBusiness record);

    /**
     * 获取所有A股个股编码集合
     * @return
     */
    List<String> getAllStockCodes();

    StockDescribeDomain getDescribe(@Param("code") String code);
}
