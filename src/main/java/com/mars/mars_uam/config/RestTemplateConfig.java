package com.mars.mars_uam.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类
 * Created by wuketao on 2018/8/16.
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Value("${restTemplateConfig.maxTotal}")
    private Integer maxTotal;
    @Value("${restTemplateConfig.maxPerRoute}")
    private Integer maxPerRoute;
    @Value("${restTemplateConfig.retryCount}")
    private Integer retryCount;
    @Value("${restTemplateConfig.connectTimeout}")
    private Integer connectTimeout;
    @Value("${restTemplateConfig.readTimeout}")
    private Integer readTimeout;
    @Value("${restTemplateConfig.connectionRequestTimeout}")
    private Integer connectionRequestTimeout;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        return restTemplate;
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            // 开始设置连接池
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
            poolingHttpClientConnectionManager.setMaxTotal(maxTotal); // 最大连接数500
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxPerRoute); // 同路由并发数100
            httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(retryCount, true)); // 重试次数
            CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
            HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(closeableHttpClient); // httpClient连接配置
            clientHttpRequestFactory.setConnectTimeout(connectTimeout);              // 连接超时
            clientHttpRequestFactory.setReadTimeout(readTimeout);                 // 数据读取超时时间
            clientHttpRequestFactory.setConnectionRequestTimeout(connectionRequestTimeout);    // 连接不够用的等待时间
            return clientHttpRequestFactory;
        } catch (Exception e) {
            log.error("初始化HTTP连接池出错", e);
        }
        return null;
    }
}
