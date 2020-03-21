package com.avpines.workshop.biography.service;

import com.avpines.workshop.biography.config.AppConfig;
import com.avpines.workshop.biography.config.BiographyConfig;
import com.avpines.workshop.biography.model.Author;
import com.avpines.workshop.biography.model.Proverb;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes =
        {
                BiographyConfig.class,
                AppConfig.class,
                ObjectMapper.class
        }
)
@TestPropertySource(
        properties = {
                "biography.endpoints.proverb-service=http://somewhere.over.the.rainbow/proverb/"
        }
)
class InMemoryAuthorServiceMockServerTest {

    @Value("${biography.endpoints.proverb-service:''}")
    private String proverbEndpoint;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @PostConstruct
    private void printEndpoint() {
        LOG.info("Proverb endpoint is '{}'", proverbEndpoint);
    }

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void authorBiography() throws Exception {
        String name = "Ralph Johnson";
        mockServer.expect(requestTo(proverbEndpoint(name)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(
                                new Proverb[] {new Proverb(name, "hello, world")})));
        Author actual = authorService.biography(name);
        // assert against the biographies that we have in the config
        assertThat(actual.getBiography().getName()).isEqualTo(name);
        assertThat(actual.getBiography().getYearOfBirth()).isEqualTo(1923);
        assertThat(actual.getBiography().getSummary())
                .isEqualTo("Medical student who enjoys spreading fake news " +
                        "on Facebook, horse riding and playing card games.");
        assertThat(actual.getProverbs()).containsExactlyInAnyOrder("hello, world");
    }

    String proverbEndpoint(String author) {
        return proverbEndpoint +
                URLEncoder.encode(author, Charset.defaultCharset())
                        .replace("+", "%20");
    }

}