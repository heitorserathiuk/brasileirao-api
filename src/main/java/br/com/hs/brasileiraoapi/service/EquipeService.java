package br.com.hs.brasileiraoapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.hs.brasileiraoapi.dto.EquipeResponseDTO;
import br.com.hs.brasileiraoapi.entity.Equipe;
import br.com.hs.brasileiraoapi.exception.NotFoundException;
import br.com.hs.brasileiraoapi.exception.StandardError;
import br.com.hs.brasileiraoapi.repository.EquipeRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;

@Service
public class EquipeService {

	@Autowired
	private EquipeRepository equipeRepository;

	public Equipe buscarEquipeId(Long id) {
		return equipeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Nenhuma equipe encontrada com o id informado: "+id));
	}

	public EquipeResponseDTO listarEquipes() {
		EquipeResponseDTO equipes = new EquipeResponseDTO();
		equipes.setEquipes(equipeRepository.findAll());		
		return equipes;
	}

}
