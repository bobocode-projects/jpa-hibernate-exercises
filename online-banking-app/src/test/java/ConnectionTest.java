import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.bobocode.dao.CardDao;
import com.bobocode.dao.PaymentDao;
import com.bobocode.dao.UserDao;
import com.bobocode.entity.Card;
import com.bobocode.entity.Payment;
import com.bobocode.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ConnectionTest {
    private static EntityManagerFactory emf;
    private static UserDao userDao;
    private static CardDao cardDao;
    private static PaymentDao paymentDao;

    @BeforeAll
    static void setUp() {
        emf = Persistence.createEntityManagerFactory("OnlineBanking");
        userDao = new UserDao(emf);
        cardDao = new CardDao(emf);
        paymentDao = new PaymentDao(emf);
    }

    @Test
    void connectionToDbByEntityManagerFactory() {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Test
    void userCrudTest() {
        User user = defaultUser("user test", 1);
        userDao.save(user);
        assertTrue(userDao.findAll() != null);
        user.setLastName("Updated");
        userDao.update(user);
        User user1 = userDao.find(user.getId());
        assertEquals("Updated", user1.getLastName());
        userDao.remove(user);
        assertNull(userDao.find(user.getId()));
    }

    @Test
    void cardCrudTest() {
        User user = defaultUser("card test", 2);
        userDao.save(user);
        Card card = defaultCard(user,"2");
        cardDao.save(card);
        assertEquals(card, cardDao.find(card.getId()));
        card.setNumber("Updated Number");
        cardDao.update(card);
        Card updatedCard = cardDao.find(card.getId());
        assertEquals("Updated Number", updatedCard.getNumber());
        cardDao.remove(card);
        assertNull(cardDao.find(card.getId()));
    }

    @Test
    void paymentCrudTest() {
        User user = defaultUser("payment", 3);
        userDao.save(user);
        Card card = defaultCard(user,"3");
        cardDao.save(card);
        Payment payment = defaultPayment(card);
        paymentDao.save(payment);
        assertEquals(payment, paymentDao.find(payment.getId()));
        payment.setAmountSpent(111);
        paymentDao.update(payment);
        Payment updatedPayment = paymentDao.find(payment.getId());
        assertEquals(payment.getAmountSpent(), updatedPayment.getAmountSpent());
        paymentDao.remove(payment);
        assertNull(paymentDao.find(payment.getId()));
    }

    @Test
    void findAllByCard() {
        User user = defaultUser("1234", 1234);
        userDao.save(user);
        Card card = defaultCard(user,"1234");
        cardDao.save(card);
        Payment payment1 = defaultPaymentWithAmount(card, 100);
        paymentDao.save(payment1);
        Payment payment2 = defaultPaymentWithAmount(card, 200);
        paymentDao.save(payment2);
        Payment payment3 = defaultPaymentWithAmount(card, 300);
        paymentDao.save(payment3);
        List<Payment > payments = List.of(payment1, payment2, payment3);
        List<Payment> allByCard = paymentDao.findAllByCard(card);
        assertEquals(payments, allByCard);
    }

    private User defaultUser(String passportNumber, long taxNumber) {
        User user = new User();
        user.setFirstName("a");
        user.setLastName("a");
        user.setBirthday(LocalDate.of(2010, 01, 27));
        user.setPassportNumber(passportNumber);
        user.setTaxNumber(taxNumber);
        return user;
    }

    private Card defaultCard(User user, String cardNumber) {
        Card card = new Card();
        card.setBalance(1111);
        card.setCvv2(111);
        card.setExpirationDate(LocalDate.of(2020, 1, 10));
        card.setUser(user);
        card.setNumber(cardNumber);
        return card;
    }

    private Payment defaultPayment(Card card) {
        Payment payment = new Payment();
        payment.setAmountSpent(100);
        payment.setCard(card);
        payment.setTransactionTime(Timestamp.valueOf(LocalDateTime.now()));
        return payment;
    }

    private Payment defaultPaymentWithAmount(Card card, long amount) {
        Payment payment = new Payment();
        payment.setAmountSpent(amount);
        payment.setCard(card);
        payment.setTransactionTime(Timestamp.valueOf(LocalDateTime.now()));
        return payment;
    }
}
