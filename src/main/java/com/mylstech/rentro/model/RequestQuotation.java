package com.mylstech.rentro.model;

import com.mylstech.rentro.util.RequestQuotationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    
    @Column(unique = true, nullable = false)
    private String requestQuotationCode;
    
    private String name;
    private String mobile;
    private String companyName;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    // Keep the old field for backward compatibility during migration
    @ElementCollection
    @CollectionTable(name = "request_quotation_image_urls",
            joinColumns = @JoinColumn(name = "request_quotation_image_id"))
    @Column(name = "image_url")
    @Deprecated
    private List<String> productImages;

    @Enumerated(EnumType.STRING)
    private RequestQuotationStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;

    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
