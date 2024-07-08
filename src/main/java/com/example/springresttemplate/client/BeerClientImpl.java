package com.example.springresttemplate.client;

import com.example.springresttemplate.model.BeerDTO;
import com.example.springresttemplate.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;

    private static final String BASE_PATH = "http://localhost:8080";

    private static final String GET_BEER_PATH = "/api/v1/beer";


    @Override
    public Page<BeerDTO> listAllBeers() {
        return listAllBeers(null, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> listAllBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        if (StringUtils.hasText(beerName)) uriComponentsBuilder.queryParam("beerName", beerName);

        if (beerStyle != null) uriComponentsBuilder.queryParam("beerStyle", beerStyle);

        if (showInventory != null) uriComponentsBuilder.queryParam("showInventory", showInventory);

        if (pageNumber != null) uriComponentsBuilder.queryParam("pageNumber", pageNumber);

        if (pageSize != null) uriComponentsBuilder.queryParam("pageSize", pageSize);

//        ResponseEntity<String>  stringResponse = restTemplate.getForEntity(BASE_PATH + GET_BEER_PATH, String.class);
//        System.out.println(stringResponse.getBody());
//        ResponseEntity<Map>  mapResponse = restTemplate.getForEntity(BASE_PATH + GET_BEER_PATH, Map.class);
//        System.out.println(mapResponse.getBody());
//
//        ResponseEntity<JsonNode>  jsonResponse = restTemplate.getForEntity(BASE_PATH + GET_BEER_PATH, JsonNode.class);
//
//        jsonResponse.getBody().findPath("content")
//                .elements().forEachRemaining(jsonNode -> {
//                    System.out.println(jsonNode.get("beerName").asText());
//                });

        ResponseEntity<BeerDTOPageImpl> pageResponse = restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);
        return pageResponse.getBody();
    }
}
