package com.webshopproject.admin.user;

import com.webshopproject.entity.Role;
import com.webshopproject.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {
    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void testCreateUser() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User userCan = new User("anh.can@codegym.vn", "12345", "Can", "Tuan Anh");
        userCan.addRole(roleAdmin);

        User saveUser = repository.save(userCan);
        assertThat(saveUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateWithTwoRole() {
        User userCan = new User("phuonglinhpham@gmail.com", "12345", "Pham", "Phuong Linh");
        Role roleEditor = entityManager.find(Role.class, 3);
        Role roleAssistant = entityManager.find(Role.class, 5);
        userCan.addRole(roleEditor);
        userCan.addRole(roleAssistant);

        User saveUser = repository.save(userCan);
        assertThat(saveUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers() {
        Iterable<User> listUsers = repository.findAll();
        listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById() {
        User user = repository.findById(3).get();
        System.out.println(user);
        assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails() {
        User user = repository.findById(3).get();
        user.setEnabled(true);
        user.setEmail("anhcan@codegym.vn");
        repository.save(user);

    }

    @Test
    public void testUpdateUserRoles() {
        User user = repository.findById(5).get();
        Role roleEditor = new Role(3);
        Role roleAdmin = new Role(1);
        Role roleShipper = new Role(4);
        user.getRoles().remove(roleEditor);
        user.addRole(roleShipper);
        user.addRole(roleAdmin);
        repository.save(user);
    }

    @Test
    public void testDeleteUser() {
        Integer userId = 5;
        repository.deleteById(userId);
    }

    @Test
    public void testGetUserByEmail() {
        String email = "anhcan@codegym.vn";
        User user = repository.getUserByEmail(email);

        assertThat(user).isNotNull();
    }

    @Test
    public void testCountById() {
        Integer id = 3;
        Long countById =  repository.countById(id);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser() {
        Integer id = 3;
        repository.updateEnabledStatus(id, false);
    }

    @Test
    public void testListFirstPage() {
        int pageNumber = 1;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repository.findAll(pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(user -> {
            System.out.println(user);
        });
        assertThat(listUsers.size()).isEqualTo(pageSize);
    }

    @Test
    public void testSearchUsers() {
        String keyword = "can";
        int pageNumber = 1;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repository.findAll(pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(user -> {
            System.out.println(user);
        });
        assertThat(listUsers.size()).isGreaterThan(0);
    }
}
