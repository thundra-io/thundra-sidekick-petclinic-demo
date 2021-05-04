package io.thundra.petclinicnotificationapp.service;


import io.thundra.petclinicnotificationapp.model.Specialty;
import io.thundra.petclinicnotificationapp.model.Vet;
import io.thundra.petclinicnotificationapp.repository.VetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VetService {
    private final VetRepository repository;

    public List<Vet> findVetBySpecialty(String specialtyName) {
        List<io.thundra.petclinicnotificationapp.entity.Vet> entities = repository.findBySpecialties_Name(specialtyName);
        return entities.stream().map(entity -> {
            List<Specialty> specialties = entity.getSpecialties().stream()
                    .map(specialty -> new Specialty(specialty.getId(), specialty.getName()))
                    .collect(Collectors.toList());
            return new Vet(entity.getId(), entity.getFirstName(), entity.getLastName(), specialties);
        }).collect(Collectors.toList());
    }
}
