package com.webshopproject.admin.user;

import com.webshopproject.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    @Query("select user from User user where user.email = :email")
    public User getUserByEmail(@Param("email") String email);

    public Long countById(Integer id);
}
