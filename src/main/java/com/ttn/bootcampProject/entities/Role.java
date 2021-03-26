package com.ttn.bootcampProject.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String authority;
    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles;

    public void addRoles(UserRole userRole) {
        if (userRole != null) {
            if (userRoles == null) {
                userRoles = new HashSet<>();
            }
            userRoles.add(userRole);
        }
    }
}
