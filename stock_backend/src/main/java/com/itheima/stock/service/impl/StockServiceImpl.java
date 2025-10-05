package com.itheima.stock.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.stock.mapper.*;
import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.pojo.entity.StockOuterMarketIndexInfo;
import com.itheima.stock.pojo.entity.StockRtInfo;
import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.service.StockService;
import com.itheima.stock.utils.DateTimeUtil;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockInfoConfig stockInfoConfig;

    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;

    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;

    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;

    @Autowired
    private StockOuterMarketIndexInfoMapper stockOuterMarketIndexInfoMapper;

    @Autowired
    private Cache<String,Object> caffeineCache;

    @Autowired
    private StockBusinessMapper stockBusinessMapper;
    @Override
    public R<List<InnerMarketDomain>> innerIndexAll() {
        //从缓存中加载数据，如果不存在，则走补偿策略获取数据，并存入本地缓存
        R<List<InnerMarketDomain>> innerMarketInfos = (R<List<InnerMarketDomain>>) caffeineCache.get("innerMarketInfos", key -> {
            DateTime lastDateMinus1Min = DateTime.now().minusMinutes(1);
            DateTime lastDate = DateTimeUtil.getLastMarketDate4Stock(lastDateMinus1Min);
            // 3. 转为 java.util.Date 类型（如果需要与旧 API 兼容）
            Date curDateTime = lastDate.toDate();
//            curDateTime =DateTime.parse("2025-09-19 15:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
            //2.获取大盘编码
            List<String> mCode = stockInfoConfig.getInner();
            //3.查询数据
            List<InnerMarketDomain> marketInfo = stockMarketIndexInfoMapper.getMarketInfo(mCode, curDateTime);
            //4.响应
            return R.ok(marketInfo);
        });
        return innerMarketInfos;

    }

    @Override
    public R<List<StockBlockDomain>> blockForTen() {
        Date curDateTime = DateTimeUtil.getDateTimeWithoutSecond(DateTime.now()).toDate();
        List<StockBlockDomain> blockForTen = stockBlockRtInfoMapper.findBlockForTen(curDateTime);
        return R.ok(blockForTen);
    }

    @Override
    public R<PageResult<StockUpdownDomain>> getStockInfoByPage(Integer page, Integer pageSize) {
        //0.设置pagehelper分页参数
        PageHelper.startPage(page, pageSize);
        //1.获取最新的股票交易时间点
        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //2.根据时间点查询涨幅榜降序排名的数据
        List<StockUpdownDomain> stockInfo = stockRtInfoMapper.getNewsStockInfo(curDate);
        //3.判断获取的集合不为空
        if (CollectionUtil.isEmpty(stockInfo)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        //4.组装pageinfo对象
        PageInfo<StockUpdownDomain> pageInfo = new PageInfo<>(stockInfo);
        PageResult<StockUpdownDomain> result = new PageResult<>(pageInfo);

        return R.ok(result);
    }

    @Override
    public R<List<StockUpdownDomain>> getStock4() {

        DateTime lastDateMinus1Min = DateTime.now().minusMinutes(1);
        DateTime lastDate = DateTimeUtil.getLastDate4Stock(lastDateMinus1Min);
        // 3. 转为 java.util.Date 类型（如果需要与旧 API 兼容）
        Date lastDateTime = lastDate.toDate();
        List<StockUpdownDomain> stockList = stockRtInfoMapper.getStock4(lastDateTime);
        return R.ok(stockList);
    }

    @Override
    public R<Map<String, List>> getUpDownTing() {
        //1.获取最新的交易时间范围openTime curTime
        //1.1获取最新的股票交易时间点
        DateTime curDate = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curDateTime = curDate.toDate();

        //1.2获取最新交易时间对应的开盘时间
        Date openDate = DateTimeUtil.getOpenDate(curDate).toDate();
        //2.查询涨停数据
        List<Map> upList = stockRtInfoMapper.getUpDownCount(curDateTime, openDate, 1);
        //3.查询跌停数据
        List<Map> downList = stockRtInfoMapper.getUpDownCount(curDateTime, openDate, 0);
        //4.组装数据
        Map<String, List> mapInfo = new HashMap<>();
        mapInfo.put("upList", upList);
        mapInfo.put("downList", downList);

        //5.返回结果
        return R.ok(mapInfo);
    }

    /**
     * 导出当前页的股票信息
     *
     * @param page     当前页
     * @param pageSize 每页大小
     * @param response
     */
    @Override
    public void stockExport(Integer page, Integer pageSize, HttpServletResponse response) {
        //1.获取分页数据
        R<PageResult<StockUpdownDomain>> r = this.getStockInfoByPage(page, pageSize);
        List<StockUpdownDomain> rows = r.getData().getRows();
        //2.将数据导出到excel
        response.setContentType("application/ms-excel");
        response.setCharacterEncoding("utf-8");
        try {
            String fileName = URLEncoder.encode("股票信息表", "utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), StockUpdownDomain.class)
                    .sheet("股票涨幅信息")
                    .doWrite(rows);
        } catch (IOException e) {
            log.info("当前导出数据异常，当前页：{},每页大小：{},异常信息：{}",
                    page,pageSize,e.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            R<Object> error = R.error(ResponseCode.ERROR);
            try {
                String jsonData = new ObjectMapper().writeValueAsString(error);
                response.getWriter().write(jsonData);
            } catch (IOException ioException) {
                log.error("stockExport:响应错误信息失败");
            }
        }


    }
    /**
     * 比较T日和T-1日的交易量
     * @return
     */
    @Override
    public R<Map<String, List<Map>>> stockTrade() {

        //1.1 获取T日的最新交易时间和开始时间
        DateTime lastDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        DateTime openDateTime = DateTimeUtil.getOpenDate(lastDateTime);
        Date lastDate = lastDateTime.toDate();
        Date openDate = openDateTime.toDate();


        //1.2 获取T-1日交易时间和开始时间
        DateTime preLastDateTime = DateTimeUtil.getPreviousTradingDay(lastDateTime);
        DateTime preOpenDateTime = DateTimeUtil.getOpenDate(preLastDateTime);
        //转化成java中Date,这样jdbc默认识别
        Date startTime4PreT = preOpenDateTime.toDate();
        Date endTime4PreT=preLastDateTime.toDate();



        //2 调用mapper查询
        List<Map> tData = stockMarketIndexInfoMapper.getSumAmtInfo(lastDate,openDate,stockInfoConfig.getInner());
        List<Map> tpreData = stockMarketIndexInfoMapper.getSumAmtInfo(endTime4PreT,startTime4PreT,stockInfoConfig.getInner());
        //3 组装数据
        HashMap<String, List<Map>> info = new HashMap<>();
        info.put("amtList",tData);
        info.put("yesAmtList",tpreData);

        return R.ok(info);
    }

    /**
     *
     * @return
     */
    @Override
    public R<Map<String,Object>> stockUpDownCount() {
        //1 获取股票最新交易时间
        DateTime dateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curDate = dateTime.toDate();


        List<Map> upDownCount= stockRtInfoMapper.getstockUpDownCount(curDate);
        List<String> upDownRange = stockInfoConfig.getUpDownRange();

//        方式1：普通循环
//        List<Map> titleCountList = new ArrayList<>();
//        for (String title : upDownRange) {
//            Map tmp =null;
//            for (Map titleCount : upDownCount) {
//                if(titleCount.containsValue(title)){
//                    tmp = titleCount;
//                    break;
//                }
//            }
//            if(tmp == null){
//                tmp = new HashMap<>();
//                tmp.put("title",title);
//                tmp.put("count",0);
//            }
//            titleCountList.add(tmp);
//        }
        //方式2：使用lambda表达式指定
        List<Map> list = upDownRange.stream().map(title -> {
            Map mp = null;
            Optional<Map> optionalMap = upDownCount.stream()
                    .filter(m ->
                    m.containsValue(title))
                    .findFirst();
            if (optionalMap.isPresent()) {
                mp = optionalMap.get();
            } else {
                mp = new HashMap<>();
                mp.put("title", title);
                mp.put("count", 0);
            }
            return mp;
        }).collect(Collectors.toList());

        String curDateStr = new DateTime(curDate).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        HashMap<String, Object> map = new HashMap<>();
        map.put("time",curDateStr);
        map.put("infos",list);
        return R.ok(map);
    }

    /**
     * 查询分时股票情况
     * @param code 股票编码
     * @return
     */
    @Override
    public R<List<Stock4MinuteDomain>> getStock4Minute(String code) {
        DateTime lastDateMinus1Min = DateTime.now().minusMinutes(1);
        DateTime lastDate = DateTimeUtil.getLastDate4Stock(lastDateMinus1Min);
        // 3. 转为 java.util.Date 类型（如果需要与旧 API 兼容）
        Date lastDateTime = lastDate.toDate();
        DateTime openDateTime = DateTimeUtil.getOpenDate(lastDate);
        Date openDate = openDateTime.toDate();

        List<Stock4MinuteDomain> stock4Minute = stockRtInfoMapper.getStock4Minute(lastDateTime,openDate,code);
        return R.ok(stock4Minute);
    }
    /**
     *统计某只股票的日K线
     * @param code 股票代码
     * @return
     */
    @Override
    public R<List<Stock4EvrDayDomain>> getStock4Day(String code) {
        DateTime endtDate = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date endDateTime = endtDate.toDate();

        DateTime startDate = endtDate.minusMonths(3);
        Date startDateTime = startDate.toDate();

        List<Stock4EvrDayDomain> list = stockRtInfoMapper.getStock4EveDay(code,startDateTime,endDateTime);
        return R.ok(list);
    }

    /**
     * 获取外盘信息
     * @return
     */
    @Override
    public R<List<OuterMarketDomain>> getOuterMarket4() {
        Date curDateTime = DateTimeUtil.getDateTimeWithoutSecond(DateTime.now()).toDate();
        List<OuterMarketDomain> list = stockOuterMarketIndexInfoMapper.getOuterMarket(curDateTime);
        return R.ok(list);
    }

    /**
     * 模糊查询股票编码及股票名称
     * @param code
     * @return
     */
    @Override
    public R<List<StockSearchDomain>> getSearchInfo(String code) {
        List<StockSearchDomain> list = stockRtInfoMapper.getSearchInfo("%"+code+"%");
        return R.ok(list);
    }

    /**
     * 获取股票描述
     * @param code
     * @return
     */
    @Override
    public R<StockDescribeDomain> getDescribe(String code) {
        StockDescribeDomain stockDescribe = stockBusinessMapper.getDescribe(code);
        return R.ok(stockDescribe);
    }

    /**
     * 获取个股最新分时行情数据
     * @param code 股票编码
     * @return
     */
    @Override
    public R<StockSecondDomain> getSecondDetail(String code) {

        DateTime lastDateMinus1Min = DateTime.now().minusMinutes(1);
        DateTime lastDate = DateTimeUtil.getLastDate4Stock(lastDateMinus1Min);
        // 3. 转为 java.util.Date 类型（如果需要与旧 API 兼容）
        Date lastDateTime = lastDate.toDate();
        StockSecondDomain secondDetail= stockRtInfoMapper.getSecondDetail(code,lastDateTime);
        return R.ok(secondDetail);
    }


    /**
     * 个股交易流水行情数据查询--查询最新交易流水，按照交易时间降序取前10
     * @param code
     * @return
     */
    @Override
    public R<List<Map<String,Object>>> getScreenSecond(String code) {

         List<Map<String,Object>>  list = stockRtInfoMapper.getScreenSecond(code);
        return R.ok(list);
    }

}

