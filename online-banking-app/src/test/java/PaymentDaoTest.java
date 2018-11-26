import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.bobocode.dao.PaymentDao;
import com.bobocode.entity.Card;
import com.bobocode.entity.Payment;
import com.bobocode.entity.User;
import com.bobocode.util.EntityManagerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaymentDaoTest extends AbstractCrudDaoTest<Payment> {
    private EntityManagerFactory entityManagerFactory;
    private PaymentDao paymentDao;
    private InstanceUtil instanceUtil;
    private EntityManagerUtil entityManagerUtil;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("OnlineBankingTest");
        paymentDao = new PaymentDao(entityManagerFactory);
        entityManagerUtil = new EntityManagerUtil(entityManagerFactory);
        instanceUtil = new InstanceUtil(entityManagerFactory);
        setAbstractCrudDao(paymentDao);
    }

    @Override
    @Test
    public void updateWhenEntityExistsUpdatesItInDatabase() {
        Payment payment = entityInstance();
        paymentDao.save(payment);
        assertEquals(payment, paymentDao.find(payment.getId()));
        payment.setAmountSpent(2222L);
        assertEquals(payment, paymentDao.find(payment.getId()));
    }

    @Test
    void findAllByCardReturnsListOfPaymentsByCard() {
        Card card = instanceUtil.cardInstance();
        entityManagerUtil.executeEntityManagerRequest(entityManager ->
                entityManager.persist(card));

        Payment payment1 = transientPayment();
        payment1.setCard(card);
        paymentDao.save(payment1);

        Payment payment2 = transientPayment();
        payment2.setCard(card);
        paymentDao.save(payment2);

        Payment payment3 = transientPayment();
        payment3.setCard(card);
        paymentDao.save(payment3);

        List<Payment> expectedList = List.of(payment1, payment2, payment3);
        assertEquals(expectedList, paymentDao.findAllByCard(card));
    }

    @Test
    void findAllByUserReturnsListOfPaymentsByUser() {
        Card card = instanceUtil.cardInstance();
        entityManagerUtil.executeEntityManagerRequest(entityManager ->
                entityManager.persist(card));
        User user = card.getUser();

        Payment payment1 = transientPayment();
        payment1.setCard(card);
        paymentDao.save(payment1);

        Payment payment2 = transientPayment();
        payment2.setCard(card);
        paymentDao.save(payment2);

        Payment payment3 = transientPayment();
        payment3.setCard(card);
        paymentDao.save(payment3);

        List<Payment> expectedList = List.of(payment1, payment2, payment3);
        assertEquals(expectedList, paymentDao.findAllByUser(user.getId()));
    }

    @Test
    void findAllByUserAndDateReturnsListOfPaymentsByUserAndDate() {
        Card card = instanceUtil.cardInstance();
        entityManagerUtil.executeEntityManagerRequest(entityManager ->
                entityManager.persist(card));
        User user = card.getUser();

        Payment payment1 = transientPayment();
        payment1.setCard(card);
        paymentDao.save(payment1);

        Payment payment2 = transientPayment();
        payment2.setCard(card);
        paymentDao.save(payment2);

        Payment payment3 = transientPayment();
        payment3.setCard(card);
        paymentDao.save(payment3);

        Payment paymentOutOfDate = transientPayment();
        paymentOutOfDate.setTransactionTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        paymentOutOfDate.setCard(card);
        paymentDao.save(paymentOutOfDate);

        List<Payment> allPayments = paymentDao.findAll();
        assertEquals(4, allPayments.size());

        List<Payment> expectedList = List.of(payment1, payment2, payment3);
        assertEquals(expectedList, paymentDao.findAllByUserAndDate(user.getId(), LocalDate.now()));
    }

    @Test
    void findAllPaymentMoreThanReturnsListOfPaymentsByUserAndAmount() {
        Card card = instanceUtil.cardInstance();
        entityManagerUtil.executeEntityManagerRequest(entityManager ->
                entityManager.persist(card));
        User user = card.getUser();

        Payment payment1 = transientPayment();
        payment1.setCard(card);
        paymentDao.save(payment1);

        Payment payment2 = transientPayment();
        payment2.setCard(card);
        paymentDao.save(payment2);

        Payment payment3 = transientPayment();
        payment3.setCard(card);
        paymentDao.save(payment3);

        Payment paymentOutOfDate = transientPayment();
        paymentOutOfDate.setAmountSpent(90L);
        paymentOutOfDate.setCard(card);
        paymentDao.save(paymentOutOfDate);

        List<Payment> allPayments = paymentDao.findAll();
        assertEquals(4, allPayments.size());

        List<Payment> expectedList = List.of(payment1, payment2, payment3);
        assertEquals(expectedList, paymentDao.findAllPaymentMoreThan(user.getId(), 100L));

    }

    @Override
    public Payment entityInstance() {
        return instanceUtil.paymentInstance();
    }

    private Payment transientPayment() {
        return Payment.builder()
                .amountSpent(123L)
                .transactionTime(LocalDateTime.now())
                .build();
    }
}
