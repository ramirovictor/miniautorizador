package com.vrbeneficios.miniautorizador.repository;


import com.vrbeneficios.miniautorizador.entity.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartaoRepository extends JpaRepository<Cartao, String> {

    Optional<Cartao> findByNumeroCartao(String numeroCartao);

}
