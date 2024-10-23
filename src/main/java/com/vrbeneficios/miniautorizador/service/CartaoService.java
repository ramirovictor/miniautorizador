package com.vrbeneficios.miniautorizador.service;

import com.vrbeneficios.miniautorizador.dto.CartaoDTO;
import com.vrbeneficios.miniautorizador.dto.TransacaoDTO;
import com.vrbeneficios.miniautorizador.entity.Cartao;
import com.vrbeneficios.miniautorizador.repository.CartaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartaoService {



    private final CartaoRepository cartaoRepository;

    public CartaoService(CartaoRepository cartaoRepository) {
        this.cartaoRepository = cartaoRepository;
    }

    public Cartao criarCartao(CartaoDTO cartaoDTO) {
        Optional<Cartao> cartaoExistente = cartaoRepository.findByNumeroCartao(cartaoDTO.numeroCartao());
        if (cartaoExistente.isPresent()) {
            throw new IllegalArgumentException("Cartão já existe");
        }

        Cartao novoCartao = new Cartao(cartaoDTO.numeroCartao(), cartaoDTO.senha(), BigDecimal.valueOf(500.00));
        return cartaoRepository.save(novoCartao);
    }

    public BigDecimal obterSaldo(String numeroCartao) {
        Cartao cartao = cartaoRepository.findById(numeroCartao).orElseThrow(() -> new IllegalArgumentException("Cartão inexistente"));
        return cartao.getSaldo();
    }

    public void autorizarTransacao(TransacaoDTO transacaoDTO) {
        Cartao cartao = cartaoRepository.findById(transacaoDTO.numeroCartao())
                .orElseThrow(() -> new IllegalArgumentException("Cartão inexistente"));

        if (!cartao.getSenha().equals(transacaoDTO.senhaCartao())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        if (cartao.getSaldo().compareTo(transacaoDTO.valor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        cartao.setSaldo(cartao.getSaldo().subtract(transacaoDTO.valor()));
        cartaoRepository.save(cartao);
    }
}

