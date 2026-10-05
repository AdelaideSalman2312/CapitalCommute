package com.CapitalCommute.commute.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "national_id")
public class Client extends Person {

    @Column(name = "client_id")
    private UUID clientId;

    @PrePersist
    public void generateId() {
        if (clientId == null) {
            clientId = UUID.randomUUID();
        }
    }
}