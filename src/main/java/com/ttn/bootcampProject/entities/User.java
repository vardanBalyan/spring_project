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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
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

   @OneToMany(cascade = CascadeType.ALL)
   @JoinTable(name = "user_role",
   joinColumns = @JoinColumn(name = "user_id"),
   inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

   public void addRoles(Role role) {
       if (role != null) {
           if (roles == null) {
               roles = new HashSet<>();
           }
           roles.add(role);
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
