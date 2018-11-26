package com.bobocode.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "amount_spent", nullable = false)
    private long amountSpent;

    @Column(name = "transaction_time", nullable = false)
    private Timestamp transactionTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "card_id")
    private Card card;
}
