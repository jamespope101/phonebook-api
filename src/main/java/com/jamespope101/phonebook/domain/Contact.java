package com.jamespope101.phonebook.domain;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

/**
 * Created by jpope on 07/02/2018.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private Title title;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name") // can be null
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Singular
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "contact_phone_number",
        joinColumns = {@JoinColumn(name = "contact", nullable = false, updatable = false)},
        inverseJoinColumns = {@JoinColumn(name = "phone_number", nullable = false, updatable = false)})
    private Set<PhoneNumber> phoneNumbers;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "address", nullable = false)
    private Address address;

}
