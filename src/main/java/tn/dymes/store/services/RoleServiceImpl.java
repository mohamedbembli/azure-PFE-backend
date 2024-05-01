package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.Role;
import tn.dymes.store.entites.User;
import tn.dymes.store.repositories.RoleRepository;
import tn.dymes.store.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RoleServiceImpl implements IRoleService{

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;


    @Override
    public Role addNewRole(String name) {
        Role role = roleRepository.findById(name).orElse(null);
        System.out.println("ROLE = "+role);
        if (role== null) return roleRepository.save(new Role(name));
        return null;
    }

    @Override
    public List<Role> retrieveAllRoles() {
        List<Role> roles = (List<Role>) roleRepository.findAll();
 
        return roles;
    }

    @Override
    public void deleteRole(String name) {
        Role role = roleRepository.findById(name).orElse(null);
        if (role!=null) roleRepository.deleteById(name);
    }

    @Override
    public Role updateRole(String rolename, String newRoleName) {
        Role role = roleRepository.findById(rolename).orElse(null);
        role.setName(newRoleName);
        return roleRepository.save(role);
    }

    @Override
    public Role retrieveRole(String name) {
        return roleRepository.findById(name).orElse(null);
    }

    @Override
    public void addRoleToUser(String userid, String role) {
        User user = userRepository.findById(userid).orElse(null);
        Role uRole = roleRepository.findById(role).get();
        List<Role> list = new ArrayList<>();
        list.add(uRole);
        user.setRoles(list);
        userRepository.save(user);

    }

    @Override
    public void removeRoleFromUser(String userid, String role) {
        User user = userRepository.findById(userid).orElse(null);
        Role uRole = roleRepository.findById(role).get();
        if (user!=null) {
            user.getRoles().remove(uRole);
            userRepository.save(user);
        }
    }
}
