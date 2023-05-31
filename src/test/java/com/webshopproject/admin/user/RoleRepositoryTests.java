package com.webshopproject.admin.user;

import com.webshopproject.entity.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository repository;

    @Test
    public void testCreateFirstRole() {
        Role adminRole = new Role("Admin", "Manager everything");
        Role saveRole = repository.save(adminRole);
        assertThat(saveRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRestRoles() {
        Role roleSalesperson = new Role("Salesperson", "Manager product price, " + "customers, " + "shipping, " + "order and sale report");
        Role roleEditor = new Role("RoleEditor", "Editor, " + "manager categories, brands, product, articles and menus");
        Role roleShipper = new Role("Shipper", "Editor, " + "View product, view order and update order status");
        Role roleAssistant = new Role("Assistant", "Manager questions and reviews");

        repository.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));

    }
}
