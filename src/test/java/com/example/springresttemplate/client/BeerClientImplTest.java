package com.example.springresttemplate.client;

import com.example.springresttemplate.model.BeerDTO;
import com.example.springresttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClient beerClient;


    @Test
    void getBeerById() {
        BeerDTO dto = beerClient.listBeers().getContent().get(0);

        BeerDTO beerById = beerClient.getBeerById(dto.getId());

        assertNotNull(beerById);
    }

    @Test
    void listBeersWithoutQueryParam() {
        beerClient.listBeers();
    }

    @Test
    void listBeers() {
        beerClient.listBeers("ALE", BeerStyle.ALE, true, 1, 25);
    }
}