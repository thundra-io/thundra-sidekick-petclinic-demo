package io.thundra.petclinicapp.visit;

import io.thundra.petclinicapp.pet.PetType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class VisitEvent {

	private String petName;

	private PetType petType;

	private String telephone;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate visitDate;

	private String visitDescription;

	private Integer visitId;

	public VisitEvent(String petName, PetType petType, String telephone, LocalDate visitDate, String visitDescription,
			Integer visitId) {
		this.petName = petName;
		this.petType = petType;
		this.telephone = telephone;
		this.visitDate = visitDate;
		this.visitDescription = visitDescription;
		this.visitId = visitId;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public PetType getPetType() {
		return petType;
	}

	public void setPetType(PetType petType) {
		this.petType = petType;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public LocalDate getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(LocalDate visitDate) {
		this.visitDate = visitDate;
	}

	public String getVisitDescription() {
		return visitDescription;
	}

	public void setVisitDescription(String visitDescription) {
		this.visitDescription = visitDescription;
	}

	public Integer getVisitId() {
		return visitId;
	}

	public void setVisitId(Integer visitId) {
		this.visitId = visitId;
	}

}
