package com.project.demo.logic.entity.rol;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent>{
    private final RoleRepository roleRep;
    private final UserRepository userRep;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(RoleRepository roleRep, UserRepository userRep, PasswordEncoder passwordEncoder) {
        this.roleRep = roleRep;
        this.userRep = userRep;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createUser();
    }

    private void createUser() {
        User user = new User();
        user.setName("User");
        user.setLastname("Basic");
        user.setEmail("thebasic.user@gmail.com");
        user.setPassword("basicuser123");

        Optional<Role> optionalRole = roleRep.findByName(RoleEnum.USER);
        Optional<User> optionalUser = userRep.findByEmail(user.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var users = new User();
        users.setName(user.getName());
        users.setLastname(user.getLastname());
        users.setEmail(user.getEmail());
        users.setPassword(passwordEncoder.encode(user.getPassword()));
        users.setRole(optionalRole.get());
        userRep.save(users);
    }

}
