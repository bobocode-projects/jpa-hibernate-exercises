package com.bobocode.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "cvv2", nullable = false)
    private int cvv2;

    @Column(name = "balance")
    private long balance;

    @ManyToOne(optional = false)
    private User user;

    @OneToMany(mappedBy = "card", fetch = FetchType.EAGER, orphanRemoval = true)
    @Setter(AccessLevel.PRIVATE)
    private List<Payment> payments = new ArrayList<>();

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setCard(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
    }
}
