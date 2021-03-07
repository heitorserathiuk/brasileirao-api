package br.com.hs.brasileiraoapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hs.brasileiraoapi.dto.EquipeResponseDTO;
import br.com.hs.brasileiraoapi.entity.Equipe;
import br.com.hs.brasileiraoapi.exception.NotFoundException;
import br.com.hs.brasileiraoapi.exception.StandardError;
import br.com.hs.brasileiraoapi.repository.EquipeRepository;
import br.com.hs.brasileiraoapi.service.EquipeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

@Api("API de equipes")
@RestController
@RequestMapping("/api/v1/equipes")
public class EquipeController {

	@Autowired
	private EquipeService equipeService;
		
	@ApiOperation(value = "Buscar equipe por id")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = Equipe.class),
			@ApiResponse(code = 400, message = "Bad request", response = StandardError.class),
			@ApiResponse(code = 401, message = "Unauthorized", response = StandardError.class),
			@ApiResponse(code = 403, message = "Forbidden", response = StandardError.class),
			@ApiResponse(code = 404, message = "Not found", response = StandardError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = StandardError.class)
	})
	@GetMapping("/{id}")
	public ResponseEntity<Equipe> buscarEquipeId(@PathVariable("id") Long id){
		return ResponseEntity.ok().body(equipeService.buscarEquipeId(id));
	}
	
	@ApiOperation(value = "Listar equipes")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = EquipeResponseDTO.class),
			@ApiResponse(code = 400, message = "Bad request", response = StandardError.class),
			@ApiResponse(code = 401, message = "Unauthorized", response = StandardError.class),
			@ApiResponse(code = 403, message = "Forbidden", response = StandardError.class),
			@ApiResponse(code = 404, message = "Not found", response = StandardError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = StandardError.class)
	})
	@GetMapping
	public ResponseEntity<EquipeResponseDTO> listarEquipes(){
		return ResponseEntity.ok().body(equipeService.listarEquipes());
	}
}
