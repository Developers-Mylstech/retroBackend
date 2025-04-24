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
@Table(name = "request_quotations")

public class RequestQuotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestQuotationId;
    private String name;
    private String mobile;
    private String companyName;
    private String location;
    @ElementCollection
    @CollectionTable(name = "request_quotation_image_urls",
            joinColumns = @JoinColumn(name = "request_quotation_image_id"))
    @Column(name = "image_url")
    private List<String> productImages;


}
