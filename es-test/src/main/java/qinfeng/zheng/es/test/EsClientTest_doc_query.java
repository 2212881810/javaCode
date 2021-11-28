package qinfeng.zheng.es.test;

import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author ZhengQinfeng
 * @Date 2021/5/9 13:46
 * @dec
 */
public class EsClientTest_doc_query {
    public static void main(String[] args) throws IOException {

        // 创建es客户端
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ));

        // 1. query全量查询数据
//        SearchRequest searchRequest = new SearchRequest();
//
//
//        searchRequest.indices("user");
//        // 索引全量匹配
//        SearchSourceBuilder query = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
//        searchRequest.source(query);
//
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHits hits = response.getHits();
//        System.out.println(hits.getTotalHits());
//        System.out.println(response.getTook());
//
//        for (SearchHit hit : hits) {
//            //打印数据结果
//            System.out.println(hit.getSourceAsString());
//        }
//
        // 2. 条件查询termQuery
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("user");
//        // 查询tel 等于 40的数据
//        SearchSourceBuilder query = new SearchSourceBuilder().query(QueryBuilders.termQuery("tel",30));
//        searchRequest.source(query);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);


//        // 3. 分页查询
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("user");
//        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
//        builder.from(0);
//        builder.size(2);
//        searchRequest.source(builder);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);


//        // 4. 查询排序
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("user");
//        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
//        builder.sort("tel", SortOrder.DESC);
//        searchRequest.source(builder);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);


//        // 5. 过滤field
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("user");
//        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
//
//        String[] include = {"name"};
//        String[] exclude = {"sex", "tel"};
//        builder.fetchSource(include, exclude);
//        searchRequest.source(builder);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);


//        // 6.组合查询
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("user");
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(QueryBuilders.matchQuery("tel", 30));
//        boolQueryBuilder.must(QueryBuilders.matchQuery("sex", "男"));
//
//        SearchSourceBuilder builder = new SearchSourceBuilder().query(boolQueryBuilder);
//        searchRequest.source(builder);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);


//        // 7.组合查询
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("user");
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.should(QueryBuilders.matchQuery("tel", 30));
//        boolQueryBuilder.should(QueryBuilders.matchQuery("tel", 40));
//
//        SearchSourceBuilder builder = new SearchSourceBuilder().query(boolQueryBuilder);
//        searchRequest.source(builder);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);



        // 8.范围查询
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("user");
//        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.rangeQuery("tel").gt(40).lt(60));
//        searchRequest.source(builder);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);


//        // 9. 模糊查询
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("user");
//        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.fuzzyQuery("name","sun").fuzziness(Fuzziness.ONE));
//        searchRequest.source(builder);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);



//        // 10. 高亮查询
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("user");
//        TermQueryBuilder query = QueryBuilders.termQuery("name", "qian");
//        SearchSourceBuilder builder = new SearchSourceBuilder().query(query);
//
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.preTags("<font color='red'>");
//        highlightBuilder.postTags("</font>");
//        highlightBuilder.field("name"); // 对name做高亮查询
//        builder.highlighter(highlightBuilder);
//
//        searchRequest.source(builder);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);




//        // 11. 聚合查询
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("user");
//
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        AggregationBuilder aggregationBuilder = AggregationBuilders.max("maxTel").field("tel");
//        builder.aggregation(aggregationBuilder);
//
//        searchRequest.source(builder);
//        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);


        // 11. 分组查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");

        SearchSourceBuilder builder = new SearchSourceBuilder();
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("telGroup").field("tel");
        builder.aggregation(aggregationBuilder);

        searchRequest.source(builder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);


        Aggregations aggregations = response.getAggregations();



        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        System.out.println(response.getTook());

        for (SearchHit hit : hits) {
            //打印数据结果
            System.out.println(hit.getSourceAsString());
        }

        // 关闭es客户端
        client.close();

    }
}
