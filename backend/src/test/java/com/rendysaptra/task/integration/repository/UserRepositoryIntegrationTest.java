package com.rendysaptra.task.integration.repository;

import com.rendysaptra.task.domain.entitty.User;
import com.rendysaptra.task.integration.BaseIntegrationTest;
import com.rendysaptra.task.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        userRepository.deleteAll();
    }

    @Test
    void findByEmail_existingUser_returnsUser() {
        String email = "test@example.com";
        User user = new User(email, "hashedPassword");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail(email);

        assertTrue(found.isPresent());
        assertEquals(email, found.get().getEmail());
    }

    @Test
    void findByEmail_nonExistentEmail_returnsEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertTrue(found.isEmpty());
    }

    @Test
    void save_validUser_persistsToDatabase() {
        User user = new User("user@example.com", "hashedPassword");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("user@example.com", saved.getEmail());
    }

    @Test
    void save_duplicateEmail_throwsException() {
        String email = "duplicate@example.com";
        User user1 = new User(email, "password1");
        User user2 = new User(email, "password2");

        userRepository.save(user1);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
            userRepository.flush();
        });
    }

    @Test
    void findById_existingUser_returnsUser() {
        User user = new User("test@example.com", "hashedPassword");
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void findById_nonExistentId_returnsEmpty() {
        Optional<User> found = userRepository.findById(java.util.UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void delete_existingUser_removesFromDatabase() {
        User user = new User("test@example.com", "hashedPassword");
        User saved = userRepository.save(user);

        userRepository.delete(saved);

        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }
}
