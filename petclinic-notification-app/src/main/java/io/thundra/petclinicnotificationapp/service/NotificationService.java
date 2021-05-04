package io.thundra.petclinicnotificationapp.service;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import io.thundra.petclinicnotificationapp.model.NotificationEvent;
import io.thundra.petclinicnotificationapp.model.Vet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final String MESSAGE = "An appointment has been created with your veterinarian %s for your pet named %s on %s";

    private final AmazonSNSAsync snsClient;

    private final VetService vetService;

    private final VisitService visitService;

    private void sendSMS(String message, String phoneNumber) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber));
        log.info("Sending message id " + result.getMessageId());
    }

    private Vet selectVet(List<Vet> vets) {
        return vets.get(new Random().nextInt(vets.size()));
    }

    private String correctingPhoneNumber(String telephone) {
        if (!telephone.startsWith("+")) {
            if (telephone.startsWith("0")) {
                telephone = "+9" + telephone;
            } else {
                telephone = "+90" + telephone;
            }
        }
        return telephone;
    }

    public void sendNotification(NotificationEvent event) {
        List<Vet> vets = vetService.findVetBySpecialty(event.getPetType().getName());
        String telephone = correctingPhoneNumber(event.getTelephone());
        if (!vets.isEmpty()) {
            Vet vet = selectVet(vets);
            String message = String.format(MESSAGE, vet.getFirstName(), event.getPetName(), event.getVisitDate());
            sendSMS(message, telephone);
            visitService.updateVisit(event.getVisitId());
        }
    }
}
