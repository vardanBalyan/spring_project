package com.ttn.bootcampProject.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(unique = true)
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String confirmPassword;
    private boolean isDeleted;
    private boolean isActive;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<Address> addresses;

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<UserRole> userRoles;

    public User() {
        this.setActive(false);
    }

    public void addRoles(UserRole userRole) {
       if (userRole != null) {
           if (userRoles == null) {
               userRoles = new HashSet<>();
           }
           userRoles.add(userRole);
           userRole.setUser(this);
       }
   }

    public void addAddress(Address address)
    {
        if(address != null)
        {
            if(addresses == null)
            {
                addresses = new ArrayList<>();
            }
            addresses.add(address);
        }
    }

}
