package com.ttn.bootcampProject.helpingclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfo {
    private long id;
    private String fullName;
    private String email;
    private boolean isActive;
}
