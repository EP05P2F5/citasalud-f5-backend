package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.EstadoDTO;
import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.mapper.EstadoMapper;
import com.feature5.pqrs.repository.EstadoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/estados")
public class EstadoController {

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private EstadoMapper estadoMapper;

    @Operation(summary = "Listar todos los estados", description = "Obtiene todos los estados disponibles en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de estados obtenida exitosamente")
    })
    @GetMapping
    public List<EstadoDTO> listarTodos() {
        return estadoRepository.findAll()
                .stream()
                .map(estadoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener estado por ID", description = "Obtiene un estado específico por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado encontrado"),
        @ApiResponse(responseCode = "404", description = "Estado no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EstadoDTO> obtenerPorId(@PathVariable Integer id) {
        Optional<Estado> estado = estadoRepository.findById(id);
        return estado.map(e -> ResponseEntity.ok(estadoMapper.toDto(e)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }
}