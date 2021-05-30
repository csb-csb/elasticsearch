package com.kuang.kuangshenesapi;

import com.alibaba.fastjson.JSON;
import com.kuang.kuangshenesapi.entity.User;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class KuangshenEsApiApplicationTests {

@Autowired
@Qualifier("restHighLevelClient")
private RestHighLevelClient client;

    /**
     * api讲解
     * 测试索引的额创建，，，requet
     */
    @Test
    void testCreatIndex() throws IOException {
        //创建请求
        CreateIndexRequest request=new CreateIndexRequest("kuang_index");
        //执行请求，IndicesClient
        CreateIndexResponse createIndexResponse=  client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);


    }
    @Test
    void existIndex() throws IOException {
        //该索引是否存在
        GetIndexRequest getIndexRequest=   new GetIndexRequest("kuang_index");

        //执行请求，IndicesClient
        Boolean exists=  client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);


    }

    @Test
    void deleteIndex() throws IOException {

        DeleteIndexRequest deleteIndexRequest=new DeleteIndexRequest("kuang_index");

        //执行请求，IndicesClient
        AcknowledgedResponse delete=  client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    @Test//加入文档
    void addDocuments() throws IOException {

        User user=new User("娃哈哈",12);
        IndexRequest indexRequest=new IndexRequest("kuang_index");
        //规则put /kuang_index/_doc/1
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.timeout("1s");
        //放入json请求 jason
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求
       IndexResponse indexResponse= client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());
        System.out.println(user.toString());

    }
    @Test        //检测文档是否存在
    void getDocumentsExists() throws IOException {

        GetRequest getRequest=   new GetRequest("kuang_index","1");
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        Boolean exists=client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);



    }

    @Test        //获取文档信息
    void getDocuments() throws IOException {

        GetRequest getRequest=   new GetRequest("kuang_index","1");
        //getRequest.fetchSourceContext(new FetchSourceContext(false));
        //getRequest.storedFields("_none_");
        GetResponse documents=client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(documents.getSourceAsString());//打印文档内容,没有库表
        System.out.println(documents);//返回的和命令内容一样

    }

    @Test        //更改文档信息
    void updateDocuments() throws IOException {

        UpdateRequest updateRequest=   new UpdateRequest("kuang_index","1");
        //getRequest.fetchSourceContext(new FetchSourceContext(false));
        //getRequest.storedFields("_none_");
        User user=new User("狂神java",18);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse documents=client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(documents.toString());//打印文档内容


    }

    @Test        //更改文档信息
    void testDeleteRequest() throws IOException {

        DeleteRequest deleteRequest=   new DeleteRequest("kuang_index","1");
      //  User user=new User("狂神java",18);

        DeleteResponse documents=client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(documents.toString());//打印文档内容
        System.out.println(documents);//返回的和命令内容一样

    }


    @Test        //批处理请求
    void BulkRequest() throws IOException {
        //批量删除，改请求名就行了
        BulkRequest bulkRequest=   new BulkRequest();
        bulkRequest.timeout("2s");
        List<User> list= new ArrayList();
        list.add(new User("kkkk1",23));
        list.add(new User("kkkk2",23));
        list.add(new User("kkkk3",23));
        list.add(new User("kkkk4",23));
        list.add(new User("kkkk5",23));
        for (int i = 0; i <list.size() ; i++) {

           /*如果不设置id，就是自增长*/
            bulkRequest.add(new IndexRequest("kuang_index").id(""+(i+1)).source(
                    JSON.toJSONString(list.get(i)),XContentType.JSON
            ));
        }


        //  User user=new User("狂神java",18);

        BulkResponse documents=client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(documents.toString());
        System.out.println(documents.hasFailures());//false表示成功


    }

    @Test        //更改文档信息
    void testSearch() throws IOException {
        //构建请求
        SearchRequest searchRequest=   new SearchRequest("kuang_index");
        //构建搜索条件,包括高亮等
        SearchSourceBuilder searchSourceBuilder= new SearchSourceBuilder();
        // 精确查询 QueryBuilders.termQuery("name","k13");
        // 查询全部数据  QueryBuilders.matchAllQuery();
        //
       TermQueryBuilder termQueryBuilder= QueryBuilders.termQuery("name","kkkk1");
       MultiMatchQueryBuilder matchQueryBuilder= QueryBuilders.multiMatchQuery("kkk","name" );
        MatchAllQueryBuilder matchQueryBuilder1= QueryBuilders.matchAllQuery();
       searchSourceBuilder.query(termQueryBuilder);
       searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
       searchRequest.source(searchSourceBuilder);
      SearchResponse documents=client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(documents.getHits()));//打印文档内容
      //作为hashmap遍历
        for (SearchHit hit: documents.getHits()
             ) {
            System.out.println(hit.getSourceAsMap());
        }

    }
}
