package com.itheima.stock.service;

import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.pojo.entity.StockRtInfo;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface StockService {
    R<List<InnerMarketDomain>> innerIndexAll();

    R<List<StockBlockDomain>> blockForTen();

    R<PageResult<StockUpdownDomain>> getStockInfoByPage(Integer page, Integer pageSize);

    R<List<StockUpdownDomain>> getStock4();

    R<Map<String, List>> getUpDownTing();
    /**
     * 导出当前页的股票信息
     * @param page 当前页
     * @param pageSize 每页大小
     * @param response
     */
    void stockExport(Integer page, Integer pageSize, HttpServletResponse response);

    /**
     * 比较T日和T-1日的交易量
     * @return
     */
    R<Map<String, List<Map>>> stockTrade();

    /**
     * 当前时间个股涨幅统计
     * @return
     */
    R<Map<String,Object>> stockUpDownCount();

    /**
     * 查询分时股票情况
     * @param code 股票编码
     * @return
     */
    R<List<Stock4MinuteDomain>> getStock4Minute(String code);

    R<List<Stock4EvrDayDomain>> getStock4Day(String code);

    R<List<OuterMarketDomain>> getOuterMarket4();

    R<List<StockSearchDomain>> getSearchInfo(String code);

    R<StockDescribeDomain> getDescribe(String code);

    R<StockSecondDomain> getSecondDetail(String code);

    R<List<Map<String,Object>>> getScreenSecond(String code);
}
