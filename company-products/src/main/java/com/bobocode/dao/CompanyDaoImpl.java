package com.bobocode.dao;

import com.bobocode.model.Company;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

public class CompanyDaoImpl implements CompanyDao {
    private EntityManagerFactory entityManagerFactory;

    public CompanyDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Company findByIdFetchProducts(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityGraph<Company> entityGraph = entityManager.createEntityGraph(Company.class);
        entityGraph.addAttributeNodes("products");
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.fetchgraph", entityGraph);
        return entityManager.find(Company.class, id, properties);
    }
}
