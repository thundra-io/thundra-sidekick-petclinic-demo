package io.thundra.petclinicnotificationapp.repository;

import io.thundra.petclinicnotificationapp.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository<Visit, Integer> {
}
