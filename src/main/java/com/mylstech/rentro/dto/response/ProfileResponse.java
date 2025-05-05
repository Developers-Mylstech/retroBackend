package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.util.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private boolean verified;
    
    public ProfileResponse(AppUser user) {
        this.id = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.role = user.getRole();
        this.verified = user.isVerified();
    }
}