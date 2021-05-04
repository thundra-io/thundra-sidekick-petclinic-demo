package io.thundra.petclinicnotificationapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String petName;
    private PetType petType;
    private String telephone;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDate;
    private String visitDescription;
    private Integer visitId;
}
