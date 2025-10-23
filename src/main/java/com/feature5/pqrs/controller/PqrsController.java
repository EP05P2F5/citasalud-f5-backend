package com.feature5.pqrs.controller;

import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.repository.PqrsRepository;
import com.feature5.pqrs.service.PqrsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pqrs")
public class PqrsController {

    private static final Logger logger = LoggerFactory.getLogger(PqrsController.class);

    @Autowired
    private PqrsRepository pqrsRepository;

    private final PqrsService pqrsService;

    public PqrsController(PqrsRepository pqrsRepository, PqrsService pqrsService) {
        this.pqrsRepository = pqrsRepository;
        this.pqrsService = pqrsService;
    }

    //Listar todas las PQRS
    @GetMapping
    public List<Pqrs> listarPqrs() {
        return pqrsRepository.findAll();
    }

    //Obtener PQRS por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pqrs> obtenerPqrsPorId(@PathVariable Long id) {
        Optional<Pqrs> pqrs = pqrsRepository.findById(id);
        return pqrs.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //Crear nueva PQRS
    @PostMapping
    public ResponseEntity<Pqrs> crearPqrs(@Valid @RequestBody Pqrs pqrs) {
        Pqrs saved = pqrsService.createPqrs(pqrs);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    //Actualizar PQRS
    @PutMapping("/{id}")
    public ResponseEntity<Pqrs> actualizarPqrs(@PathVariable Long id, @RequestBody Pqrs pqrsActualizado) {
        Optional<Pqrs> pqrsExistente = pqrsRepository.findById(id);

        if (pqrsExistente.isPresent()) {
            Pqrs pqrs = pqrsExistente.get();
            pqrs.setIdUsuario(pqrsActualizado.getIdUsuario());
            pqrs.setIdTipo(pqrsActualizado.getIdTipo());
            pqrs.setDescripcion(pqrsActualizado.getDescripcion());
            pqrs.setFechaDeGeneracion(pqrsActualizado.getFechaDeGeneracion());
            pqrs.setRadicado(pqrsActualizado.getRadicado());
            pqrs.setEstado(pqrsActualizado.getEstado());
            pqrs.setFechaDeRespuesta(pqrsActualizado.getFechaDeRespuesta());
            pqrs.setRespuesta(pqrsActualizado.getRespuesta());

            return ResponseEntity.ok(pqrsRepository.save(pqrs));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Eliminar PQRS
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPqrs(@PathVariable Long id) {
        if (pqrsRepository.existsById(id)) {
            pqrsRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Buscar PQRS por estado
    @GetMapping("/estado/{estado}")
    public List<Pqrs> buscarPorEstado(@PathVariable String estado) {
        return pqrsRepository.findByEstado(estado);
    }

    //Buscar PQRS por usuario
    @GetMapping("/usuario/{idUsuario}")
    public List<Pqrs> buscarPorUsuario(@PathVariable Long idUsuario) {
        return pqrsRepository.findByIdUsuario(idUsuario);
    }
}