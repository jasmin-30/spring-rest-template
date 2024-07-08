package com.example.springresttemplate.client;

import com.example.springresttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClient beerClient;

    @Test
    void listAllBeersNoBeerName() {
        beerClient.listAllBeers();
    }

    @Test
    void listAllBeers() {
        beerClient.listAllBeers("ALE", BeerStyle.ALE, true, 1, 25);
    }
}