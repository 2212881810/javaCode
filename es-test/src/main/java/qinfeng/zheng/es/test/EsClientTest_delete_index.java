package qinfeng.zheng.es.test;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

import java.io.IOException;

/**
 * @Author ZhengQinfeng
 * @Date 2021/5/9 13:46
 * @dec
 */
public class EsClientTest_delete_index {
    public static void main(String[] args) throws IOException {

        // 创建es客户端
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ));


        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("user");
        RequestOptions options = RequestOptions.DEFAULT;

        AcknowledgedResponse response = client.indices().delete(deleteIndexRequest, options);
        System.out.println(response.isAcknowledged());


        // 关闭es客户端
        client.close();

    }
}
