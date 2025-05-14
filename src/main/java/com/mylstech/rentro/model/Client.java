package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name ="clients")
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;
    private String name;

    // New field for Image entity integration with ManyToOne relationship
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    private Image image;
    
//    @Column(name = "image_url")
//    @Deprecated
//    private String imageUrl;
//
    // Helper methods for backward compatibility
//    /**
//     * @deprecated This method is only for backward compatibility.
//     * Use getImage().getImageUrl() instead.
//     */
//    @Deprecated
//    public String getImageUrl() {
//        return this.image != null ? this.image.getImageUrl() : null;
//    }
//
//    /**
//     * @deprecated This method is only for backward compatibility.
//     * Use setImage(Image) instead.
//     */
//    @Deprecated
//    public void setImageUrl(String imageUrl) {
//        // This is kept for backward compatibility
//        // In new code, use setImage(Image) instead
//    }
}
