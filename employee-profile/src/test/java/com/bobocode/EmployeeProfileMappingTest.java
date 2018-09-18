package com.bobocode;

import com.bobocode.model.Employee;
import com.bobocode.model.EmployeeProfile;
import com.bobocode.util.EntityManagerUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EmployeeProfileMappingTest {
    private static EntityManagerUtil emUtil;
    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("Employees");
        emUtil = new EntityManagerUtil(entityManagerFactory);
    }

    @AfterAll
    static void destroy() {
        entityManagerFactory.close();
    }

    @Test
    public void testSaveEmployeeOnly() {
        Employee employee = createRandomEmployee();

        emUtil.performWithinTx(entityManager -> entityManager.persist(employee));

        assertThat(employee.getId(), notNullValue());
    }

    private Employee createRandomEmployee() {
        Employee employee = new Employee();
        employee.setEmail(RandomStringUtils.randomAlphabetic(15));
        employee.setFistName(RandomStringUtils.randomAlphabetic(15));
        employee.setLastName(RandomStringUtils.randomAlphabetic(15));
        return employee;
    }

    @Test
    public void testSaveEmployeeWithoutEmail() {
        Employee employee = createRandomEmployee();
        employee.setEmail(null);
        try {
            emUtil.performWithinTx(entityManager -> entityManager.persist(employee));
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertTrue(e instanceof PersistenceException);
        }
    }

    @Test
    public void testSaveEmployeeFirstName() {
        Employee employee = createRandomEmployee();
        employee.setFistName(null);
        try {
            emUtil.performWithinTx(entityManager -> entityManager.persist(employee));
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertTrue(e instanceof PersistenceException);
        }
    }

    @Test
    public void testSaveEmployeeLastName() {
        Employee employee = createRandomEmployee();
        employee.setLastName(null);
        try {
            emUtil.performWithinTx(entityManager -> entityManager.persist(employee));
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertTrue(e instanceof PersistenceException);
        }
    }

    @Test
    public void testSaveEmployeeProfileOnly() {
        EmployeeProfile employeeProfile = createRandomEmployeeProfile();
        employeeProfile.setId(666L);

        try {
            emUtil.performWithinTx(entityManager -> entityManager.persist(employeeProfile));
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertTrue(e instanceof PersistenceException);
        }
    }

    private EmployeeProfile createRandomEmployeeProfile() {
        EmployeeProfile employeeProfile = new EmployeeProfile();
        employeeProfile.setDepartment(RandomStringUtils.randomAlphabetic(15));
        employeeProfile.setPosition(RandomStringUtils.randomAlphabetic(15));
        return employeeProfile;
    }

    @Test
    public void testSaveBothEmployeeAndEmployeeProfile() {
        Employee employee = createRandomEmployee();
        EmployeeProfile employeeProfile = createRandomEmployeeProfile();

        emUtil.performWithinTx(entityManager -> {
            entityManager.persist(employee);
            employeeProfile.setEmployee(employee);
            entityManager.persist(employeeProfile);
        });

        assertThat(employee.getId(), notNullValue());
        assertThat(employeeProfile.getId(), notNullValue());
        assertThat(employeeProfile.getId(), equalTo(employee.getId()));
    }

    @Test
    public void testAddEmployeeProfile() {
        Employee employee = createRandomEmployee();
        emUtil.performWithinTx(entityManager -> entityManager.persist(employee));
        long employeeId = employee.getId();

        EmployeeProfile employeeProfile = createRandomEmployeeProfile();
        emUtil.performWithinTx(entityManager -> {
            Employee managedEmployee = entityManager.find(Employee.class, employeeId);
            employeeProfile.setEmployee(managedEmployee);
            entityManager.persist(employeeProfile);
        });

        assertThat(employee.getId(), notNullValue());
        assertThat(employeeProfile.getId(), notNullValue());
        assertThat(employeeProfile.getId(), equalTo(employee.getId()));
    }

    @Test
    public void testAddEmployeeWithoutPosition() {
        Employee employee = createRandomEmployee();
        emUtil.performWithinTx(entityManager -> entityManager.persist(employee));
        long employeeId = employee.getId();

        EmployeeProfile profileWithoutPosition = createRandomEmployeeProfile();
        profileWithoutPosition.setPosition(null);
        try {
            emUtil.performWithinTx(entityManager -> {
                Employee managedEmployee = entityManager.find(Employee.class, employeeId);
                profileWithoutPosition.setEmployee(managedEmployee);
                entityManager.persist(profileWithoutPosition);
            });
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertTrue(e instanceof PersistenceException);
        }
    }

    @Test
    public void testAddEmployeeWithoutDepartment() {
        Employee employee = createRandomEmployee();
        emUtil.performWithinTx(entityManager -> entityManager.persist(employee));
        long employeeId = employee.getId();

        EmployeeProfile profileWithoutDepartment = createRandomEmployeeProfile();
        profileWithoutDepartment.setDepartment(null);
        try {
            emUtil.performWithinTx(entityManager -> {
                Employee managedEmployee = entityManager.find(Employee.class, employeeId);
                profileWithoutDepartment.setEmployee(managedEmployee);
                entityManager.persist(profileWithoutDepartment);
            });
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertTrue(e instanceof PersistenceException);
        }
    }

    @Test
    public void testForeignKeyColumnHasCorrectName() throws NoSuchFieldException {
        Field employee = EmployeeProfile.class.getDeclaredField("employee");
        JoinColumn joinColumn = employee.getAnnotation(JoinColumn.class);
        String foreignKeyColumnName = joinColumn.name();

        assertThat(foreignKeyColumnName, equalTo("employee_id"));
    }

    @Test
    public void testEmployeeTableHasCorrectName() {
        Table table = Employee.class.getAnnotation(Table.class);
        String tableName = table.name();

        assertThat(tableName, equalTo("employee"));
    }

    @Test
    public void testEmployeeProfileTableHasCorrectName() {
        Table table = EmployeeProfile.class.getAnnotation(Table.class);
        String tableName = table.name();

        assertThat(tableName, equalTo("employee_profile"));
    }
}
