package com.itheima.stock.controller;

import com.itheima.stock.pojo.domain.*;
import com.itheima.stock.pojo.entity.StockRtInfo;
import com.itheima.stock.service.StockService;
import com.itheima.stock.vo.resp.PageResult;
import com.itheima.stock.vo.resp.R;
import io.swagger.annotations.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author by itheima
 * @Date 2021/12/19
 * @Description
 */
@Api(value = "/api/quot", tags = {"其它省略....."})
@RestController
@RequestMapping("/api/quot")
public class StockController {

    @Autowired
    private StockService stockService;

	//其它省略.....
    /**
     * 获取国内最新大盘指数
     * @return
     */
    @ApiOperation(value = "获取国内最新大盘指数", notes = "获取国内最新大盘指数", httpMethod = "GET")
    @GetMapping("/index/all")
    public R<List<InnerMarketDomain>> innerIndexAll(){
        return stockService.innerIndexAll();
    }

    /**
     * 获取涨幅榜前十的数据
     * @return
     */
    @ApiOperation(value = "获取涨幅榜前十的数据", notes = "获取涨幅榜前十的数据", httpMethod = "GET")
    @GetMapping("/sector/all")
    public R<List<StockBlockDomain>> blockForTen(){
        return stockService.blockForTen();
    }

    /**
     * 分页查询个股数据
     * @param page 当前页
     * @param pageSize 每页大小
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "当前页"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页大小")
    })
    @ApiOperation(value = "分页查询个股数据", notes = "分页查询个股数据", httpMethod = "GET")
    @GetMapping("/stock/all")
    public R<PageResult<StockUpdownDomain>> getStockInfoByPage(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                                               @RequestParam(value = "pageSize",required = false,defaultValue = "20") Integer pageSize){
        return stockService.getStockInfoByPage(page,pageSize);
    }

    /**
     * 获取个股涨幅前4的数据涨幅榜
     * @return
     */
    @ApiOperation(value = "获取个股涨幅前4的数据", notes = "获取个股涨幅前4的数据", httpMethod = "GET")
    @GetMapping("/stock/increase")
    public R<List<StockUpdownDomain>> getStock4(){
        return stockService.getStock4();
    }

    /**
     * 获取每分钟涨停跌停的数据
     * @return
     */
    @ApiOperation(value = "获取每分钟涨停跌停的数据", notes = "获取每分钟涨停跌停的数据", httpMethod = "GET")
    @GetMapping("/stock/updown/count")
    public R<Map<String, List>> getUpDownTing(){
        return stockService.getUpDownTing();
    }

    /**
     * 导出当前页的股票信息
     * @param page 当前页
     * @param pageSize 每页大小
     * @param response
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "当前页"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页大小")
    })
    @ApiOperation(value = "导出当前页的股票信息", notes = "导出当前页的股票信息", httpMethod = "GET")
    @GetMapping("/stock/export")
    public void stockExport(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                            @RequestParam(value = "pageSize",required = false,defaultValue = "20") Integer pageSize,
                            HttpServletResponse response){
        stockService.stockExport(page,pageSize,response);
    }

    /**
     * 比较T日和T-1日的交易量
     * @return
     */
    @ApiOperation(value = "比较T日和T-1日的交易量", notes = "比较T日和T-1日的交易量", httpMethod = "GET")
    @GetMapping("/stock/tradeAmt")
    public R<Map<String,List<Map>>> stockTrade(){
        return stockService.stockTrade();
    }

    /**
     * 当前时间个股涨幅统计
     * @return
     */
    @ApiOperation(value = "当前时间个股涨幅统计", notes = "当前时间个股涨幅统计", httpMethod = "GET")
    @GetMapping("/stock/updown")
    public R<Map<String,Object>> stockUpDownCount(){
        return stockService.stockUpDownCount();
    }

    /**
     * 查询分时股票情况
     * @param code 股票编码
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "code", value = "股票编码", required = true)
    })
    @ApiOperation(value = "查询分时股票情况", notes = "查询分时股票情况", httpMethod = "GET")
    @GetMapping("/stock/screen/time-sharing")
    public R<List<Stock4MinuteDomain>> getStock4Minute(@RequestParam(value = "code",required = true) String code){
        return stockService.getStock4Minute(code);
    }

    /**
     *统计某只股票的日K线
     * @param code 股票代码
     * @return
     */
    @GetMapping("/screen/dkline")
    public R<List<Stock4EvrDayDomain>> getStock4Day(@RequestParam(value = "code", required = true) String code){
        return stockService.getStock4Day(code);
    }

    /**
     * 获取外盘信息
     * @return
     */
    @GetMapping("/external/index")
    public R<List<OuterMarketDomain>> getOuterMarket4(){
        return stockService.getOuterMarket4();
    }

    /**
     * 搜索个股信息
     * @param searchStr 查询股票的代码
     * @return
     */
    @GetMapping("/stock/search")
    public R<List<StockSearchDomain>> getSearchInfo(String searchStr){
        return stockService.getSearchInfo(searchStr);
    }

    /**
     * 获取个股描述
     * @param code 股票代码
     * @return
     */
    @GetMapping("/stock/describe")
    public R<StockDescribeDomain> getDescribe(String code){
        return stockService.getDescribe(code);
    }

    @GetMapping("/stock/screen/second/detail")
    public R<StockSecondDomain> getSecondDetail(String code){
        return stockService.getSecondDetail(code);
    }

    @GetMapping("/stock/screen/second")
    public R<List<Map<String,Object>>> getScreenSecond(String code){
        return stockService.getScreenSecond(code);
    }
}