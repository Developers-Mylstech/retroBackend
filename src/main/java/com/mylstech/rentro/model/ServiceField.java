package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "service_fields")
public class ServiceField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceFieldId;
    private Integer limitedTimePeriods;
    private Double price;
    @ElementCollection
    @CollectionTable(name = "service_field_benefits",
            joinColumns = @JoinColumn(name = "service_field_id"))
    @Column(name = "benefit")
    private List<String> benefits;
}
