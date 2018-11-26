package com.bobocode.dao;

import javax.persistence.EntityManagerFactory;
import java.util.List;

import com.bobocode.util.EntityManagerUtil;

public abstract class AbstractCrudDao<T> implements Crud<T> {
    protected Class<T> entityClass;
    private final EntityManagerFactory emf;
    protected final EntityManagerUtil emExecutor;

    protected AbstractCrudDao(EntityManagerFactory emf) {
        this.emf = emf;
        emExecutor = new EntityManagerUtil(emf);
    }


    @Override
    public void save(T element) {
        emExecutor.executeEntityManagerRequest(entityManager -> entityManager.persist(element));
    }

    @Override
    public T find(Long id) {
        return emExecutor.resultListFromEntityManagerRequest(entityManager -> entityManager.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        String query = "from " + entityClass.getSimpleName();
        return emExecutor.resultListFromEntityManagerRequest(entityManager ->
                entityManager.createQuery(query, entityClass).getResultList());
    }

    @Override
    public void update(T element) {
        emExecutor.executeEntityManagerRequest(entityManager -> entityManager.merge(element));
    }

    @Override
    public void remove(T element) {
        emExecutor.executeEntityManagerRequest(entityManager -> {
            T mergedElement = entityManager.merge(element);
            entityManager.remove(mergedElement);
        });
    }
}
