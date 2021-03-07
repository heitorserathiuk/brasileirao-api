package br.com.hs.brasileiraoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hs.brasileiraoapi.entity.Partida;

@Repository
public interface PartidaRepository extends JpaRepository<Partida,Long>{

}
