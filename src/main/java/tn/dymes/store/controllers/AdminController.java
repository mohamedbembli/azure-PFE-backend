package tn.dymes.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.entites.*;
import tn.dymes.store.repositories.ClaimRepository;
import tn.dymes.store.repositories.MyOrderRepository;
import tn.dymes.store.repositories.OrderLifeCycleRepository;
import tn.dymes.store.repositories.RatingRepository;
import tn.dymes.store.services.IClaimService;
import tn.dymes.store.services.IOrderService;
import tn.dymes.store.services.IRatingService;
import tn.dymes.store.services.IUserService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    IUserService userService;

    @Autowired
    OrderLifeCycleRepository orderLifeCycleRepository;

    @Autowired
    MyOrderRepository orderRepository;

    @Autowired
    IOrderService orderService;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    IRatingService ratingService;

    @Autowired
    ClaimRepository claimRepository;

    @Autowired
    IClaimService claimService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    @GetMapping("/retrieve-user/{user-id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    @ResponseBody
    //http://localhost:9092/admin/retrieve-user/8
    public User retrieveUser(@PathVariable("user-id") String userId) {
        return userService.retrieveUser(userId);
    }

    @GetMapping("/loadUser")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    @ResponseBody
    public User loadUser(Principal principal) {
        return userService.loadUser(principal.getName());
    }

    @PostMapping(path = "/checkUploadedFile")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> checkValidImage(@RequestParam("file") MultipartFile file) {
        try {
            // Check if the uploaded file is an image
            if (!userService.isAllowedFileType(file) || !userService.isImage(file.getInputStream())) {
                return new ResponseEntity<>(Map.of("message","Format de fichier invalide. Seuls les fichiers image sont autorisés."), HttpStatus.BAD_REQUEST);
            }

            if (!userService.isImageContentSecure(file.getInputStream())){
                return new ResponseEntity<>(Map.of("message","Le contenu du fichier n'est pas sécurisé."), HttpStatus.BAD_REQUEST);
            }

            // Check file size
            if (file.getSize() > MAX_FILE_SIZE) {
                return new ResponseEntity<>(Map.of("message","La taille du fichier dépasse la limite. Maximum 10 MO"), HttpStatus.BAD_REQUEST);
            }

            // Generate a unique file name
            String fileName = userService.generateUniqueFileName(file.getOriginalFilename());

            // Return a success response
            return new ResponseEntity<>(Map.of("message","Merci! Sauvegarder maintenant."), HttpStatus.OK);
        } catch (IOException e) {
            // Return an error response if any exception occurs during the upload process.
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/checkPassword")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> checkValidPass(@RequestParam("password") String pass, Principal principal) {
        try {
            if (this.userService.checkPassword(principal.getName(),pass))
                return new ResponseEntity<>(Map.of("message","Mot de passe correct."), HttpStatus.OK);
            else
                return new ResponseEntity<>(Map.of("message","Mot de passe incorrect."), HttpStatus.OK);
        } catch (Exception e) {
            // Return an error response if any exception occurs during the upload process.
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/updatePassword")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> updatePass(@RequestParam("password") String pass, Principal principal) {
        try {
            this.userService.updatePassword(principal.getName(),pass);
            return new ResponseEntity<>(Map.of("message","Votre mot de passe à été changé avec succès."), HttpStatus.OK);

        } catch (Exception e) {
            // Return an error response if any exception occurs during the upload process.
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(path = "/savePhotoProfile")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> updateProfilImage(@RequestParam("file") MultipartFile file, Principal principal, HttpServletRequest request) {
        try {
            this.userService.updatePhotoProfile(file,principal.getName());
            return new ResponseEntity<>(Map.of("message","Photo updated successfully"),HttpStatus.OK);
        } catch (Exception e) {
            // Return an error response if any exception occurs during the upload process.
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(path = "/savePrivData")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> savePrivData(@RequestParam("bio") String bio, @RequestParam("email") String email, Principal principal) {
        try {
            userService.savePrivData(bio,email,principal.getName());
            return new ResponseEntity<>(Map.of("message","Data Updated Successfully"),HttpStatus.OK);
        } catch (Exception e) {
            // Return an error response if any exception occurs during the upload process.
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/saveSecondData")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> saveSecondData(@RequestParam("gender") String gender, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName, @RequestParam("address") String address,
                                                              @RequestParam("phone") String phone, @RequestParam("dob") String dob, @RequestParam("country") String country, @RequestParam("city") String city,
                                                              @RequestParam("state") String state,  @RequestParam("zipcode") String zipcode, Principal principal)
    {
        try {
            userService.saveSecondsData(gender,firstName,lastName,address,phone,dob,country,city,state,zipcode,principal.getName());
            return new ResponseEntity<>(Map.of("message","Data Updated Successfully"),HttpStatus.OK);
        } catch (Exception e) {
            // Return an error response if any exception occurs during the upload process.
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getAllClients")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<List<User>> getAllClients() {
        try {
            List<User> clients = this.userService.retrieveAllClients();
            System.out.println("Number of clients found: " + clients.size());
            if (clients.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(clients, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/changeClientStatus")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> changeClientStatus(@RequestParam("userid") String userId, @RequestParam("status") boolean status) {
        try {
            this.userService.changeSuspensionStatus(userId,status);
            return new ResponseEntity<>(Map.of("message","Client status updated successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/deleteClient/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteClient (@PathVariable String userId) {
        try {
            this.userService.deleteClient(userId);
            return new ResponseEntity<>(Map.of("message","Client deleted successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getAllRating")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<List<Rating>> getAllRating() {
        try {
            List<Rating> ratingList = this.ratingRepository.findAll();
            System.out.println("Number of Rating found: " + ratingList.size());
            if (ratingList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(ratingList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/acceptRating")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> changeRatingStatus(@RequestParam("ratingID") String ratingID) {
        try {
            this.ratingService.acceptRating(Long.parseLong(ratingID));
            return new ResponseEntity<>(Map.of("message","Rating accepted successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/deleteRating/{ratingID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> deleteRating (@PathVariable String ratingID) {
        try {
            this.ratingService.deleteRating(Long.parseLong(ratingID));
            return new ResponseEntity<>(Map.of("message","Rating deleted successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getAllClaims")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<List<Claim>> getAllClaims() {
        try {
            List<Claim> claims = this.claimRepository.findAll();
            System.out.println("Number of claims found: " + claims.size());
            if (claims.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(claims, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(path = "/changeClaimStatus")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> changeClaimStatus(@RequestParam("claimID") String claimID) {
        try {
            this.claimService.changeClaimStatus(Long.parseLong(claimID));
            return new ResponseEntity<>(Map.of("message","Claim Solved."), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getAllOrderLifeCycle")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<List<OrderLifeCycle>> getAllOrderLifeCycle() {
        try {
            Sort sort = Sort.by(Sort.Direction.ASC, "position");
            List<OrderLifeCycle> orderLifeCycles = orderLifeCycleRepository.findAll(sort);
            System.out.println("Number of OrderLifeCycle found: " + orderLifeCycles.size());
            if (orderLifeCycles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(orderLifeCycles, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/getAllOrders")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<List<MyOrder>> getAllOrders() {
        try {
            List<MyOrder> orders = orderRepository.findAll();
            System.out.println("Number of orders found: " + orders.size());
            if (orders.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/changeOrderStatus")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> changeOrderStatus(@RequestParam("previousStepID") String previousStepID,
                                                                 @RequestParam("nextStepID") String nextStepID,
                                                                 @RequestParam("orderID") String orderID) {
        try {
            this.orderService.changeOrderStatus(Long.parseLong(orderID),Long.parseLong(previousStepID),Long.parseLong(nextStepID));
            return new ResponseEntity<>(Map.of("message","Status changed successfully."), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }




}