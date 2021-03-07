package br.com.hs.brasileiraoapi.service;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hs.brasileiraoapi.dto.EquipeDTO;
import br.com.hs.brasileiraoapi.dto.EquipeResponseDTO;
import br.com.hs.brasileiraoapi.dto.PartidaDTO;
import br.com.hs.brasileiraoapi.dto.PartidaGoogleDTO;
import br.com.hs.brasileiraoapi.dto.PartidaResponseDTO;
import br.com.hs.brasileiraoapi.entity.Equipe;
import br.com.hs.brasileiraoapi.entity.Partida;
import br.com.hs.brasileiraoapi.exception.BadRequestException;
import br.com.hs.brasileiraoapi.exception.NotFoundException;
import br.com.hs.brasileiraoapi.repository.EquipeRepository;
import br.com.hs.brasileiraoapi.repository.PartidaRepository;

@Service
public class PartidaService {

	@Autowired
	private PartidaRepository partidaRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private EquipeService equipeService;

	public Partida buscarPartidaPorId(Long id) {
		return partidaRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Nenhuma partida encontrada com o id informado: "+id));
	}
	
	public PartidaResponseDTO listarPartidas() {
		PartidaResponseDTO partidas = new PartidaResponseDTO();
		partidas.setPartidas(partidaRepository.findAll());		
		return partidas;
	}

	public Partida inserirPartida(@Valid PartidaDTO dto) {		
		Partida partida = modelMapper.map(dto, Partida.class);		
		partida.setEquipeCasa(equipeService.buscarEquipePorNome(dto.getNomeEquipeCasa()));
		partida.setEquipeVisitante(equipeService.buscarEquipePorNome(dto.getNomeEquipeVisitante()));
		return salvarPartida(partida);
	}
	
	private Partida salvarPartida(Partida partida) {
		return partidaRepository.save(partida);	
	}

	public void alterarPartida(Long id, PartidaDTO dto) {
		boolean exists = partidaRepository.existsById(id);
		if(!exists) {
			throw new BadRequestException("Não foi possível alterar a partida: ID inexistente");
		}
		Partida partida = buscarPartidaPorId(id);		
		partida.setId(id);
		partida.setEquipeCasa(equipeService.buscarEquipePorNome(dto.getNomeEquipeCasa()));
		partida.setEquipeVisitante(equipeService.buscarEquipePorNome(dto.getNomeEquipeVisitante()));
		partida.setDataHoraPartida(dto.getDataHoraPartida());
		partida.setLocalPartida(dto.getLocalPartida());
		salvarPartida(partida);
	}
	
	public void atualizaPartida(Partida partida, PartidaGoogleDTO partidaGoogle) {
		partida.setPlacarEquipeCasa(partidaGoogle.getPlacarEquipeCasa());
		partida.setPlacarEquipeVisitante(partidaGoogle.getPlacarEquipeVisitante());
		partida.setGolsEquipeCasa(partidaGoogle.getGolsEquipeCasa());
		partida.setGolsEquipeVisitante(partidaGoogle.getGolsEquipeVisitante());
		partida.setPlacarEstendidoEquipeCasa(partidaGoogle.getPlacarEstendidoEquipeCasa());
		partida.setPlacarEstendidoEquipeVisitante(partidaGoogle.getPlacarEstendidoEquipeVisitante());
		partida.setTempoPartida(partidaGoogle.getTempoPartida());
		salvarPartida(partida);
	}

	public Integer buscarQuantidadePartidasPeriodo() {
		return partidaRepository.buscarQuantidadePartidasPeriodo();
	}

	public List<Partida> listarPartidasPeriodo() {
		return partidaRepository.listarPartidasPeriodo();
	}




}
