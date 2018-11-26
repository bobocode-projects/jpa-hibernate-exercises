import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.bobocode.dao.CardDao;
import com.bobocode.entity.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CardDaoTest extends AbstractCrudDaoTest<Card> {
    private EntityManagerFactory entityManagerFactory;
    private CardDao cardDao;
    private InstanceUtil instanceUtil;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("OnlineBankingTest");
        cardDao = new CardDao(entityManagerFactory);
        instanceUtil = new InstanceUtil(entityManagerFactory);
        setAbstractCrudDao(cardDao);
    }

    @Override
    @Test
    public void updateWhenEntityExistsUpdatesItInDatabase() {
        Card card = entityInstance();
        cardDao.save(card);
        assertEquals(card, cardDao.find(card.getId()));
    }

    @Override
    public Card entityInstance() {
        return instanceUtil.cardInstance();
    }
}
