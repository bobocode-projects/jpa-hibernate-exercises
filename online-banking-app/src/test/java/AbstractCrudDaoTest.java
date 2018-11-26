import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import com.bobocode.dao.AbstractCrudDao;
import com.bobocode.entity.IdNumber;
import org.junit.jupiter.api.Test;

public abstract class AbstractCrudDaoTest<E extends IdNumber> {
    protected AbstractCrudDao<E> abstractCrudDao;

    @Test
    void saveWhenTransientEntitySavesEntityToDatabase() {
        E entityInstance = entityInstance();
        abstractCrudDao.save(entityInstance);
    }

    @Test
    void findWhenEntityExistsReturnsEntityFromDatabase() {
        E entityInstance = entityInstance();
        abstractCrudDao.save(entityInstance);
        E entity = abstractCrudDao.find(entityInstance.getId());
        assertEquals(entityInstance, entity);
    }

    @Test
    void findAllWhenRowsInDatabaseExistReturnsListOfAllElements() {
        E instance1 = entityInstance();
        abstractCrudDao.save(instance1);
        E instance2 = entityInstance();
        abstractCrudDao.save(instance2);
        E instance3 = entityInstance();
        abstractCrudDao.save(instance3);
        List expectedList = List.of(instance1, instance2, instance3);
        List<E> allInstances = abstractCrudDao.findAll();
        assertEquals(expectedList, allInstances);
    }

    @Test
    void removeWhenEntityExistsRemovesItFromDatabase() {
        E instance = entityInstance();
        abstractCrudDao.save(instance);
        assertEquals(instance, abstractCrudDao.find(instance.getId()));
        abstractCrudDao.remove(instance);
        assertNull(abstractCrudDao.find(instance.getId()));
    }


    public abstract void updateWhenEntityExistsUpdatesItInDatabase();

    protected abstract E entityInstance();

    public void setAbstractCrudDao(AbstractCrudDao<E> abstractCrudDao) {
        this.abstractCrudDao = abstractCrudDao;
    }
}
