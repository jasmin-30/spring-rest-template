package com.example.springresttemplate.client;

import com.example.springresttemplate.model.BeerDTO;
import com.example.springresttemplate.model.BeerStyle;
import org.springframework.data.domain.Page;

public interface BeerClient {
    Page<BeerDTO> listAllBeers();

    Page<BeerDTO> listAllBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize);
}
