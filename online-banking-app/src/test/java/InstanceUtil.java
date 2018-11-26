import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.bobocode.entity.Card;
import com.bobocode.entity.Payment;
import com.bobocode.entity.User;
import com.bobocode.util.EntityManagerUtil;

public class InstanceUtil {
    EntityManagerFactory entityManagerFactory;
    EntityManagerUtil entityManagerUtil;

    public InstanceUtil(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        entityManagerUtil = new EntityManagerUtil(entityManagerFactory);
    }

    public User userInstance() {
        long randomData = System.nanoTime();
        return User.builder()
                .firstName("Name")
                .lastName("Surname")
                .birthday(LocalDate.now())
                .passportNumber(String.valueOf(randomData))
                .taxNumber(randomData)
                .build();
    }

    public Card cardInstance() {
        long randomData = System.nanoTime();
        User user = User.builder()
                .firstName("Name")
                .lastName("Surname")
                .birthday(LocalDate.now())
                .passportNumber(String.valueOf(randomData))
                .taxNumber(randomData)
                .build();
        saveInstance(user);

        return Card.builder()
                .balance(randomData)
                .cvv2(111)
                .user(user)
                .expirationDate(LocalDate.now())
                .number(String.valueOf(randomData))
                .build();
    }


    public Payment paymentInstance() {
        long randomData = System.nanoTime();
        User user = User.builder()
                .firstName("Name")
                .lastName("Surname")
                .birthday(LocalDate.now())
                .passportNumber(String.valueOf(randomData))
                .taxNumber(randomData)
                .build();
        saveInstance(user);

        Card card = Card.builder()
                .balance(randomData)
                .cvv2(111)
                .user(user)
                .expirationDate(LocalDate.now())
                .number(String.valueOf(randomData))
                .build();
        saveInstance(card);

        return Payment.builder()
                .card(card)
                .amountSpent(111L)
                .transactionTime(LocalDateTime.now())
                .build();
    }

    private void saveInstance(Object user) {
        entityManagerUtil.executeEntityManagerRequest(entityManager -> entityManager.persist(user));
    }
}
