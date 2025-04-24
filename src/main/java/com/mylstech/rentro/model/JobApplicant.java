package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobApplicantId;
    private String name;
    private String email;
    private String phone;
    private String resume;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private JobPost jobPost;
}
