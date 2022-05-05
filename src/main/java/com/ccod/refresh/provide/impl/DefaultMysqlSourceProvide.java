package com.ccod.refresh.provide.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.ccod.refresh.processor.CustomMadeEnvironmentPostProcessor;
import com.ccod.refresh.properties.ConfigConstant;
import com.ccod.refresh.provide.CustomSourceProvide;
import com.ccod.refresh.util.IoUtils;
import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * mysql配置实现
 *
 * @author ccod
 * @date 2022/5/5 11:10 AM
 **/
public class DefaultMysqlSourceProvide implements CustomSourceProvide {

    private static final Log log = LogFactory.getLog(CustomMadeEnvironmentPostProcessor.class);

    private ConfigurableEnvironment environment;

    private DruidDataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private String querySql;

    /**
     * 默认mysql表名配置key
     */
    public static final String DEFAULT_MYSQL_TABLE_NAME_KEY = ConfigConstant.REFRESH_CONFIG_KEY_PREFIX + ".mysql.table-name";

    private long gmtModify;

    @Override
    public Map<String, Object> getSource() {
        List<Map<String, Object>> sourceList = jdbcTemplate.queryForList(querySql, new Date(gmtModify));
        if (CollectionUtils.isEmpty(sourceList)) {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("当前查询结果集:" + sourceList);
        }
        Map<String, Object> res = Maps.newHashMap();
        for (Map<String, Object> sourceMap : sourceList) {
            long gmtModify = ((LocalDateTime) sourceMap.get("gmt_modify")).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if (this.gmtModify < gmtModify) {
                this.gmtModify = gmtModify;
            }
            res.put(sourceMap.get("key") + "", sourceMap.get("value"));
        }
        return res;
    }

    @Override
    public void setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
        init();
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public void close() {
        IoUtils.close(dataSource);
    }

    private void init() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(this.environment.getProperty("spring.datasource.url"));
        druidDataSource.setUsername(this.environment.getProperty("spring.datasource.username"));
        druidDataSource.setPassword(this.environment.getProperty("spring.datasource.password"));
        druidDataSource.setDriverClassName(this.environment.getProperty("spring.datasource.driver-class-name"));
        druidDataSource.setMaxIdle(1);
        druidDataSource.setMinIdle(this.environment.getProperty("spring.datasource.minIdle", Integer.class, 1));
        druidDataSource.setInitialSize(this.environment.getProperty("spring.datasource.initialSize", Integer.class, 1));
        druidDataSource.setMaxWait(this.environment.getProperty("spring.datasource.maxWait", Integer.class, 60000));
        this.dataSource = druidDataSource;
        this.jdbcTemplate = new JdbcTemplate(druidDataSource);
        String tableName = this.environment.getProperty(DEFAULT_MYSQL_TABLE_NAME_KEY, "custom_refresh_config");
        this.querySql = "select `key`,`value`,gmt_modify from " + tableName + " where gmt_modify > ? and `key` is not null and `value` is not null order by gmt_modify desc";
    }
}
