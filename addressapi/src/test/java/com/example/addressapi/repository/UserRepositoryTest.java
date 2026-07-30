package com.example.addressapi.repository;

import com.example.addressapi.AddressapiApplication;
import com.example.addressapi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = AddressapiApplication.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb2;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_whenUserExists_returnsUser() {
        userRepository.save(new User(null, "alice", "hashedPassword123"));

        Optional<User> result = userRepository.findByUsername("alice");

        assertThat(result).isPresent();
        assertThat(result.get().getPassword()).isEqualTo("hashedPassword123");
    }

    @Test
    void findByUsername_whenUserDoesNotExist_returnsEmpty() {
        Optional<User> result = userRepository.findByUsername("ghost");
        assertThat(result).isEmpty();
    }

    @Test
    void save_enforcesUniqueUsernameConstraint() {
        userRepository.save(new User(null, "bob", "pw1"));

        assertThrows(Exception.class, () ->
                userRepository.saveAndFlush(new User(null, "bob", "pw2")));
    }
}