package com.ttn.bootcampProject.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetAllCustomerInfoDto {
    private long id;
    private String fullName;
    private String email;
    private boolean isActive;

    public GetAllCustomerInfoDto(long id, String fullName, String email, boolean isActive) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.isActive = isActive;
    }
}
