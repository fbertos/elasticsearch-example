package org.fbertos.search;

import java.net.InetAddress;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class App 
{
    public static void main( String[] args )
    {
    	try {
    		Settings settings = Settings.builder()
    		        .put("cluster.name", "docker-cluster")
    		        .put("client.transport.sniff", true).build();
    		
    		TransportClient client = new PreBuiltTransportClient(settings)
    	        .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
    		
    		/*
    		User user = new User();
    		user.setId(3L);
    		user.setUsername("viewer");
    		user.setEnabled(true);
    		    		
    		ObjectMapper mapper = new ObjectMapper();
    		String json = mapper.writeValueAsString(user);
    		
    		IndexResponse response = client.prepareIndex("twitter", "_doc", new Long(user.getId()).toString())
    		        .setSource(json, XContentType.JSON)
    		        .get();
    		*/
    		
    		/*
    		GetResponse response = client.prepareGet("twitter", "_doc", "1").get();
    		
    		System.out.println(response.getSourceAsString());
    		*/
    		
    		
    		BoolQueryBuilder query = QueryBuilders.boolQuery()
    				.must(QueryBuilders.boolQuery()
    						.should(QueryBuilders.termQuery("username", "fbertos"))
    						.should(QueryBuilders.termQuery("username", "admin")))
    				.must(QueryBuilders.termQuery("enabled", "true"));
    		
    		SearchResponse response = client.prepareSearch("twitter")
    		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
    		        .setQuery(query)
    		        //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))
    		        .setFrom(0).setSize(60).setExplain(true)
    		        .get();

    		
    		for (int i=0; i<response.getHits().totalHits; i++) {
    			System.out.println(response.getHits().getAt(i).getScore());
    			System.out.println(response.getHits().getAt(i).getSourceAsString());
    		}
    		    		
    		client.close();
    		
    		System.out.println("OK!!");
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}

    }
}
