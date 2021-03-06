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
     * api??????
     * ?????????????????????????????????requet
     */
    @Test
    void testCreatIndex() throws IOException {
        //????????????
        CreateIndexRequest request=new CreateIndexRequest("kuang_index");
        //???????????????IndicesClient
        CreateIndexResponse createIndexResponse=  client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);


    }
    @Test
    void existIndex() throws IOException {
        //?????????????????????
        GetIndexRequest getIndexRequest=   new GetIndexRequest("kuang_index");

        //???????????????IndicesClient
        Boolean exists=  client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);


    }

    @Test
    void deleteIndex() throws IOException {

        DeleteIndexRequest deleteIndexRequest=new DeleteIndexRequest("kuang_index");

        //???????????????IndicesClient
        AcknowledgedResponse delete=  client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    @Test//????????????
    void addDocuments() throws IOException {

        User user=new User("?????????",12);
        IndexRequest indexRequest=new IndexRequest("kuang_index");
        //??????put /kuang_index/_doc/1
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.timeout("1s");
        //??????json?????? jason
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        //?????????????????????
       IndexResponse indexResponse= client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());
        System.out.println(user.toString());

    }
    @Test        //????????????????????????
    void getDocumentsExists() throws IOException {

        GetRequest getRequest=   new GetRequest("kuang_index","1");
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        Boolean exists=client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);



    }

    @Test        //??????????????????
    void getDocuments() throws IOException {

        GetRequest getRequest=   new GetRequest("kuang_index","1");
        //getRequest.fetchSourceContext(new FetchSourceContext(false));
        //getRequest.storedFields("_none_");
        GetResponse documents=client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(documents.getSourceAsString());//??????????????????,????????????
        System.out.println(documents);//??????????????????????????????

    }

    @Test        //??????????????????
    void updateDocuments() throws IOException {

        UpdateRequest updateRequest=   new UpdateRequest("kuang_index","1");
        //getRequest.fetchSourceContext(new FetchSourceContext(false));
        //getRequest.storedFields("_none_");
        User user=new User("??????java",18);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse documents=client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(documents.toString());//??????????????????


    }

    @Test        //??????????????????
    void testDeleteRequest() throws IOException {

        DeleteRequest deleteRequest=   new DeleteRequest("kuang_index","1");
      //  User user=new User("??????java",18);

        DeleteResponse documents=client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(documents.toString());//??????????????????
        System.out.println(documents);//??????????????????????????????

    }


    @Test        //???????????????
    void BulkRequest() throws IOException {
        //????????????????????????????????????
        BulkRequest bulkRequest=   new BulkRequest();
        bulkRequest.timeout("2s");
        List<User> list= new ArrayList();
        list.add(new User("kkkk1",23));
        list.add(new User("kkkk2",23));
        list.add(new User("kkkk3",23));
        list.add(new User("kkkk4",23));
        list.add(new User("kkkk5",23));
        for (int i = 0; i <list.size() ; i++) {

           /*???????????????id??????????????????*/
            bulkRequest.add(new IndexRequest("kuang_index").id(""+(i+1)).source(
                    JSON.toJSONString(list.get(i)),XContentType.JSON
            ));
        }


        //  User user=new User("??????java",18);

        BulkResponse documents=client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(documents.toString());
        System.out.println(documents.hasFailures());//false????????????


    }

    @Test        //??????????????????
    void testSearch() throws IOException {
        //????????????
        SearchRequest searchRequest=   new SearchRequest("kuang_index");
        //??????????????????,???????????????
        SearchSourceBuilder searchSourceBuilder= new SearchSourceBuilder();
        // ???????????? QueryBuilders.termQuery("name","k13");
        // ??????????????????  QueryBuilders.matchAllQuery();
        //
       TermQueryBuilder termQueryBuilder= QueryBuilders.termQuery("name","kkkk1");
       MultiMatchQueryBuilder matchQueryBuilder= QueryBuilders.multiMatchQuery("kkk","name" );
        MatchAllQueryBuilder matchQueryBuilder1= QueryBuilders.matchAllQuery();
       searchSourceBuilder.query(termQueryBuilder);
       searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
       searchRequest.source(searchSourceBuilder);
      SearchResponse documents=client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(documents.getHits()));//??????????????????
      //??????hashmap??????
        for (SearchHit hit: documents.getHits()
             ) {
            System.out.println(hit.getSourceAsMap());
        }

    }
}
