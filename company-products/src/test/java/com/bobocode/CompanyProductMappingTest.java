package com.bobocode;

import com.bobocode.dao.CompanyDao;
import com.bobocode.dao.CompanyDaoImpl;
import com.bobocode.model.Company;
import com.bobocode.model.Product;
import com.bobocode.util.EntityManagerUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class CompanyProductMappingTest {
    private static EntityManagerUtil emUtil;
    private static EntityManagerFactory entityManagerFactory;
    private static CompanyDao companyDao;

    @BeforeAll
    static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("CompanyProducts");
        emUtil = new EntityManagerUtil(entityManagerFactory);
        companyDao = new CompanyDaoImpl(entityManagerFactory);
    }

    @AfterAll
    static void destroy() {
        entityManagerFactory.close();
    }

    @Test
    public void testSaveCompany() {
        var company = createRandomCompany();
        emUtil.performWithinTx(entityManager -> entityManager.persist(company));

        assertThat(company.getId(), notNullValue());
    }

    private Company createRandomCompany() {
        var company = new Company();
        company.setName(RandomStringUtils.randomAlphabetic(20));
        return company;
    }

    @Test
    public void testSaveProduct() {
        var product = createRandomProduct();

        emUtil.performWithinTx(entityManager -> entityManager.persist(product));
    }

    private Product createRandomProduct() {
        var product = new Product();
        product.setName(RandomStringUtils.randomAlphabetic(20));
        return product;
    }

    @Test
    public void testSaveCompanyWithNullName() {
        var company = new Company();

        try {
            emUtil.performWithinTx(entityManager -> entityManager.persist(company));
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(PersistenceException.class));
        }
    }

    @Test
    public void testSaveProductWithNullName() {
        var product = new Product();

        try {
            emUtil.performWithinTx(entityManager -> entityManager.persist(product));
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(PersistenceException.class));
        }
    }

    @Test
    public void testForeignKeyColumnIsSpecified() throws NoSuchFieldException {
        Field company = Product.class.getDeclaredField("company");
        JoinColumn joinColumn = company.getAnnotation(JoinColumn.class);

        assertThat(joinColumn.name(), equalTo("company_id"));
    }

    @Test
    public void testSaveProductAndCompany() {
        var company = createRandomCompany();
        var product = createRandomProduct();

        emUtil.performWithinTx(entityManager -> entityManager.persist(company));
        emUtil.performWithinTx(entityManager -> {
            var companyProxy = entityManager.getReference(Company.class, company.getId());
            product.setCompany(companyProxy);
            entityManager.persist(product);
        });

        emUtil.performWithinTx(entityManager -> {
            var managedCompany = entityManager.find(Company.class, company.getId());
            var managedProduct = entityManager.find(Product.class, product.getId());
            assertThat(managedCompany.getProducts(), hasItem(managedProduct));
            assertThat(managedProduct.getCompany(), equalTo(managedCompany));
        });
    }

    @Test
    public void testAddNewProductToExistingCompany() {
        var company = createRandomCompany();
        emUtil.performWithinTx(entityManager -> entityManager.persist(company));

        var product = createRandomProduct();
        emUtil.performWithinTx(entityManager -> {
            entityManager.persist(product);
            var managedCompany = entityManager.merge(company);
            managedCompany.addProduct(product);
            assertThat(managedCompany.getProducts(),hasItem(product));
        });

        assertThat(product.getCompany(), equalTo(company));
        emUtil.performWithinTx(entityManager -> {
            var managedCompany = entityManager.find(Company.class, company.getId());
            assertThat(managedCompany.getProducts(), hasItem(product));

        });
    }

    @Test
    public void testRemoveProductFromCompany() {
        var company = createRandomCompany();
        emUtil.performWithinTx(entityManager -> entityManager.persist(company));

        var product = createRandomProduct();
        emUtil.performWithinTx(entityManager -> {
            product.setCompany(company);
            entityManager.persist(product);
        });

        emUtil.performWithinTx(entityManager -> {
            var managedProduct = entityManager.find(Product.class, product.getId());
            var managedCompany = entityManager.find(Company.class, company.getId());
            managedCompany.removeProduct(managedProduct);
            assertThat(managedCompany.getProducts(), not(hasItem(managedProduct)));
        });

        emUtil.performWithinTx(entityManager -> {
            var managedCompany = entityManager.find(Company.class, company.getId());
            assertThat(managedCompany.getProducts(), not(hasItem(product)));
        });
    }

    @Test
    public void testCompanyToProductsIsLazy() {
        var company = createRandomCompany();
        emUtil.performWithinTx(entityManager -> entityManager.persist(company));

        var product = createRandomProduct();
        emUtil.performWithinTx(entityManager -> {
            product.setCompany(company);
            entityManager.persist(product);
        });

        Company loadedCompany = emUtil.performReturningWithinTx(entityManager -> entityManager.find(Company.class, company.getId()));
        try {
            List<Product> products = loadedCompany.getProducts();
            System.out.println(products);
            fail("should throw exception");
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(LazyInitializationException.class));
        }
    }

    @Test
    public void testProductsToCompanyIsLazy() {
        var company = createRandomCompany();
        emUtil.performWithinTx(entityManager -> entityManager.persist(company));

        var product = createRandomProduct();
        emUtil.performWithinTx(entityManager -> {
            product.setCompany(company);
            entityManager.persist(product);
        });

        Product loadedProduct = emUtil.performReturningWithinTx(entityManager -> entityManager.find(Product.class, product.getId()));
        try {
            Company loadedCompany = loadedProduct.getCompany();
            System.out.println(loadedCompany);
            fail("should throw exception");
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(LazyInitializationException.class));
        }
    }

    @Test
    public void testFindByIdFetchesProducts() {
        var company = createRandomCompany();
        emUtil.performWithinTx(entityManager -> entityManager.persist(company));

        var product = createRandomProduct();
        emUtil.performWithinTx(entityManager -> {
            product.setCompany(company);
            entityManager.persist(product);
        });

        Company foundCompany = companyDao.findByIdFetchProducts(company.getId());
        assertThat(foundCompany, equalTo(company));
        assertThat(foundCompany.getProducts(), hasItem(product));
    }

    @Test
    public void testCompanySetProductsIsPrivate() throws NoSuchMethodException {
        assertThat(Company.class.getDeclaredMethod("setProducts", List.class).getModifiers(), equalTo(Modifier.PRIVATE));
    }
}
