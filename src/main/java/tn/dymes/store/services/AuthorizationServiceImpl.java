package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.dtos.ModuleDTO;
import tn.dymes.store.entites.Authorization;
import tn.dymes.store.entites.User;
import tn.dymes.store.repositories.AuthorizationRepository;
import tn.dymes.store.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuthorizationServiceImpl implements IAuthorizationService{

    @Autowired
    AuthorizationRepository authorizationRepository;

    @Autowired
    UserRepository userRepository;


    @Override
    public void loadModules(List<ModuleDTO> modules) {
        for (ModuleDTO module : modules) {
            addAuthorization(module.name(),module.type(),module.parent());
        }
    }

    @Override
    public void addAuthorization(String name, String type, String parentModule) {
        boolean moduleFound = false;
        List<Authorization> authorizations = (List<Authorization>) authorizationRepository.findAll();

        if (authorizations != null) {
            for (Authorization authorization : authorizations) {
                String moduleParent = authorization.getModuleParent();

                if ((moduleParent == null && parentModule == null) || (moduleParent != null && moduleParent.equals(parentModule))) {
                    if (authorization.getName().equals(name) && authorization.getType().equals(type)) {
                        System.out.println("Authorization FOUND");
                        moduleFound = true;
                        break; // No need to continue searching if found
                    }
                }
            }
        }

        if (!moduleFound) {
            Authorization authorization = new Authorization();
            authorization.setName(name);
            authorization.setModuleParent(parentModule);
            authorization.setType(type);
            authorizationRepository.save(authorization);
            System.out.println("Authorization Added");
        }
    }

    @Override
    public void addAuthorizationsToUser(String userid, List<ModuleDTO> moduleDTOList) {
        User employee = userRepository.findById(userid).orElse(null);
        List<Authorization> authorizationList = new ArrayList<>();
        List<ModuleDTO> modules = moduleDTOList;
        for (ModuleDTO module : modules) {
            Authorization authorization = findByNameAndTypeAndParent(module.name(),module.type(),module.parent());
            if (authorization != null)
                System.out.println("autorization = "+authorization.toString());
                authorizationList.add(authorization);
        }
        employee.setAuthorizations(authorizationList);
        userRepository.save(employee);

    }

    @Override
    public Authorization findByNameAndTypeAndParent(String name, String type, String parent) {
        List<Authorization> authorizations = (List<Authorization>) authorizationRepository.findAll();
        if (authorizations != null) {
            for (Authorization authorization : authorizations) {
                if ((authorization.getModuleParent() == null && parent == null) || (authorization.getModuleParent() != null && authorization.getModuleParent().equals(parent))) {
                    if (authorization.getName().equals(name) && authorization.getType().equals(type))
                        return authorization;
                }
            }
        }
        return null;
    }

    @Override
    public void removeAllAuthorizationsFromEmployee(String userid) {
        User employee = userRepository.findById(userid).orElse(null);
        employee.getAuthorizations().clear();
        userRepository.save(employee);
    }


    @Override
    public List<Authorization> retrieveAllAuthorizationsFromUser(String userid) {
        return null;
    }

    @Override
    public void deleteAuthorization(String name, String type, String userid) {

    }

    @Override
    public Authorization Authorize(String name, String type, boolean isAutorised) {
        return null;
    }

    @Override
    public Authorization retrieveAuthorization(String name, String type, String userid) {
        return null;
    }

    @Override
    public void removeAuthorizationFromUser(String userid, String name, String type) {

    }

    @Override
    public void removeAuthorizationsFromUser(String userid, List<Authorization> authorizationList) {

    }

    @Override
    public void removeAllAuthorizationFromUser(String userid) {

    }
}
