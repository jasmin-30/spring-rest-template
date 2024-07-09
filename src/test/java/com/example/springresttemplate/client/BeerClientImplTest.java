package com.example.springresttemplate.client;

import com.example.springresttemplate.model.BeerDTO;
import com.example.springresttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClient beerClient;

    @Test
    void testDeleteBeer() {
        BeerDTO dto = BeerDTO.builder()
                .beerName("Mango Bobs")
                .price(new BigDecimal("10.99"))
                .beerStyle(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123245")
                .build();

        BeerDTO newDto = beerClient.createBeer(dto);

        beerClient.deleteBeer(newDto.getId());
        assertThrows(HttpClientErrorException.class, () -> beerClient.getBeerById(newDto.getId()));
    }

    @Test
    void testUpdateBeer() {
        BeerDTO dto = BeerDTO.builder()
                .beerName("Mango Bobs")
                .price(new BigDecimal("10.99"))
                .beerStyle(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123245")
                .build();

        BeerDTO newDto = beerClient.createBeer(dto);
        final String newName = "Mango Bobs 2";
        newDto.setBeerName(newName);

        BeerDTO updatedBeerDto = beerClient.updateBeer(newDto);
        assertEquals(newName, updatedBeerDto.getBeerName());
    }

    @Test
    void testCreateBeer() {
        BeerDTO dto = BeerDTO.builder()
                .beerName("Mango Bobs")
                .price(new BigDecimal("10.99"))
                .beerStyle(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123245")
                .build();

        BeerDTO savedBeerDto = beerClient.createBeer(dto);
        assertNotNull(savedBeerDto);
    }

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