package com.vrbeneficios.miniautorizador.controller;


import com.vrbeneficios.miniautorizador.dto.CartaoDTO;
import com.vrbeneficios.miniautorizador.dto.TransacaoDTO;
import com.vrbeneficios.miniautorizador.entity.Cartao;
import com.vrbeneficios.miniautorizador.service.CartaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private final CartaoService cartaoService;

    public CartaoController(CartaoService cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PostMapping
    public ResponseEntity<CartaoDTO> criarCartao(@RequestBody CartaoDTO cartaoDTO) {
        System.out.println("Creating card: " + cartaoDTO);
        try {
            Cartao novoCartao = cartaoService.criarCartao(cartaoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CartaoDTO(novoCartao.getNumeroCartao(), novoCartao.getSenha()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(cartaoDTO);
        }
    }

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> obterSaldo(@PathVariable String numeroCartao) {
        try {
            BigDecimal saldo = cartaoService.obterSaldo(numeroCartao);
            return ResponseEntity.ok(saldo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/transacoes")
    public ResponseEntity<String> realizarTransacao(@RequestBody TransacaoDTO transacaoDTO) {
        try {
            cartaoService.autorizarTransacao(transacaoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("OK");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }
}
