package com.bobocode.dao;

import com.bobocode.model.Company;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class CompanyDaoImpl implements CompanyDao {
    private EntityManagerFactory entityManagerFactory;

    public CompanyDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Company findByIdFetchProducts(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Company company = entityManager
                .createQuery("SELECT c FROM Company c LEFT JOIN FETCH c.products WHERE c.id = :id", Company.class)
                .setParameter("id", id)
                .getSingleResult();
        entityManager.close();
        return company;
    }
}
