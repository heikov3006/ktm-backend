package at.htl.leonding;

import at.htl.leonding.model.User;
import at.htl.leonding.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserTest {

    @Inject
    UserRepository userRepository;

    @Test
    @Transactional
    void testCreateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.persist();

        Assertions.assertNotNull(user.id, "User ID should be generated after persisting.");
    }

    @Test
    @Transactional
    void testFindUserByEmail() {
        // Setup: create and persist a user
        User user = new User();
        user.setEmail("search@example.com");
        user.setPassword("password456");
        user.setFirstname("Alice");
        user.setLastname("Smith");
        user.persist();

        // Find user by email
        User foundUser = User.find("email", "search@example.com").firstResult();
        Assertions.assertNotNull(foundUser, "User should be found by email.");
        Assertions.assertEquals("Alice", foundUser.getFirstname(), "Firstname should match.");
        Assertions.assertEquals("Smith", foundUser.getLastname(), "Lastname should match.");
    }

    @Test
    @Transactional
    void testUpdateUser() {
        // Setup: create and persist a user
        User user = new User();
        user.setEmail("update@example.com");
        user.setPassword("password123");
        user.setFirstname("Initial");
        user.setLastname("Name");
        user.persist();

        // Update the user's details
        user.setFirstname("Updated");
        user.setLastname("Surname");
        user.persist();

        User updatedUser = User.find("email", "update@example.com").firstResult();
        Assertions.assertNotNull(updatedUser, "Updated user should still exist.");
        Assertions.assertEquals("Updated", updatedUser.getFirstname(), "Firstname should be updated.");
        Assertions.assertEquals("Surname", updatedUser.getLastname(), "Lastname should be updated.");
    }

    @Test
    @Transactional
    void testDeleteUser() {
        // Setup: create and persist a user
        User user = new User();
        user.setEmail("delete@example.com");
        user.setPassword("password123");
        user.setFirstname("To Be");
        user.setLastname("Deleted");
        user.persist();

        // Delete the user
        user.delete();

        User deletedUser = User.find("email", "delete@example.com").firstResult();
        Assertions.assertNull(deletedUser, "User should be deleted and no longer found.");
    }
}
