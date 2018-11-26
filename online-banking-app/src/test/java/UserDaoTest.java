import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.bobocode.dao.UserDao;
import com.bobocode.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDaoTest extends AbstractCrudDaoTest<User> {
    private EntityManagerFactory entityManagerFactory;
    private UserDao userDao;
    InstanceUtil instanceUtil;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("OnlineBankingTest");
        userDao = new UserDao(entityManagerFactory);
        instanceUtil = new InstanceUtil(entityManagerFactory);
        setAbstractCrudDao(userDao);
    }

    @Override
    @Test
    public void updateWhenEntityExistsUpdatesItInDatabase() {
        User user = entityInstance();
        userDao.save(user);
        assertEquals(user, userDao.find(user.getId()));
        userDao.remove(user);
        assertNull(userDao.find(user.getId()));
    }

    @Override
    public User entityInstance() {
        return instanceUtil.userInstance();
    }
}
