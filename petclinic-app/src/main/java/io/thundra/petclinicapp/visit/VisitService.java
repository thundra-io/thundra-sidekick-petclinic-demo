package io.thundra.petclinicapp.visit;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.thundra.petclinicapp.configuration.ThundraConfig;
import io.thundra.petclinicapp.pet.Pet;
import io.thundra.petclinicapp.pet.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VisitService {

	Logger logger = LoggerFactory.getLogger(VisitService.class);

	private final VisitRepository repository;

	private final PetRepository petRepository;

	private final AmazonSQSAsync sqsClient;

	private final ThundraConfig config;

	private final ObjectMapper mapper;

	public VisitService(VisitRepository repository, PetRepository petRepository, AmazonSQSAsync sqsClient,
			ThundraConfig config, ObjectMapper mapper) {
		this.repository = repository;
		this.petRepository = petRepository;
		this.sqsClient = sqsClient;
		this.config = config;
		this.mapper = mapper;
	}

	@Transactional
	public void save(Visit request) {
		request = repository.save(request);
		Pet pet = petRepository.findById(request.getPetId());
		VisitEvent event = new VisitEvent(pet.getName(), pet.getType(), pet.getOwner().getTelephone(),
				request.getDate(), request.getDescription(), request.getId());
		try {
			sqsClient.sendMessage(config.getQueueUrl(), mapper.writeValueAsString(event));
		}
		catch (JsonProcessingException e) {
			logger.error("Error occurred sending sqs message ", e);
		}
	}

	public List<Visit> findByPetId(int petId) {
		return repository.findByPetId(petId);
	}

}
