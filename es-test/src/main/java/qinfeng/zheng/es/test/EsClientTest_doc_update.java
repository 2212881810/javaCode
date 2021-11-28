package qinfeng.zheng.es.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
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
public class EsClientTest_doc_update {
    public static void main(String[] args) throws IOException {

        // 创建es客户端
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ));

        // 修改数据
        UpdateRequest updateRequest = new UpdateRequest("user", "10001");
        updateRequest.doc(XContentType.JSON, "sex", "女");


        RequestOptions options = RequestOptions.DEFAULT;
        UpdateResponse response = client.update(updateRequest, options);
        System.out.println(response.getResult());

        // 关闭es客户端
        client.close();

    }
}
