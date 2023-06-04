package com.webshopproject.admin.user;

import com.webshopproject.entity.Role;
import com.webshopproject.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    public static final int USER_PER_PAGE = 5;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    public List<User> listAll() {
        return (List<User>) userRepository.findAll();
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);

        if (isUpdatingUser) {
            User existingUser = userRepository.findById(user.getId()).get();

            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }
        }
        return userRepository.save(user);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepository.getUserByEmail(email);

        if (id == null) {
            if (userByEmail != null) {
                return false;
            }
            return true;
        }

        if (userByEmail != null && userByEmail.getId() == id) {
            return true;
        }

        return false;
    }

    public User getUserWith(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("Can not found user with id: " + id);
        }
    }

    public void deleteUserWith(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);

        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not found any user with ID: " + id);
        }

        userRepository.deleteById(id);
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepository.updateEnabledStatus(id, enabled);
    }

    public Page<User> listByPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, USER_PER_PAGE);

        return userRepository.findAll(pageable);
    }
}
