package tn.dymes.store.services;


import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.dtos.AddEmployeeDTO;
import tn.dymes.store.entites.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IUserService {
    void updateClientAddress(String clientID, String gender, String firstName, String lastName, String address, String phone, String phone2, String city, String zipCode);
    void removeClientAddress(String clientID);
    void changeSuspensionStatus(String userid, boolean status);
    void addEmployee(AddEmployeeDTO employeeDTO);

    void addClient(String email, String pass, String confirmPass);

    void updateEmployee(AddEmployeeDTO employeeDTO);

    List<User> findAllEmployee();

    public User addUser (User user);
    public User findUserByEmail(String email);
    List<User> retrieveAllUsers();

    List<User> retrieveAllClients();
    void deleteUser(String id);
    void deleteClient(String id);
    User updateUser(User user);
    User retrieveUser(String id);
    User retrieveUserByEmailAndPassword(String email, String password);

    void updatePassword(String userid, String newPass);

    String generateUniqueFileName(String originalFileName);

    boolean isImage(InputStream inputStream);

    boolean isAllowedFileType(MultipartFile file);

    boolean isImageContentSecure(InputStream inputStream);

    boolean checkPassword(String id, String password);

    User loadUser(String id);

    void updatePhotoProfile(MultipartFile file, String userid) throws IOException;

    byte[] getPublicPhoto(String userId) throws IOException;

    void savePrivData(String bio, String email, String userid);

     void saveSecondsData(String gender, String firstName, String lastName, String address, String phone, String dob, String country,
                          String city, String state, String zipcode, String userid);

}
