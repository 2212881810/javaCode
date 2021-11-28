package qinfeng.zheng.es.test;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;

import java.io.IOException;

/**
 * @Author ZhengQinfeng
 * @Date 2021/5/9 13:46
 * @dec
 */
public class EsClientTest_create_index {
    public static void main(String[] args) throws IOException {

        // 创建es客户端
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ));

        CreateIndexRequest createIndexRequest = new CreateIndexRequest("user");
        // 创建索引
        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        // 创建索引的结果
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);

        // 关闭es客户端
        client.close();

    }
}
