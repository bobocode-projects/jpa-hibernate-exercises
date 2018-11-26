package com.bobocode.dao;

import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import com.bobocode.entity.Card;
import com.bobocode.entity.Payment;
import com.bobocode.entity.User;

public class PaymentDao extends AbstractCrudDao<Payment> {
    public PaymentDao(EntityManagerFactory emf) {
        super(emf);
        entityClass = Payment.class;
    }

    public List<Payment> findAllByCard(Card card) {
//        return card.getPayments();
        return emExecutor.resultListFromEntityManagerRequest(entityManager -> entityManager
                .createQuery("select p from Payment p where p.card.id=:cardId", Payment.class)
                .setParameter("cardId", card.getId())
                .getResultList());
    }

    public List<User> findAllByUser(Long userId) {
        return emExecutor.resultListFromEntityManagerRequest(entityManager -> entityManager
                .createQuery("select p from Payment p where p.card.user.id=:userId", User.class)
                .setParameter("userId", userId)
                .getResultList());
    }

    public List<Payment> findAllByUserAndDate(Long userId, LocalDate date) {
        return emExecutor.resultListFromEntityManagerRequest(entityManager -> entityManager
                .createQuery("select p from Payment p where p.card.user.id=:userId and" +
                        " p.transactionTime between :startTime and :endTime", Payment.class)
                .setParameter("userId", userId)
                .setParameter("startTime", Timestamp.valueOf(LocalDateTime.of(date, LocalTime.MIN)))
                .setParameter("endTime", Timestamp.valueOf(LocalDateTime.of(date, LocalTime.MAX)))
                .getResultList());
    }

    private List<Payment> findAllPaymentMoreThan(Long userId, long amount) {
        return emExecutor.resultListFromEntityManagerRequest(entityManager -> entityManager
                .createQuery("select p from Payment p where p.card.user.id=:userId and" +
                        " p.amountSpent>:amount", Payment.class)
                .setParameter("userId", userId)
                .setParameter("amount", amount)
                .getResultList());
    }
}
