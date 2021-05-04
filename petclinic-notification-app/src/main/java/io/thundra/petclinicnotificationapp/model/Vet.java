package io.thundra.petclinicnotificationapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vet {
    private int id;
    private String firstName;
    private String lastName;
    private List<Specialty> specialties;
}
