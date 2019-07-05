package com.dengyunlong.component;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * @author dyl
 * @description 带有连接池的rest template配置
 * @date 2019/7/5
 */
public class HttpRestTemplate {

    /**
     * @return
     */
    public RestTemplate restTemplate() {

        RestTemplateBuilder builder = new RestTemplateBuilder();

        RestTemplate restTemplate = builder.build();
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        // 使用 utf-8 编码集的 conver 替换默认的 conver（默认的 string conver 的编码集为"ISO-8859-1"）
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
        while (iterator.hasNext()) {
            HttpMessageConverter<?> converter = iterator.next();
            if (converter instanceof StringHttpMessageConverter) {
                iterator.remove();
            }
        }
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return restTemplate;
    }

    /**
     * @return
     */
    public HttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        // 连接池最大连接数
        poolingConnectionManager.setMaxTotal(100);
        // 每个主机的并发
        poolingConnectionManager.setDefaultMaxPerRoute(100);
        return poolingConnectionManager;
    }

    /**
     * 
     * @return
     */
    public HttpClientBuilder httpClientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //设置HTTP连接管理器
        httpClientBuilder.setConnectionManager(poolingConnectionManager());
        return httpClientBuilder;
    }

    /**
     *
     * @return
     */
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClientBuilder().build());
        // 连接超时，毫秒
        clientHttpRequestFactory.setConnectTimeout(6000);
        // 读写超时，毫秒
        clientHttpRequestFactory.setReadTimeout(6000);
        return clientHttpRequestFactory;
    }
}