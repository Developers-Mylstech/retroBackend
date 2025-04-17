package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "specification_fields")
public class SpecificationField {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long specificationFieldId;
    private String name;

}
