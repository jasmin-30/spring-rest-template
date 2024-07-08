package com.example.springresttemplate.client;

import com.example.springresttemplate.model.BeerDTO;
import org.springframework.data.domain.Page;

public interface BeerClient {
    Page<BeerDTO> listAllBeers();
}
