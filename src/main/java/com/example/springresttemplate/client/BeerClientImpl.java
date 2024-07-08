package com.example.springresttemplate.client;

import com.example.springresttemplate.model.BeerDTO;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;

    private static final String BASE_PATH = "http://localhost:8080";

    private static final String GET_BEER_PATH = "/api/v1/beer";


    @Override
    public Page<BeerDTO> listAllBeers() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<String>  stringResponse = restTemplate.getForEntity(BASE_PATH + GET_BEER_PATH, String.class);
        System.out.println(stringResponse.getBody());

        ResponseEntity<Map>  mapResponse = restTemplate.getForEntity(BASE_PATH + GET_BEER_PATH, Map.class);
        System.out.println(mapResponse.getBody());

        ResponseEntity<JsonNode>  jsonResponse = restTemplate.getForEntity(BASE_PATH + GET_BEER_PATH, JsonNode.class);

        jsonResponse.getBody().findPath("content")
                .elements().forEachRemaining(jsonNode -> {
                    System.out.println(jsonNode.get("beerName").asText());
                });

        return null;
    }
}
