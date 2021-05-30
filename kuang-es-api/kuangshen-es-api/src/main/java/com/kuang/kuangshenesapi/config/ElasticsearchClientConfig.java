package com.kuang.kuangshenesapi.config;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchClientConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        //restClientBuilder, Collections.emptyList(),如果是集群，就可以是多个
        RestHighLevelClient restHighLevelClient=new RestHighLevelClient(//构建实例连接
                RestClient.builder(
                        new HttpHost("localhost",9200,"http")
                )
        );
        return restHighLevelClient;
    }
}
