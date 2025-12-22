package com.igsm.chatbot.controller;

import com.igsm.chatbot.model.Diplomatura;
import com.igsm.chatbot.repository.DiplomaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diplomaturas")
@CrossOrigin(origins = "*") // Allow frontend access
public class DiplomaturaController {

    @Autowired // Autowired es un annotation que permite inyectar dependencias. ej: inyecta la
               // dependencia de DiplomaturaRepository
    private DiplomaturaRepository diplomaturaRepository; // Inyecta la dependencia de DiplomaturaRepository, encargada
                                                         // de gestionar las operaciones CRUD de la entidad Diplomatura,
                                                         // definida en DiplomaturaRepository.java

    @GetMapping // Indica que el método getAll() es un endpoint GET que retorna una lista de
                // todos los diplomaturas
    public List<Diplomatura> getAll() {
        return diplomaturaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diplomatura> getById(@PathVariable Long id) {
        return diplomaturaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping // Indica que el método create() es un endpoint POST que crea una nueva
                 // diplomatura
    public Diplomatura create(@RequestBody Diplomatura diplomatura) {
        return diplomaturaRepository.save(diplomatura);
    }

    @PutMapping("/{id}") // Indica que el método update() es un endpoint PUT que actualiza una
                         // diplomatura existente
    public ResponseEntity<Diplomatura> update(@PathVariable Long id, @RequestBody Diplomatura diplomaturaDetails) {
        return diplomaturaRepository.findById(id)
                .map(diplomatura -> {
                    diplomatura.setName(diplomaturaDetails.getName());
                    diplomatura.setDescription(diplomaturaDetails.getDescription());
                    diplomatura.setContent(diplomaturaDetails.getContent());
                    return ResponseEntity.ok(diplomaturaRepository.save(diplomatura));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // Indica que el método delete() es un endpoint DELETE que elimina una
                            // diplomatura existente
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (diplomaturaRepository.existsById(id)) {
            diplomaturaRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
