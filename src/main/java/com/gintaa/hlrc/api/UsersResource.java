package com.gintaa.hlrc.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gintaa.hlrc.model.User;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.core.MainResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/users")
public class UsersResource {

    @Autowired
    RestHighLevelClient client;

    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/stats")
    public String callInfo() throws IOException {
        MainResponse info = client.info(RequestOptions.DEFAULT);
        String version = info.getVersion().getNumber();
        return version;
    }

    @GetMapping("/all")
    public List<User> findAll() throws IOException {
        SearchResponse response = client.search(new SearchRequest("users"), RequestOptions.DEFAULT);
        SearchHit[] searchHit = response.getHits().getHits();
        List<User> userDocs = new ArrayList<>();

        for (SearchHit hit : searchHit) {
            userDocs.add(mapper.convertValue(hit.getSourceAsMap(), User.class));
        }
        return userDocs;
    }

    @GetMapping("/find/{username}")
    public User findByUsername(@PathVariable String username) throws IOException {
        SearchResponse response = client.search(
                new SearchRequest("users")
                        .source(new SearchSourceBuilder().query(QueryBuilders.matchQuery("username", username))),
                RequestOptions.DEFAULT);

        SearchHit searchHit = response.getHits().getHits()[0];

        mapper.convertValue(searchHit.getSourceAsMap(), User.class);

        return mapper.convertValue(searchHit.getSourceAsMap(), User.class);

    }

    @PostMapping("/create")
    public User create(@RequestBody String document) throws IOException {
        User newUser = mapper.readValue(document, User.class);
        client.index(new IndexRequest("users").source(document, XContentType.JSON), RequestOptions.DEFAULT);
        client.indices().refresh(new RefreshRequest("users"), RequestOptions.DEFAULT);
        SearchResponse response = client.search(new SearchRequest("users"), RequestOptions.DEFAULT);
        System.out.println("response.getHits().totalHits = " + response.getHits().getTotalHits().value);
        return findByUsername(newUser.getUsername());
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("users", id);
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);

        return response.getResult().name();

    }

}
