# elastic-search-example
elastic-search-example初步试验
注：

step1：elastic-search服务启动

       配置文件elasticsearch.yml至少需要配置这几个位置：cluster.name、node.name、network.host
       （如果要跨域，需要添加http.cors.enabled: true、http.cors.allow-origin: "*"）

step2：elasticsearch-head-master项目拉下来，npm run install、然后npm run start方便随时查看elastic-search的状态和数据情况；


tips：对于数据同步到elasticSearch，可以用bboss elasticsearch进行导入。参考https://my.oschina.net/bboss/blog/1832212
