package com.alexnv.contoller;

import com.alexnv.exception.UserNotFoundException;
import com.alexnv.model.Role;
import com.alexnv.model.User;
import com.alexnv.service.RoleServiceImpl;
import com.alexnv.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserServiceImpl userServiceImpl;
    private final RoleServiceImpl roleServiceImpl;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminRestController(UserServiceImpl userServiceImpl, RoleServiceImpl roleServiceImpl, PasswordEncoder passwordEncoder) {
        this.userServiceImpl = userServiceImpl;
        this.roleServiceImpl = roleServiceImpl;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userServiceImpl.findAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@RequestParam Long id) {
        return ResponseEntity.ok(userServiceImpl.findById(id));
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        String email = principal.getName();
        User user = userServiceImpl.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/new")
    public ResponseEntity<User> addNewUser(
            @RequestBody User user,
            @RequestParam("roles") Set<Long> roleIds) {

        User newUser = new User();

        Set<Role> roles = roleServiceImpl.findRolesByIds(roleIds);

        newUser.setFirstname(user.getFirstname());
        newUser.setLastname(user.getLastname());
        newUser.setAge(user.getAge());
        newUser.setEmail(user.getEmail());

        if (user.getPassword() != null) {
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        newUser.setRoles(roles);

        User savedUser = userServiceImpl.saveUser(newUser);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<User> showEditUserForm(@PathVariable("id") Long id) {
        User user = userServiceImpl.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/user/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") Long id,
            @RequestBody User user,
            @RequestParam("roles") Set<Long> roleIds) {

        User existingUser = userServiceImpl.findById(id);
        if (existingUser == null) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }

        Set<Role> roles = roleServiceImpl.findRolesByIds(roleIds);

        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());
        existingUser.setAge(user.getAge());
        existingUser.setEmail(user.getEmail());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setRoles(roles);

        userServiceImpl.updateUser(id, existingUser, roles);

        return ResponseEntity.ok(existingUser);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userServiceImpl.findById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userServiceImpl.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
