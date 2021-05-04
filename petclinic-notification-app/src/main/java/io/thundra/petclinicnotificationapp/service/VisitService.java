package io.thundra.petclinicnotificationapp.service;

import io.thundra.petclinicnotificationapp.entity.Visit;
import io.thundra.petclinicnotificationapp.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository repository;

    @Transactional
    public void updateVisit(Integer visitId) {
        Visit visit = repository.getOne(visitId);
        visit.setNotificationStatus(true);
        repository.save(visit);
    }
}
