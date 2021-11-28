package qinfeng.zheng.es.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

/**
 * @Author ZhengQinfeng
 * @Date 2021/5/9 13:46
 * @dec
 */
public class EsClientTest_doc_insert {
    public static void main(String[] args) throws IOException {

        // 创建es客户端
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ));

        // 插入数据

        IndexRequest indexRequest = new IndexRequest("user");
        indexRequest.id("10001");
        User user = new User();
        user.setName("zheng");
        user.setSex("男");
        user.setTel("1234");
        // 向es插入数据，必须转成json

        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);
        indexRequest.source(userJson, XContentType.JSON);


        RequestOptions options = RequestOptions.DEFAULT;
        IndexResponse response = client.index(indexRequest, options);
        System.out.println(response.getResult());

        // 关闭es客户端
        client.close();

    }
}
