package com.bobocode.dao;

import com.bobocode.exceptions.PhotoCommentException;
import com.bobocode.model.Photo;
import com.bobocode.model.PhotoComment;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Please note that you should not use auto-commit mode for your implementation.
 */
public class PhotoDaoImpl implements PhotoDao {
    private static EntityManagerFactory entityManagerFactory;

    public PhotoDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Photo photo) {
        performOperationWithoutReturnData(entityManager -> entityManager.persist(photo),
                false);
    }

    @Override
    public Photo findById(long id) {
        return performOperationWithReturnData(entityManager ->
                        entityManager.find(Photo.class, id),
                true);
    }

    @Override
    public List<Photo> findAll() {
        return performOperationWithReturnData(entityManager ->
                entityManager.createQuery("select ph from Photo ph", Photo.class)
                .getResultList(),
                true);
    }

    @Override
    public void remove(Photo photo) {
        performOperationWithoutReturnData(entityManager -> {
            Photo removePhoto = entityManager.merge(photo);
            entityManager.remove(removePhoto);
                },
                true);
    }

    @Override
    public void addComment(long photoId, String comment) {
        performOperationWithoutReturnData(entityManager -> {
                PhotoComment newComment = new PhotoComment();
                newComment.setText(comment);
                entityManager.find(Photo.class, photoId)
                .addComment(newComment);},
                false);
    }

    private static void performOperationWithoutReturnData(Consumer<EntityManager> contextOperation, boolean setReadOnly) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Session hibernateSession = entityManager.unwrap(Session.class);
        hibernateSession.setDefaultReadOnly(setReadOnly);
        entityManager.getTransaction().begin();
        try {
            contextOperation.accept(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new PhotoCommentException("Transaction is rolled back");
        } finally {
            entityManager.close();
            hibernateSession.close();
        }
    }

    private static <T> T performOperationWithReturnData(Function<EntityManager, T> contextOperation, boolean setReadOnly) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Session hibernateSession = entityManager.unwrap(Session.class);
        entityManager.getTransaction().begin();
        try {
            T result = contextOperation.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new PhotoCommentException("Transaction is rolled back");
        } finally {
            entityManager.close();
            hibernateSession.close();
        }
    }
}
