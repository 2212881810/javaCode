package qinfeng.zheng.es.test;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

import java.io.IOException;

/**
 * @Author ZhengQinfeng
 * @Date 2021/5/9 13:46
 * @dec
 */
public class EsClientTest_search_index {
    public static void main(String[] args) throws IOException {

        // 创建es客户端
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ));


        GetIndexRequest getIndexRequest = new GetIndexRequest("user");
        RequestOptions options = RequestOptions.DEFAULT;

        GetIndexResponse response = client.indices().get(getIndexRequest, options);
        System.out.println(response.getAliases());
        System.out.println(response.getMappings());
        System.out.println(response.getSettings());

        // 关闭es客户端
        client.close();

    }
}
