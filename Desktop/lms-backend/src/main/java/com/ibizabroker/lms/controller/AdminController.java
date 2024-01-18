package com.ibizabroker.lms.controller;

import com.ibizabroker.lms.dao.UsersRepository;
import com.ibizabroker.lms.entity.Role;
import com.ibizabroker.lms.entity.Users;
import com.ibizabroker.lms.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin("http://localhost:4200/")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/users")
//    @PreAuthorize("hasRole('Admin')")
    public Users addUserByAdmin(@RequestBody Users user) {
        Role role = new Role();
        System.out.println("User=="+user);
       //role.setRoleName(user.getRoleName());
        if(user.getRoleName().equals("Admin")) {
        	
        	role.setRoleName("Admin");
        }else if(user.getRoleName().equals("User")) {
        	
        	role.setRoleName("User");
		}
        Set<Role> setRole = new HashSet<>();
        setRole.add(role);
        user.setRole(setRole);
   System.out.println(user);
        String password = user.getPassword();
        String encryptPassword = passwordEncoder.encode(password);
        user.setPassword(encryptPassword);
        System.out.println("User with role=="+user);
        usersRepository.save(user);
        return user;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('Admin')")
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/users/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Integer id) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id "+ id +" does not exist."));
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/users/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Integer id, @RequestBody Users userDetails) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id "+ id +" does not exist."));

        user.setName(userDetails.getName());
        user.setRole(userDetails.getRole());
        user.setUsername(userDetails.getUsername());

        Users updatedUser = usersRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
}
