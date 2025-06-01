package com.mylstech.rentro.model;

import com.mylstech.rentro.util.RequestQuotationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private RequestQuotationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now ( );
        updatedAt = createdAt;

    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now ( );
    }
}
