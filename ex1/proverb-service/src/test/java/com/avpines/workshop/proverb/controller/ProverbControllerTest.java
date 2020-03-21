package com.avpines.workshop.proverb.controller;

import com.avpines.workshop.proverb.model.Proverb;
import com.avpines.workshop.proverb.service.ProverbService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = { ProverbController.class })
@AutoConfigureWebClient
class ProverbControllerTest {

    private static final String PROVERB_ENDPOINT = "/proverb";

    private static final String ALL_PROVERBS_ENDPOINT = "/proverbs";

    private static final String ALL_AUTHORS_ENDPOINT = "/authors";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProverbService proverbService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void randomProverbEndpoint() throws Exception {
        Proverb proverb = Proverb.builder()
                .author("avpines").proverb("what a nice exercise!").build();
        // set the response of the inner service
        given(this.proverbService.randomProverb()).willReturn(proverb);
        mockMvc.perform(get(PROVERB_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(proverb)));
    }

    @Test
    void allProverbs() throws Exception {
        List<Proverb> proverbs = Arrays.asList(
                Proverb.builder().author("first").proverb("hello").build(),
                Proverb.builder().author("second").proverb("world").build(),
                Proverb.builder().author("third").proverb("fubar").build());
        // set the response of the inner service
        given(this.proverbService.allProverbs()).willReturn(proverbs);
        mockMvc.perform(get(ALL_PROVERBS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(proverbs)));
    }

    @Test
    void insertProverbNewProverb() throws Exception {
        // when we insert the proverbs, the inner service will return 'true',
        // meaning that at least one proverb was new and inserted to the list.
        given(this.proverbService.insertProverbs(anyCollection())).willReturn(true);
        mockMvc.perform(put(PROVERB_ENDPOINT)
                .content(mapper.writeValueAsString(List.of(
                        Proverb.builder().author("first").proverb("hello").build(),
                        Proverb.builder().author("second").proverb("world").build())))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void insertProverbExistingProverb() throws Exception {
        // when we insert the proverbs, the inner service will return 'false',
        // meaning that no proverb was new and nothing was inserted to the list.
        given(this.proverbService.insertProverbs(anyCollection())).willReturn(false);
        mockMvc.perform(put(PROVERB_ENDPOINT)
                .content(mapper.writeValueAsString(List.of(
                        Proverb.builder().author("hello").proverb("world").build())))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void allAuthors() throws Exception {
        List<String> authors = Arrays.asList("first", "second", "third");
        // set the response of the inner service
        given(this.proverbService.authors()).willReturn(authors);
        mockMvc.perform(get(ALL_AUTHORS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(authors)));
    }

    @Test
    void proverbsByAuthors() throws Exception {
        String author = "what";
        List<Proverb> proverbs = Arrays.asList(
                Proverb.builder().author("what").proverb("hello").build(),
                Proverb.builder().author("what").proverb("world").build(),
                Proverb.builder().author("what").proverb("fubar").build());
        // set the response of the inner service
        given(this.proverbService.proverb(author)).willReturn(proverbs);
        mockMvc.perform(get(proverbByAuthorEndpoint(author)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(proverbs)));
    }

    @NotNull
    private String proverbByAuthorEndpoint(String author) {
        return PROVERB_ENDPOINT + "/" + author;
    }

}