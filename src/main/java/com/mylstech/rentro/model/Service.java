package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "services")

public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServiceField ots;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServiceField mmc;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServiceField amcBasic;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServiceField amcGold;

}
