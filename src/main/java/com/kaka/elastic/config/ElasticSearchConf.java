package com.kaka.elastic.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fuwei
 * @version V1.0
 * @Description: TODO(用一句话描述该文件做什么)
 * @date 2018/10/11 10:04
 */
@Configuration
public class ElasticSearchConf {

  private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConf.class);

  @Bean
  public TransportClient client() throws UnknownHostException {
    //多个节点可以继续增加node2、node3、、、、、
    InetSocketTransportAddress node1 = new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300);
    //定义集群的名字，应该与Elasticsearch的master的配置保持一致
    Settings settings = Settings.builder()
            .put("cluster.name", "kaka-elastic")
             .build();
    //使用设置建立客户端
    TransportClient client = new PreBuiltTransportClient(settings);
    client.addTransportAddress(node1);
    return client;
  }


}
