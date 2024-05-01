package tn.dymes.store.services;

import tn.dymes.store.dtos.ModuleDTO;
import tn.dymes.store.entites.Authorization;
import tn.dymes.store.entites.Role;

import java.util.List;

public interface IAuthorizationService {

    void loadModules(List<ModuleDTO> moduleDTOList);
    void addAuthorization(String name, String type, String parentModule);
    public void addAuthorizationsToUser (String userid, List<ModuleDTO> moduleDTOList);

    Authorization findByNameAndTypeAndParent(String name, String type, String parent);

    void removeAllAuthorizationsFromEmployee(String userid);
    List<Authorization> retrieveAllAuthorizationsFromUser(String userid);
    void deleteAuthorization(String name, String type, String userid);
    Authorization Authorize(String name, String type, boolean isAutorised);
    Authorization retrieveAuthorization(String name, String type, String userid);
    void removeAuthorizationFromUser(String userid, String name, String type);
    void removeAuthorizationsFromUser(String userid, List<Authorization> authorizationList);
    void removeAllAuthorizationFromUser(String userid);
}
