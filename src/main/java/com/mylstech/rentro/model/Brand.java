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
@Table(name = "Brands", uniqueConstraints = {
        @UniqueConstraint(name = "uk_brand_name", columnNames = {"name"})
})
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandId;

    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Product> products;

    // Change from OneToMany to ManyToOne
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "image_id")
    private Image image;


}
