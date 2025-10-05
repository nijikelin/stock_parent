package com.itheima.stock.sharding;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.ShardingValue;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author nijikelin
 * @Date 2025/9/26 19:26
 * @Description 公共分库策略 根据日期分库，片键的类型是Date
 */

public class CommonAlg4Db implements PreciseShardingAlgorithm<Date>, RangeShardingAlgorithm<Date> {

    /**
     * 精准查询时走该方法，查询条件是cur_Time
     * @param dsNames 所有的数据源
     * @param shardingValue
     * @return
     */
    @Override
    public String doSharding(Collection<String> dsNames, PreciseShardingValue<Date> shardingValue) {
        //获取逻辑表
        String logicTableName = shardingValue.getLogicTableName();
        //获取分片键
        String columnName = shardingValue.getColumnName();
        //获取等值查询的条件值
        Date curTime = shardingValue.getValue();

        String year = new DateTime(curTime).getYear()+"";

        Optional<String> result = dsNames.stream().filter(dsName -> dsName.endsWith(year)).findFirst();
        return result.orElse(null);
    }

    /**
     *
     * @param dsNames 所有数据源
     * @param shardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> dsNames, RangeShardingValue<Date> shardingValue) {

        String logicTableName = shardingValue.getLogicTableName();
        String columnName = shardingValue.getColumnName();
        Range<Date> timeRange = shardingValue.getValueRange();
        if (timeRange.hasLowerBound()) {
            Date lowerEndpoint = timeRange.lowerEndpoint();
            int startYear = new DateTime(lowerEndpoint).getYear();
            dsNames = dsNames.stream().filter(dsName -> Integer.parseInt(dsName.substring(dsName.lastIndexOf("-") + 1)) >= startYear).collect(Collectors.toList());
        }

        if (timeRange.hasUpperBound()) {
            Date upperEndpoint = timeRange.upperEndpoint();
            int endYear = new DateTime(upperEndpoint).getYear();
            dsNames = dsNames.stream().filter(dsName -> Integer.parseInt(dsName.substring(dsName.lastIndexOf("-") + 1)) <= endYear).collect(Collectors.toList());
        }
        return dsNames;
    }
}
