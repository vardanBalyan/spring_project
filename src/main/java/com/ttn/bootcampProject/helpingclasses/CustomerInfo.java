package com.ttn.bootcampProject.helpingclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerInfo {
    private long id;
    private String fullName;
    private String email;
    private boolean isActive;

    public CustomerInfo(long id, String fullName, String email, boolean isActive) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.isActive = isActive;
    }
}
