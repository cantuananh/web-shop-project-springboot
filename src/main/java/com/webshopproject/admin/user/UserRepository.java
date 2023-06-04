package com.webshopproject.admin.user;

import com.webshopproject.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CustomPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CustomPagingAndSortingRepository<User, Integer> {
    @Query("select user from User user where user.email = :email")
    public User getUserByEmail(@Param("email") String email);

    public Long countById(Integer id);

    @Query("update User user set user.enabled = ?2 where user.id = ?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);
}
