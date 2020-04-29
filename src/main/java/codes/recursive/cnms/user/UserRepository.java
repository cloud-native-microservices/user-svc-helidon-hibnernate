package codes.recursive.cnms.user;

import codes.recursive.cnms.user.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class UserRepository {
    @PersistenceContext
    private static EntityManager entityManager;

    @Inject
    public UserRepository(UserProvider userProvider) {
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        configOverrides.put("hibernate.connection.url", userProvider.getDbUrl());
        configOverrides.put("hibernate.connection.username", userProvider.getDbUser());
        configOverrides.put("hibernate.connection.password", userProvider.getDbPassword());
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("UserPU", configOverrides);
        entityManager = emf.createEntityManager();
    }

    public Set<ConstraintViolation<User>> validate(User user) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        return constraintViolations;
    }

    public User save(User user) {
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        return user;
    }

    public User get(String id) {
        User user = entityManager.find(User.class, id);
        return user;
    }

    public List<User> findAll() {
        return entityManager.createQuery("from User").getResultList();
    }

    public List<User> findAll(int offset, int max) {
        Query query = entityManager.createQuery("from User");
        query.setFirstResult(offset);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public long count() {
        Query queryTotal = entityManager.createQuery("Select count(u.id) from User u");
        long countResult = (long)queryTotal.getSingleResult();
        return countResult;
    }

    public void deleteById(String id) {
        // Retrieve the movie with this ID
        User user = get(id);
        if (user != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(user);
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
