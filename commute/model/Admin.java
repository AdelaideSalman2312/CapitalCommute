package com.CapitalCommute.commute.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Person {
    
    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
    private AdminProfile adminProfile;
}