package com.vrbeneficios.miniautorizador.service;

import com.vrbeneficios.miniautorizador.dto.CartaoDTO;
import com.vrbeneficios.miniautorizador.dto.TransacaoDTO;
import com.vrbeneficios.miniautorizador.entity.Cartao;
import com.vrbeneficios.miniautorizador.repository.CartaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartaoServiceTest {

    @InjectMocks
    private CartaoService cartaoService;

    @Mock
    private CartaoRepository cartaoRepository;

    @Test
    void criarCartao_deveCriarNovoCartao() {
        CartaoDTO cartaoDTO = new CartaoDTO("6549873025634501", "1234");
        Cartao novoCartao = new Cartao(cartaoDTO.numeroCartao(), cartaoDTO.senha(), BigDecimal.valueOf(500.00));

        when(cartaoRepository.findByNumeroCartao(cartaoDTO.numeroCartao())).thenReturn(Optional.empty());

        when(cartaoRepository.save(any(Cartao.class))).thenReturn(novoCartao);

        Cartao cartaoCriado = cartaoService.criarCartao(cartaoDTO);

        assertEquals(cartaoDTO.numeroCartao(), cartaoCriado.getNumeroCartao());

        verify(cartaoRepository, times(1)).save(any(Cartao.class));
    }

    @Test
    void obterSaldo_deveRetornarSaldoCorreto() {
        Cartao cartao = new Cartao("6549873025634501", "1234", BigDecimal.valueOf(500.00));
        when(cartaoRepository.findById(cartao.getNumeroCartao())).thenReturn(Optional.of(cartao));

        BigDecimal saldo = cartaoService.obterSaldo(cartao.getNumeroCartao());

        assertEquals(BigDecimal.valueOf(500.00), saldo);
    }

    @Test
    void autorizarTransacao_deveDebitarSaldo() {
        Cartao cartao = new Cartao("6549873025634501", "1234", BigDecimal.valueOf(500.00));
        TransacaoDTO transacaoDTO = new TransacaoDTO("6549873025634501", "1234", BigDecimal.valueOf(100.00));
        when(cartaoRepository.findById(transacaoDTO.numeroCartao())).thenReturn(Optional.of(cartao));

        cartaoService.autorizarTransacao(transacaoDTO);

        assertEquals(BigDecimal.valueOf(400.00), cartao.getSaldo());
        verify(cartaoRepository, times(1)).save(cartao);
    }
}
