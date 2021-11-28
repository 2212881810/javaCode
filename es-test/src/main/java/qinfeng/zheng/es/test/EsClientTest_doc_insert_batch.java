package qinfeng.zheng.es.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

/**
 * @Author ZhengQinfeng
 * @Date 2021/5/9 13:46
 * @dec
 */
public class EsClientTest_doc_insert_batch {
    public static void main(String[] args) throws IOException {

        // 创建es客户端
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ));

        // 批量插入数据
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest().index("user").id("10001").source(XContentType.JSON, "name", "zhao","sex","男","tel",30));
        bulkRequest.add(new IndexRequest().index("user").id("10002").source(XContentType.JSON, "name", "qian","sex","男","tel",30));
        bulkRequest.add(new IndexRequest().index("user").id("10003").source(XContentType.JSON, "name", "sun","sex","女","tel",40));
        bulkRequest.add(new IndexRequest().index("user").id("10004").source(XContentType.JSON, "name", "sun1","sex","男","tel",50));
        bulkRequest.add(new IndexRequest().index("user").id("10005").source(XContentType.JSON, "name", "sun2","sex","女","tel",20));
        bulkRequest.add(new IndexRequest().index("user").id("10006").source(XContentType.JSON, "name", "sun3","sex","男","tel",80));
        bulkRequest.add(new IndexRequest().index("user").id("10007").source(XContentType.JSON, "name", "sun4","sex","妇","tel",30));

        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(response.getTook());// 耗时
        System.out.println(response.getItems());// 结果

        // 关闭es客户端
        client.close();

    }
}
