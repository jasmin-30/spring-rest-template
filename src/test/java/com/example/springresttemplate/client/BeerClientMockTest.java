package com.example.springresttemplate.client;

import com.example.springresttemplate.config.OAuthClientInterceptor;
import com.example.springresttemplate.config.RestTemplateBuilderConfig;
import com.example.springresttemplate.model.BeerDTO;
import com.example.springresttemplate.model.BeerStyle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
class BeerClientMockTest {
    @Autowired
    RestTemplateBuilder restTemplateBuilderConfigured;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

    private BeerClient beerClient;

    private MockRestServiceServer server;

    private static final String URL = "http://localhost:8080";
    private static final String BEARER_TOKEN = "Bearer test";

    BeerDTO dto;
    String dtoJson;

    @MockBean
    OAuth2AuthorizedClientManager manager;

    @TestConfiguration
    public static class TestConfig {

        @Bean
        ClientRegistrationRepository clientRegistrationRepository() {
            return new InMemoryClientRegistrationRepository(
                    ClientRegistration.withRegistrationId("springauth")
                            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                            .clientId("test")
                            .tokenUri("test")
                            .build()
            );
        }

        @Bean
        OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
            return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        }

        @Bean
        OAuthClientInterceptor oAuthClientInterceptor(OAuth2AuthorizedClientManager manager, ClientRegistrationRepository clientRegistrationRepository) {
            return new OAuthClientInterceptor(manager, clientRegistrationRepository);
        }
    }

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        RestTemplate restTemplate = restTemplateBuilderConfigured.build();
        server = MockRestServiceServer.bindTo(restTemplate).build();

        when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
        beerClient = new BeerClientImpl(mockRestTemplateBuilder);

        dto = getBeerDto();
        dtoJson = objectMapper.writeValueAsString(dto);

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("springauth");

        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "test", Instant.MIN, Instant.MAX);

        when(manager.authorize(Mockito.any())).thenReturn(new OAuth2AuthorizedClient(clientRegistration, "test", token));
    }

    private void mockGetOperation() {
        server.expect(method(HttpMethod.GET))
                .andExpect(header("Authorization", BEARER_TOKEN))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withSuccess(dtoJson, MediaType.APPLICATION_JSON));
    }

    @Test
    void testListBeers() throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(getPage());

        server.expect(method(HttpMethod.GET))
                .andExpect(header("Authorization", BEARER_TOKEN))
                .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> dtos = beerClient.listBeers();
        assertThat(dtos.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testGetBeerById() throws JsonProcessingException {

        mockGetOperation();

        BeerDTO returnedDto = beerClient.getBeerById(dto.getId());
        assertThat(returnedDto.getId()).isEqualTo(dto.getId());
    }

    @Test
    void testCreateBeer() throws JsonProcessingException {
        URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH).build(dto.getId());

        server.expect(method(HttpMethod.POST))
                .andExpect(header("Authorization", BEARER_TOKEN))
                .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
                .andRespond(withAccepted().location(uri));

        mockGetOperation();

        BeerDTO returnedDto = beerClient.createBeer(dto);
        assertThat(returnedDto.getId()).isEqualTo(dto.getId());

    }

    @Test
    void testUpdateBeer() throws JsonProcessingException {
        server.expect(method(HttpMethod.PUT))
                .andExpect(header("Authorization", BEARER_TOKEN))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withNoContent());

        mockGetOperation();

        BeerDTO returnedDto = beerClient.updateBeer(dto);
        assertThat(returnedDto.getId()).isEqualTo(dto.getId());
    }

    @Test
    void testDeleteBeer() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(header("Authorization", BEARER_TOKEN))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withNoContent());

        beerClient.deleteBeer(dto.getId());
        server.verify();
    }

    @Test
    void testDeleteBeerWhenBeerIdNotFound() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(header("Authorization", BEARER_TOKEN))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.class, () -> beerClient.deleteBeer(dto.getId()));
        server.verify();
    }

    @Test
        void testListBeersWithQueryParam() throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(getPage());
        URI uri = UriComponentsBuilder.fromHttpUrl(URL + BeerClientImpl.GET_BEER_PATH)
                .queryParam("beerName", "ALE")
                .queryParam("beerStyle", BeerStyle.ALE)
                .queryParam("showInventory", true)
                .queryParam("pageNumber", 1)
                .queryParam("pageSize", 25)
                .build().toUri();

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(uri))
                .andExpect(header("Authorization", BEARER_TOKEN))
                .andExpect(queryParam("beerName", "ALE"))
                .andExpect(queryParam("beerStyle", "ALE"))
                .andExpect(queryParam("showInventory", "true"))
                .andExpect(queryParam("pageNumber", "1"))
                .andExpect(queryParam("pageSize", "25"))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> pageResponse = beerClient.listBeers("ALE", BeerStyle.ALE, true, 1, 25);
        assertThat(pageResponse.getContent().size()).isEqualTo(1);
    }

    BeerDTO getBeerDto(){
        return BeerDTO.builder()
                .id(UUID.randomUUID())
                .price(new BigDecimal("10.99"))
                .beerName("Mango Bobs")
                .beerStyle(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123245")
                .build();
    }

    BeerDTOPageImpl<BeerDTO> getPage(){
        return new BeerDTOPageImpl<>(Collections.singletonList(getBeerDto()), 1, 25, 1);
    }
}