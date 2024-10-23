package com.vrbeneficios.miniautorizador.controller;

import com.vrbeneficios.miniautorizador.configuration.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class CartaoControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    // Teste: Dado um cartão válido, quando criar um cartão, então retorna criado com sucesso
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void givenValidCard_whenCreateCard_thenReturnsCreated() throws Exception {
        String cartaoJson = """
                    {
                        "numeroCartao": "6549873025634501",
                        "senha": "1234"
                    }
                """;

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartaoJson).with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value("6549873025634501"))
                .andExpect(jsonPath("$.senha").value("1234"));
    }

    // Teste: Dado um cartão já existente, quando tentar criar novamente, então retorna entidade não processável
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void givenExistingCard_whenCreateCard_thenReturnsUnprocessableEntity() throws Exception {
        String cartaoJson = """
                    {
                        "numeroCartao": "6549873025634501",
                        "senha": "1234"
                    }
                """;

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartaoJson).with(csrf()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartaoJson).with(csrf()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    // Teste: Dado um novo cartão, quando consultar o saldo, então retorna o saldo com sucesso
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void givenCard_whenGetBalance_thenReturnsBalanceSuccessfully() throws Exception {
        mockMvc.perform(get("/cartoes/6549873025634502")
                        .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.00"));
    }

    // Teste: Dado um cartão inexistente, quando consultar o saldo, então retorna não encontrado
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void givenNonexistentCard_whenGetBalance_thenReturnsNotFound() throws Exception {
        mockMvc.perform(get("/cartoes/1111111111111111")
                        .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isNotFound());
    }

    // Teste: Dado um cartão com saldo suficiente, quando realizar transação, então retorna transação autorizada
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void givenSufficientBalance_whenAuthorizeTransaction_thenReturnsAuthorized() throws Exception {
        String transacaoJson = "{ \"numeroCartao\": \"6549873025634502\", \"senhaCartao\": \"5678\", \"valor\": 10.00 }";

        mockMvc.perform(post("/cartoes/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transacaoJson).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));

        mockMvc.perform(get("/cartoes/6549873025634502"))
                .andExpect(status().isOk())
                .andExpect(content().string("990.00"));
    }

    // Teste: Dado saldo insuficiente, quando tentar realizar uma transação, então retorna saldo insuficiente
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void givenInsufficientBalance_whenAuthorizeTransaction_thenReturnsInsufficientBalance() throws Exception {
        String transacaoJson = "{ \"numeroCartao\": \"6549873025634502\", \"senhaCartao\": \"5678\", \"valor\": 1500.00 }";

        mockMvc.perform(post("/cartoes/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transacaoJson).with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Saldo insuficiente"));
    }

    // Teste: Dado uma senha incorreta, quando tentar realizar uma transação, então retorna senha inválida
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void givenInvalidPassword_whenAuthorizeTransaction_thenReturnsInvalidPassword() throws Exception {
        String transacaoJson = "{ \"numeroCartao\": \"6549873025634502\", \"senhaCartao\": \"9999\", \"valor\": 50.00 }";

        mockMvc.perform(post("/cartoes/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transacaoJson).with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Senha inválida"));
    }

    // Teste: Dado um cartão inexistente, quando tentar realizar uma transação, então retorna cartão inexistente
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void givenNonexistentCard_whenAuthorizeTransaction_thenReturnsNonexistentCard() throws Exception {
        String transacaoJson = "{ \"numeroCartao\": \"1111111111111111\", \"senhaCartao\": \"1234\", \"valor\": 50.00 }";

        mockMvc.perform(post("/cartoes/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transacaoJson).with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Cartão inexistente"));
    }

    // Teste: Dado que a autenticação não foi fornecida, quando tentar criar um cartão, então retorna não autorizado (401)
    @Test
    void whenNoAuthentication_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"numeroCartao\": \"6549873025634501\", \"senha\": \"1234\" }")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}