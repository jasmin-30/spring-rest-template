package com.example.springresttemplate.client;

import com.example.springresttemplate.model.BeerDTO;
import org.springframework.data.domain.Page;

public class BeerClientImpl implements BeerClient {
    @Override
    public Page<BeerDTO> listAllBeers() {
        return null;
    }
}
