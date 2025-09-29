package com.feature5.pqrs.controller;

import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.repository.PqrsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pqrs")
public class PqrsController {

    @Autowired
    private PqrsRepository pqrsRepository;

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
    public Pqrs crearPqrs(@RequestBody Pqrs pqrs) {
        return pqrsRepository.save(pqrs);
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