package com.itheima.stock.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.itheima.stock.mapper.*;
import com.itheima.stock.pojo.domain.OuterMarketDomain;
import com.itheima.stock.pojo.entity.StockBlockRtInfo;
import com.itheima.stock.pojo.entity.StockMarketIndexInfo;
import com.itheima.stock.pojo.entity.StockRtInfo;
import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.service.StockTimerTaskService;
import com.itheima.stock.utils.DateTimeUtil;
import com.itheima.stock.utils.IdWorker;
import com.itheima.stock.utils.ParserStockInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName StockTimerTaskServiceImpl
 * @Description TODO:获取股票实时数据实现类
 * @Author 1
 * @Date 2025/9/15 16:34
 * @Version 1.0
 */
@Slf4j
@Service
public class StockTimerTaskServiceImpl implements StockTimerTaskService {

    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;

    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;

    private HttpEntity entity;

    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;

    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private StockOuterMarketIndexInfoMapper stockOuterMarketIndexInfoMapper;

    @Override
    public void getInnerMarketInfo() {

        //1.阶段1；采集原始数据
        //1.1构造url
        String url = stockInfoConfig.getMarketUrl()+String.join(",",stockInfoConfig.getInner());

//        //1.2构造请求头

        //1.3发起请求
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        int statusCodeValue = exchange.getStatusCodeValue();
        if (statusCodeValue !=200) {
            log.error("当前时间为{},状态码为{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),statusCodeValue);
            return;
        }
        String jsData = exchange.getBody();
        log.info("当前时间为{},采集数据为{}",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),jsData);

        //2.阶段2：利用java正则解析数据

        List<StockMarketIndexInfo> list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, 1);

        //4.阶段4：利用MyBatis批量入库
        int count = stockMarketIndexInfoMapper.insertBatch(list);
        if (count>0) {
            log.info("当前时间{},插入数据{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
            rabbitTemplate.convertAndSend("stockExchange","inner.market",new Date());
        }else{
            log.error("当前时间{},插入数据失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        }

    }

    @Override
    public void getStockRtIndex() {
        List<String> allStockCodes = stockBusinessMapper.getAllStockCodes();
        allStockCodes = allStockCodes.stream().map(code->code.startsWith("6")?"sh"+code:"sz"+code).collect(Collectors.toList());
//        System.out.println(allStockCodes);
        List<List<String>> allCodes = Lists.partition(allStockCodes, 15);
        allCodes.forEach(codes->{
//            threadPoolTaskExecutor.execute(()->{
                String url = stockInfoConfig.getMarketUrl()+String.join(",",codes);

                //1.3发起请求
                ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                int statusCodeValue = exchange.getStatusCodeValue();
                if (statusCodeValue !=200) {
                    log.error("当前时间为{},状态码为{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),statusCodeValue);
                    return;
                }
                String jsData = exchange.getBody();
                List<StockRtInfo> list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, 3);
                //4.阶段4：利用MyBatis批量入库
                int count = stockRtInfoMapper.insertBatch(list);
                if (count>0) {
                    log.info("当前时间{},插入数据{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
                }else{
                    log.error("当前时间{},插入数据失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                }
//            });

        });

    }

    @Override
    public void getStockBlockRtInfo() {

        //组装url
        String url = stockInfoConfig.getBlockUrl();
        //发送请求
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        int statusCodeValue = exchange.getStatusCodeValue();
        if (statusCodeValue !=200) {
            log.error("当前时间为{},状态码为{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),statusCodeValue);
            return;
        }
        //获取板块数据
        String jsData = exchange.getBody();
        //解析数据
        List<StockBlockRtInfo> collect = parserStockInfoUtil.parse4StockBlock(jsData);

        //利用Mybatis批量保存在数据库中
        int count = stockBlockRtInfoMapper.insertBatch(collect);
        if (count>0) {
            log.info("当前时间{},插入数据{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),collect);
        }else{
            log.error("当前时间{},插入数据失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        }


    }

    @Override
    public void getOuterMarketInfo() {
        String url = stockInfoConfig.getMarketUrl()+String.join(",",stockInfoConfig.getOuter());

        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        int statusCodeValue = exchange.getStatusCodeValue();
        if (statusCodeValue !=200) {
            log.error("当前时间为{},状态码为{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),statusCodeValue);
            return;
        }
        String jsString = exchange.getBody();
        List<OuterMarketDomain> list = parserStockInfoUtil.parser4StockOrMarketInfo(jsString, 2);
        int count = stockOuterMarketIndexInfoMapper.insertBatch(list);
        if (count>0) {
            log.info("当前时间{},插入数据{}成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),list);
            rabbitTemplate.convertAndSend("stockExchange","inner.market",new Date());
        }else{
            log.error("当前时间{},插入数据失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        }


    }

    /**
     * 构造请求头,使用生命周期函数初始化
     * @return
     */
    @PostConstruct
    private HttpEntity initData(){
        //1.2构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Referer","https://finance.sina.com.cn/stock/");
        headers.add("UserAgent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36");

        //维护http请求实体对象
        entity = new HttpEntity<>(headers);
        return entity;
    }
}
