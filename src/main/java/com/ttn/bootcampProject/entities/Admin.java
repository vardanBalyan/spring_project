package com.ttn.bootcampProject.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User{

    private String contact;
}
