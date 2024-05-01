package tn.dymes.store.services;


import tn.dymes.store.entites.Role;

import java.util.List;

public interface IRoleService {
    public Role addNewRole (String name);
    List<Role> retrieveAllRoles();
    void deleteRole(String name);
    Role updateRole(String rolename, String newRoleName);
    Role retrieveRole(String name);
    void addRoleToUser(String userid, String role);
    void removeRoleFromUser(String userid, String role);


}
