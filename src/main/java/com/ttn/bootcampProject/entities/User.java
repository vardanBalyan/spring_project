package com.ttn.bootcampProject.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.util.HashSet;
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
    private String password;
    private boolean isDeleted;
    private boolean isActive;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Set<Address> addresses;

   @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<UserRole> userRoles;

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
                addresses = new HashSet<>();
            }
            addresses.add(address);
        }
    }

}
