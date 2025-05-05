package com.mylstech.rentro.model;

import com.mylstech.rentro.util.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_users", indexes = {
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_phone", columnList = "phone", unique = true)
})
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(unique = true)
    private String phone;
    private boolean verified;

    // Keep the simple address field for backward compatibility
    @Column(length = 500)
    private String address;

    // Add the relationship to Address entities
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Role role;
    
    // Helper method to add an address
    public void addAddress(Address address) {
        addresses.add(address);
        address.setUser(this);
    }
    
    // Helper method to remove an address
    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setUser(null);
    }
    
    // Helper method to get default address
    public Address getDefaultAddress() {
        return addresses.stream()
                .filter(Address::isDefault)
                .findFirst()
                .orElse(addresses.isEmpty() ? null : addresses.get(0));
    }
}

