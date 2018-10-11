package com.kaka.elastic.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fuwei
 * @version V1.0
 * @Description: TODO(测试五种常用增删改查)
 * @date 2018/10/11 10:22
 */
@SpringBootApplication
@RestController
public class MyController {

  @Autowired
  private TransportClient client;

  /***
   * GET localhost:8080/book/novel?id=1
   * @param id 书籍ID
   * @return 书籍信息
   */
  @GetMapping("/book/novel")
  @ResponseBody
  public ResponseEntity getBookNovel(@RequestParam(name="id",defaultValue = "")String id){
    if(id.isEmpty()){
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    GetResponse result = client.prepareGet("book","novel",id).get();

    if( !result.isExists()) {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity(result.getSource(), HttpStatus.OK);
  }

  /**
   * 插入一条数据
   * POST localhost:8080/book/novel
   * @param title 标题
   * @param author 作者
   * @param word_count 字数
   * @param publish_date 出版日期
   * @return 成功返回ID，失败返回错误码
   */
  @PostMapping("/book/novel")
  public ResponseEntity addBookNovel(
          @RequestParam(name="title",defaultValue = "")String title,
          @RequestParam(name="author",defaultValue = "")String author,
          @RequestParam(name="word_count",defaultValue = "")Integer word_count,
          @RequestParam(name="publish_date",defaultValue = "")
          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                  Date publish_date){

    try {
      XContentBuilder content =  XContentFactory.jsonBuilder()
              .startObject()
              .field("title",title)
              .field("author",author)
              .field("word_count",word_count)
              .field("publish_date",publish_date.getTime())
              .endObject();
      IndexResponse response = this.client.prepareIndex("book","novel")
              .setSource(content)
              .get();
      return new ResponseEntity(response.getId(), HttpStatus.OK);
    } catch (IOException e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /***
   * 通过DELETE  删除
   * @param id 书籍
   * @return
   */
  @DeleteMapping("/book/novel")
  public ResponseEntity delBookNovel(@RequestParam(name="id",defaultValue = "")String id){
    if(id.isEmpty()){
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    DeleteResponse response = client.prepareDelete("book","novel",id).get();

    return new ResponseEntity(response.getResult().toString(), HttpStatus.OK);
  }

  /***
   * 通过PUT修改
   * @param id 书籍
   * @param title
   * @param author
   * @param word_count
   * @param publish_date
   * @return
   */
  @PutMapping("/book/novel")
  public ResponseEntity updateBookNovel(
          @RequestParam(name="id",defaultValue = "")String id,
          @RequestParam(name="title",required = false)String title,
          @RequestParam(name="author",required = false)String author,
          @RequestParam(name="word_count",required = false)Integer word_count,
          @RequestParam(name="publish_date",required = false)
          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date publish_date){
    if(id.isEmpty()){
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    UpdateRequest update = new UpdateRequest("book","novel",id);
    try {
      XContentBuilder content =  XContentFactory.jsonBuilder()
              .startObject();
      if(title != null){
        content.field("title",title);
      }
      if(author != null){
        content.field("author",author);
      }
      if(word_count != null){
        content.field("word_count",word_count);
      }
      if(publish_date != null){
        content.field("publish_date",publish_date.getTime());
      }
      content.endObject();
      update.doc(content);

    } catch (IOException e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    try {
      UpdateResponse response =  this.client.update(update).get();
      return new ResponseEntity(response.getResult().toString(),HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /***
   * 通过POST进行复合查询
   * @param title
   * @param author
   * @param gtWordCount
   * @param ltWordCount
   * @return
   */
  @PostMapping("book/novel/query")
  public ResponseEntity query(
          @RequestParam(name="title",required = false)String title,
          @RequestParam(name="author",required = false)String author,
          @RequestParam(name="gt_word_count",defaultValue = "0")Integer gtWordCount,
          @RequestParam(name="lt_word_count",defaultValue = "0",required = false)Integer ltWordCount
  ){
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    if(author != null){
      boolQuery.must(QueryBuilders.matchQuery("author",author));
    }
    if(title != null){
      boolQuery.must(QueryBuilders.matchQuery("title",title));
    }
    RangeQueryBuilder range = QueryBuilders.rangeQuery("word_count");
    if(ltWordCount != null && ltWordCount > 0){
      range.to(ltWordCount);
    }

    boolQuery.filter(range);

    SearchRequestBuilder builder = this.client.prepareSearch("book")
            .setTypes("novel")
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .setQuery(boolQuery)
            .setFrom(0)
            .setSize(10);
    System.out.println(builder);
    SearchResponse response = builder.get();
    List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
    for(SearchHit hit:response.getHits()){
      result.add(hit.getSource());
    }
    return new ResponseEntity(result,HttpStatus.OK);
  }



}
