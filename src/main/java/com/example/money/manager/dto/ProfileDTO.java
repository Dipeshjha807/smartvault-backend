package com.example.money.manager.dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {
    private  Long id;
    private String FullName;

    private String email;
    private String  password;
    private String profieImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
//basinishrabani@gmail.com