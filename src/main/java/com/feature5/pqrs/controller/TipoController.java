package com.feature5.pqrs.controller;

import com.feature5.pqrs.entities.Tipo;
import com.feature5.pqrs.repository.TipoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tipos")
public class TipoController {

    @Autowired
    private TipoRepository tipoRepository;

    @Operation(summary = "Listar todos los tipos", description = "Obtiene todos los tipos disponibles en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de tipos obtenida exitosamente")
    })
    @GetMapping
    public List<Tipo> listarTodos() {
        return tipoRepository.findAll();
    }

    @Operation(summary = "Obtener tipo por ID", description = "Obtiene un tipo específico por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tipo encontrado"),
        @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Tipo> obtenerPorId(@PathVariable Integer id) {
        Optional<Tipo> tipo = tipoRepository.findById(id);
        return tipo.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
}