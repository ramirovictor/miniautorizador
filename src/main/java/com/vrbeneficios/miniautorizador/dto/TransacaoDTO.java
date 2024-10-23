package com.vrbeneficios.miniautorizador.dto;


import java.math.BigDecimal;

public record TransacaoDTO(
        String numeroCartao,
        String senhaCartao,
        BigDecimal valor
) {
}

