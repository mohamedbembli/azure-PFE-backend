package tn.dymes.store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.dtos.AddEmployeeDTO;
import tn.dymes.store.entites.User;
import tn.dymes.store.enums.Gender;
import tn.dymes.store.repositories.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService{


    @Autowired
    UserRepository userRepository;

    @Autowired
    IAuthorizationService authorizationService;

    @Autowired
    IMailService mailService;

    @Autowired
    IRoleService roleService;

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Override
    public void updateClientAddress(String clientID, String gender, String firstName, String lastName, String address, String phone, String phone2, String city, String zipCode) {
        User client = userRepository.findById(clientID).orElse(null);
        // check if user role is "USER"
        if (client.getRoles().get(0).getName().equals("USER")){
            client.setGender(Gender.valueOf(gender));
            client.setFirstName(firstName);
            client.setLastName(lastName);
            client.setAddress(address);
            client.setPhone(phone);
            client.setPhone2(phone2);
            client.setCity(city);
            client.setZipCode(zipCode);
            userRepository.save(client);
        }
    }

    @Override
    public void removeClientAddress(String clientID) {
        User client = userRepository.findById(clientID).orElse(null);
        if (client != null){
            client.setFirstName(null);
            client.setLastName(null);
            client.setAddress(null);
            client.setPhone(null);
            client.setPhone2(null);
            client.setCity(null);
            client.setZipCode(null);
            userRepository.save(client);
        }
    }

    @Override
    public void changeSuspensionStatus(String userid, boolean status) {
        User user = userRepository.findById(userid).orElse(null);
        if (user != null){
            user.setSuspension(status);
            userRepository.save(user);
        }
    }

    @Override
    public void addEmployee(AddEmployeeDTO employeeDTO) {
        User employee = new User();
        employee.setId(UUID.randomUUID().toString());
        employee.setGender(Gender.valueOf(employeeDTO.gender()));
        employee.setFirstName(employeeDTO.firstName());
        employee.setLastName(employeeDTO.lastName());
        employee.setEmail(employeeDTO.email().toLowerCase());
        employee.setPassword(employeeDTO.password());
        employee.setPhone(employeeDTO.phoneNumber());
        employee.setBio(employeeDTO.BIO());
        employee.setCreate_account_date(new Date());
        employee.setProfilePhoto(null);
        addUser(employee);
        // add role employee
        this.roleService.addRoleToUser(employee.getId(), "EMPLOYEE");

        // assign autorization to employee
        this.authorizationService.addAuthorizationsToUser(employee.getId(),employeeDTO.modules());

        // send mail notification to employee contain email + pass
          this.mailService.sendEmail(employee.getEmail(), "Dymes Access Credentials", "Cher " + employee.getFirstName() + " " + employee.getLastName()
                + ",\nBienvenue! \nVoici vos données d'accès à Dymes Administration: \nEmail: " + employee.getEmail() + "\nMot de passe: " + employeeDTO.password());

    }

    @Override
    public void addClient(String email, String pass, String confirmPass) {
        if (pass.equals(confirmPass)){
            User client = new User();
            client.setEmail(email.toLowerCase());
            client.setPassword(pass);
            addUser(client);
            this.roleService.addRoleToUser(client.getId(),"USER");
            // send mail notification to employee contain email + pass
           // this.mailService.sendEmail(client.getEmail(), "Votre compte sur Dymes TN a été créé", "Cher Client" + ",\nBienvenue sur Dymes TN! \nMerci d’avoir créé un compte sur Dymes TN. Votre identifiant est "+client.getEmail()+". Vous pouvez accéder à l’espace membre de votre compte pour visualiser vos commandes, changer votre mot de passe, et plus encore ici : \n http://localhost:4200/loginUsr \n \n Au plaisir de vous revoir prochainement sur notre boutique.\n");

        }

    }

    @Override
    public void updateEmployee(AddEmployeeDTO employeeDTO) {
        User employee = this.findUserByEmail(employeeDTO.email().toLowerCase());
        if (employee != null){
            employee.setGender(Gender.valueOf(employeeDTO.gender()));
            employee.setFirstName(employeeDTO.firstName());
            employee.setLastName(employeeDTO.lastName());
            employee.setEmail(employeeDTO.email().toLowerCase());
            employee.setPassword(this.passwordEncoder().encode(employeeDTO.password()));
            employee.setPhone(employeeDTO.phoneNumber());
            employee.setBio(employeeDTO.BIO());
            this.updateUser(employee);
            this.authorizationService.removeAllAuthorizationFromUser(employee.getId());
        }
        // assign autorization again to employee
        this.authorizationService.addAuthorizationsToUser(employee.getId(),employeeDTO.modules());

    }


    @Override
    public List<User> findAllEmployee() {
        List<User> employees = userRepository.findUsersByRoleName("EMPLOYEE");
        System.out.println("Employees found: " + employees.size());
        return employees;
    }


    @Override
    public User addUser(User user) {
            user.setId(UUID.randomUUID().toString());
            user.setPassword(this.passwordEncoder().encode(user.getPassword()));
            return userRepository.save(user);
    }


    @Override
    public User findUserByEmail(String email) {
        List<User> users = (List<User>) userRepository.findAll();
            for (User user: users){
                if (user.getEmail().toLowerCase().equals(email.toLowerCase())){
                    System.out.println("USER FOUND");
                    return user;
                }
            }
        System.out.println("USER NOT FOUND");
        return null;
    }

    @Override
    public List<User> retrieveAllUsers() {
        List<User> users = (List<User>) userRepository.findAll();
        return users;
    }

    @Override
    public List<User> retrieveAllClients() {
        return userRepository.findUsersByRoles_Name("USER");
    }

    @Override
    public void deleteUser(String id) {
        List<User> employees = userRepository.findUsersByRoleName("EMPLOYEE");
        System.out.println("Employees found before delete: " + employees.size());
        userRepository.deleteById(id);
        List<User> employees2 = userRepository.findUsersByRoleName("EMPLOYEE");
        System.out.println("Employees found after delete: " + employees2.size());
    }

    @Override
    public void deleteClient(String id) {
        List<User> clients = userRepository.findUsersByRoleName("USER");
        System.out.println("Clients found before delete: " + clients.size());
        userRepository.deleteById(id);
        List<User> clients2 = userRepository.findUsersByRoleName("USER");
        System.out.println("Clients found after delete: " + clients2.size());
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User retrieveUser(String id) {
        User user = userRepository.findById(id).orElse(null);
        return user;
    }

    @Override
    public User retrieveUserByEmailAndPassword(String email, String password) {
        User user = this.findUserByEmail(email);
        if (user!=null){
            if (user.getPassword() == passwordEncoder().encode(password))
                return user;
        }
        return null;
    }


    @Override
    public String generateUniqueFileName(String originalFileName) {
        // generate unique string
        return "unique_" + System.currentTimeMillis() + "_" + originalFileName;
    }

    @Override
    public boolean isImage(InputStream inputStream) {
        try {
            // Use javax.imageio.ImageIO to check if the input stream is a valid image
            ImageIO.read(inputStream);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isAllowedFileType(MultipartFile file) {
        // Implement logic to check allowed file types based on MIME type or extension
        String mimeType = file.getContentType();
        if (mimeType != null && mimeType.startsWith("image/") ) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isImageContentSecure(InputStream inputStream) {
        try {
            // Load the image using ImageIO
            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                // The input stream does not contain a valid image
                return false;
            }

            return true;
        } catch (IOException e) {
            // Handle the exception here
            return false;
        }
    }

    @Override
    public boolean checkPassword(String id, String password) {
        User user = userRepository.findById(id).orElse(null);
        if (this.passwordEncoder().matches(password,user.getPassword()))
            return true;
        return false;
    }

    @Override
    public void updatePassword(String userid, String newPass) {
        User user = userRepository.findById(userid).orElse(null);
        user.setPassword(this.passwordEncoder().encode(newPass));
        userRepository.save(user);

    }

    @Override
    public User loadUser(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void updatePhotoProfile(MultipartFile file, String userId) throws IOException {
        // Define the uploads directory within the static resources
        String uploadsDirPath = "src/main/resources/static/uploads/";

        // Create the uploads directory if it doesn't exist
        File directory = new File(uploadsDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the uploaded file to the uploads directory
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = Paths.get(uploadsDirPath, fileName);
        Files.write(filePath, file.getBytes());

        // Update profile photo URL in the database
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            String filename = fileName;
            System.out.println(filename);
            user.setProfilePhoto(filename);
            userRepository.save(user);
        }
    }

    @Override
    public byte[] getPublicPhoto(String userId) throws IOException {
        User user =userRepository.findById(userId).orElse(null);
        String profilePath = "src/main/resources/static/uploads";
        Path path=Paths.get(profilePath,user.getProfilePhoto());
        return Files.readAllBytes(path);
    }

    @Override
    public void savePrivData(String bio, String email, String userid) {
        User user = userRepository.findById(userid).orElse(null);
        if (email != null)
        user.setEmail(email.toLowerCase());
        if (bio != null)
        user.setBio(bio);
        userRepository.save(user);
    }

    @Override
    public void saveSecondsData(String gender, String firstName, String lastName, String address, String phone, String dob, String country, String city, String state, String zipcode, String userid) {
        User user = userRepository.findById(userid).orElse(null);
        if (gender.equals("MALE")){
            user.setGender(Gender.valueOf("MALE"));
        }
        else if (gender.equals("FEMALE")){
            user.setGender(Gender.valueOf("FEMALE"));
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAddress(address);
        user.setPhone(phone);
        user.setDob(dob);
        user.setCountry(country);
        user.setCity(city);
        user.setState(state);
        user.setZipCode(zipcode);
        userRepository.save(user);
    }


}
