package com.yuu.scw.user.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootConfiguration
public class DruidConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource ds = new DruidDataSource();
        ds.setFilters("stat"); // 配置监控系统拦截的filters
        return ds;
    }

    // 配置Druid监控
    // 1. 监控后台的Service
    @Bean
    public ServletRegistrationBean statViewServlet() {
        // 设置哪个路径可以访问Druid监控系统
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "admin"); // 设置监控系统登录的用户名
        initParams.put("loginPassword", "123456"); // 设置监控系统登录的密码
        initParams.put("allow", ""); // 默认允许所有访问监控系统
        initParams.put("deny", ""); // 拒绝哪个ip访问监控系统
        bean.setInitParameters(initParams);
        return bean;
    }

    // 2. 配置web监控的Filter
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());
        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.css,/druid/*"); // 排除过滤
        bean.setInitParameters(initParams);
        bean.setUrlPatterns(Arrays.asList("/*")); // 过滤所有
        return bean;
    }

}
