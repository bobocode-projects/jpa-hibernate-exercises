package com.bobocode.dao;

import com.bobocode.model.Account;
import com.bobocode.model.Photo;
import com.bobocode.model.PhotoComment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Please note that you should not use auto-commit mode for your implementation.
 */
public class PhotoDaoImpl implements PhotoDao {
    private EntityManagerFactory entityManagerFactory;

    public PhotoDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Photo photo) {
        performWithinTransaction(em -> em.persist(photo));
    }

    @Override
    public Photo findById(long id) {
        return returnWithinTransaction(em -> em.find(Photo.class, id));
    }

    @Override
    public List<Photo> findAll() {
        return returnWithinTransaction(em -> em.createQuery("SELECT p FROM Photo p", Photo.class).getResultList());
    }

    @Override
    public void remove(Photo photo) {
        performWithinTransaction(em -> {
            Photo existedPhoto = em.find(Photo.class, photo.getId());
            if (existedPhoto != null) {
                em.remove(existedPhoto);
            }
        });
    }

    @Override
    public void addComment(long photoId, String comment) {
        performWithinTransaction(em -> {
            Photo existedPhoto = em.find(Photo.class, photoId);
            if (existedPhoto != null) {
                existedPhoto.addComment(new PhotoComment(comment));
            }
        });
    }

    private <T> T returnWithinTransaction(Function<EntityManager, T> function) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        entityManager.setFlushMode(FlushModeType.COMMIT);
        entityManager.getTransaction().begin();
        try {
            T result = function.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        } catch (RuntimeException e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    private void performWithinTransaction(Consumer<EntityManager> consumer) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        entityManager.setFlushMode(FlushModeType.COMMIT);
        entityManager.getTransaction().begin();
        try {
            consumer.accept(entityManager);
            entityManager.getTransaction().commit();
        } catch (RuntimeException e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
