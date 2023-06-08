package com.webshopproject.admin.user;

import com.webshopproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CustomPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CustomPagingAndSortingRepository<User, Integer> {
    @Query("select user from User user where user.email = :email")
    public User getUserByEmail(@Param("email") String email);

    @Query("select user from User user where user.firstName like %?1% or user.lastName like %?1%")
    public Page<User> findAll(String keyword, Pageable pageable);

    public Long countById(Integer id);

    @Query("update User user set user.enabled = ?2 where user.id = ?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);
}
