package com.example.hibernateonetoonedemo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wives")
@Getter
@Setter
@NoArgsConstructor
public class Wife {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "husband_id", nullable = false, unique = true)
    private Husband husband;

    public Wife(String name) {
        this.name = name;
    }

    public void setHusband(Husband husband) {
        this.husband = husband;
        if (husband != null && husband.getWife() != this) {
            husband.setWife(this);
        }
    }
}
