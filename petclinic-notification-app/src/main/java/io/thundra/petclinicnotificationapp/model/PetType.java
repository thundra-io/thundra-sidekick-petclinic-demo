package io.thundra.petclinicnotificationapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetType {
    private Integer id;
    private String name;
    private boolean isNew;
}
