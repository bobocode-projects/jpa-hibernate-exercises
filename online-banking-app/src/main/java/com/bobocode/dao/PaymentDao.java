package com.bobocode.dao;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.bobocode.entity.Card;
import com.bobocode.entity.Payment;

public class PaymentDao extends AbstractCrudDao<Payment> {
    public PaymentDao(EntityManagerFactory emf) {
        super(emf);
        entityClass = Payment.class;
    }

    public List<Payment> findAllByCard(Card card) {
        return emExecutor.resultListFromEntityManagerRequest(entityManager -> entityManager
                .createQuery("select p from Payment p where p.card.id=:cardId", Payment.class)
                .setParameter("cardId", card.getId())
                .getResultList());
    }

    public List<Payment> findAllByUser(Long userId) {
        return emExecutor.resultListFromEntityManagerRequest(entityManager -> entityManager
                .createQuery("select p from Payment p where p.card.user.id=:userId", Payment.class)
                .setParameter("userId", userId)
                .getResultList());
    }

    public List<Payment> findAllByUserAndDate(Long userId, LocalDate date) {
        return emExecutor.resultListFromEntityManagerRequest(entityManager -> entityManager
                .createQuery("select p from Payment p where p.card.user.id=:userId and" +
                        " p.transactionTime > :startTime and p.transactionTime< :endTime", Payment.class)
                .setParameter("userId", userId)
                .setParameter("startTime", LocalDateTime.of(date, LocalTime.MIN))
                .setParameter("endTime", LocalDateTime.of(date, LocalTime.MAX))
                .getResultList());
    }

    public List<Payment> findAllPaymentMoreThan(Long userId, long amount) {
        return emExecutor.resultListFromEntityManagerRequest(entityManager -> entityManager
                .createQuery("select p from Payment p where p.card.user.id=:userId and" +
                        " p.amountSpent>:amount", Payment.class)
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .getResultList());
    }
}
