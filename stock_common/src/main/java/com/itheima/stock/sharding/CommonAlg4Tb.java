package com.itheima.stock.sharding;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author nijikelin
 * @Date 2025/9/26 19:26
 * @Description 分表策略 根据日期分表，片键的类型是Date
 */

public class CommonAlg4Tb implements PreciseShardingAlgorithm<Date>, RangeShardingAlgorithm<Date> {

    /**
     * 精准查询时走该方法，查询条件是cur_Time
     * @param tbNames 逻辑表所有的分表
     * @param shardingValue
     * @return
     */
    @Override
    public String doSharding(Collection<String> tbNames, PreciseShardingValue<Date> shardingValue) {
        //获取逻辑表
        String logicTableName = shardingValue.getLogicTableName();
        //获取分片键
        String columnName = shardingValue.getColumnName();
        //获取等值查询的条件值
        Date curTime = shardingValue.getValue();

        String yearMonth = new DateTime(curTime).toString(DateTimeFormat.forPattern("yyyyMM"));

        Optional<String> result = tbNames.stream().filter(tbName -> tbName.endsWith(yearMonth)).findFirst();
        return result.orElse(null);
    }

    /**
     *
     * @param tbNames 逻辑表下的所有分表
     * @param shardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> tbNames, RangeShardingValue<Date> shardingValue) {

        String logicTableName = shardingValue.getLogicTableName();
        String columnName = shardingValue.getColumnName();
        Range<Date> timeRange = shardingValue.getValueRange();
        if (timeRange.hasLowerBound()) {
            Date lowerEndpoint = timeRange.lowerEndpoint();
            String lowerYearMonth = new DateTime(lowerEndpoint).toString(DateTimeFormat.forPattern("yyyyMM"));
            Integer startYearMonth = Integer.valueOf(lowerYearMonth);
            tbNames = tbNames.stream().filter(tbName -> Integer.parseInt(tbName.substring(tbName.lastIndexOf("_") + 1)) >= startYearMonth).collect(Collectors.toList());
        }

        if (timeRange.hasUpperBound()) {
            Date upperEndpoint = timeRange.upperEndpoint();
            String upperYearMonth = new DateTime(upperEndpoint).toString(DateTimeFormat.forPattern("yyyyMM"));
            int endYearMonth =Integer.valueOf(upperYearMonth);
            tbNames = tbNames.stream().filter(tbName -> Integer.parseInt(tbName.substring(tbName.lastIndexOf("_") + 1)) <= endYearMonth).collect(Collectors.toList());
        }
        return tbNames;
    }
}
