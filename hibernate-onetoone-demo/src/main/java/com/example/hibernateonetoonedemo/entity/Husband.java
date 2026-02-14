package com.example.hibernateonetoonedemo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "husbands")
@Getter
@Setter
@NoArgsConstructor
public class Husband {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(mappedBy = "husband", cascade = CascadeType.ALL)
    private Wife wife;

    public Husband(String name) {
        this.name = name;
    }

    public void setWife(Wife wife) {
        this.wife = wife;
        if (wife != null && wife.getHusband() != this) {
            wife.setHusband(this);
        }
    }
}
