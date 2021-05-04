
package io.thundra.petclinicnotificationapp.repository;

import io.thundra.petclinicnotificationapp.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface VetRepository extends JpaRepository<Vet, Integer> {
    List<Vet> findBySpecialties_Name(String specialtyName);
}
