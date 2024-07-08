package com.example.springresttemplate.client;

import com.example.springresttemplate.model.BeerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;

    private static final String BASE_PATH = "http://localhost:8080/api/v1";

    @Override
    public Page<BeerDTO> listAllBeers() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<String>  stringResponse = restTemplate.getForEntity(BASE_PATH + "/beer", String.class);
        System.out.println(stringResponse.getBody());
        return null;
    }
}
