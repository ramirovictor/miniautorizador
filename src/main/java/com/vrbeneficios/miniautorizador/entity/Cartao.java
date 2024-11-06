package com.vrbeneficios.miniautorizador.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class Cartao {

        @Id
        private String numeroCartao;
        private String senha;
        private BigDecimal saldo;

        public Cartao(String numeroCartao, String senha, BigDecimal saldo) {
            this.numeroCartao = numeroCartao;
            this.senha = senha;
            this.saldo = saldo;
        }

        public boolean possuiSaldoSuficiente(BigDecimal valor) {
            return this.saldo.compareTo(valor) >= 0;
        }

        public void debitarSaldo(BigDecimal valor) {
            if (!possuiSaldoSuficiente(valor)) {
                throw new IllegalArgumentException("Saldo insuficiente");
            }
            this.saldo = this.saldo.subtract(valor);
        }
    }

