package com.bobocode.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.bobocode.model.Photo;
import com.bobocode.model.PhotoComment;
import org.hibernate.Session;

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
        executeEntityManagerWithinTransaction(entityManager -> entityManager.persist(photo));
    }

    @Override
    public Photo findById(long id) {
        return resultFromEntityManagerWithinTransaction(entityManager -> entityManager.find(Photo.class, id));
    }

    @Override
    public List<Photo> findAll() {
        return resultFromEntityManagerWithinTransaction(entityManager ->
                entityManager.createQuery("from Photo", Photo.class).getResultList()
        );
    }

    @Override
    public void remove(Photo photo) {
        executeEntityManagerWithinTransaction(entityManager -> {
            Photo merge = entityManager.merge(photo);
            entityManager.remove(merge);
        });
    }

    @Override
    public void addComment(long photoId, String comment) {
        executeEntityManagerWithinTransaction(entityManager -> {
            Photo photo = entityManager.getReference(Photo.class, photoId);
            PhotoComment photoComment = new PhotoComment();
            photoComment.setText(comment);
            photo.addComment(photoComment);
        });
    }

    private void executeEntityManagerWithinTransaction(Consumer<EntityManager> emConsumer) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            emConsumer.accept(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    private <T> T resultFromEntityManagerWithinTransaction(Function<EntityManager, T> emFuntion) {
        Session session = (Session) entityManagerFactory.createEntityManager();
        try {
            T result = emFuntion.apply(session);
//            session.setReadOnly(result, true);
            return result;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}

// при session.setReadOnly(result, true) падає findAll (TransientObjectException: Instance was not associated with this persistence context)