package tn.dymes.store.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.dymes.store.entites.MyOrder;
import tn.dymes.store.entites.Promotion;
import tn.dymes.store.entites.User;
import tn.dymes.store.repositories.ClaimRepository;
import tn.dymes.store.services.IClaimService;
import tn.dymes.store.services.IOrderService;
import tn.dymes.store.services.IUserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client")
@CrossOrigin("*")
public class ClientController {

    @Autowired
    IClaimService claimService;

    @Autowired
    ClaimRepository claimRepository;

    @Autowired
    IUserService userService;

    @Autowired
    IOrderService orderService;

    @GetMapping("/loadUser")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @ResponseBody
    public User loadUser(Principal principal) {
        return userService.loadUser(principal.getName());
    }

    @PostMapping(path = "/updateAddress")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<Map<String, String>> updateAddress(Principal principal, @RequestParam(name = "gender") String gender, @RequestParam(name = "firstName") String firstName, @RequestParam(name = "lastName") String lastName, @RequestParam(name = "address") String address,
                                                             @RequestParam(name = "phone") String phone, @RequestParam(name = "phone2") String phone2,
                                                             @RequestParam(name = "city") String city, @RequestParam(name = "zipCode") String zipCode){
        try {
            this.userService.updateClientAddress(principal.getName(),gender,firstName,lastName,address,phone,phone2,city,zipCode);
            return new ResponseEntity<>(Map.of("message","Address updated success."), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/removeAddress")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<Map<String, String>> removeAddress(Principal principal){
        try {
            this.userService.removeClientAddress(principal.getName());
            return new ResponseEntity<>(Map.of("message","Address deleted success."), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/orders/addClientOrder")
    @PreAuthorize("hasAuthority('SCOPE_USER') || hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> addClientOrder(Principal principal, @RequestParam("checkoutDataArray") String checkoutDataArrayJson,
                                                              @RequestParam(name = "totalPrices") String totalPricesJson,
                                                              @RequestParam(name = "totalWithTax") String totalWithTax,
                                                              @RequestParam(name = "shippingFees") String shippingFees,
                                                              @RequestParam(name = "promotion") String promotionJson,
                                                              @RequestParam(name = "clientType") String clientType,
                                                              @RequestParam(name = "paymentType") String paymentType) {


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse the JSON strings into arrays
            Map<String, Object>[] checkoutDataArray = objectMapper.readValue(checkoutDataArrayJson, new TypeReference<Map<String, Object>[]>() {});
            Map<String, Object>[] totalPrices = objectMapper.readValue(totalPricesJson, new TypeReference<Map<String, Object>[]>() {});

            // Deserialize promotion JSON string into a Promotion object
            Promotion promotion = null;
            if (!promotionJson.equals("null")) {
                promotion = objectMapper.readValue(promotionJson, Promotion.class);
            }

            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Define the desired date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            // Format the date and time using the formatter
            String formattedDateTime = now.format(formatter);

            // Add the order
            long orderid = this.orderService.addClientOrder(formattedDateTime, checkoutDataArray, totalPrices, Float.parseFloat(totalWithTax), Float.parseFloat(shippingFees), promotion, paymentType, clientType,principal.getName());
            if (orderid == -1){
                return new ResponseEntity<>(Map.of("message","out of stock."), HttpStatus.OK);
            }
            return new ResponseEntity<>(Map.of("message", "Order added successfully."+orderid), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/orders/addFastCheckoutClient")
    @PreAuthorize("hasAuthority('SCOPE_USER') || hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> fastCheckoutClient(Principal principal, @RequestParam(name = "fullName") String fullName,
                                                                 @RequestParam(name = "phone") String phone,
                                                                 @RequestParam(name = "address") String address,
                                                                 @RequestParam(name = "shippingFees") String shippingFees,
                                                                 @RequestParam(name = "totalWithTax") String totalWithTax,
                                                                 @RequestParam(name = "productID") String productID,
                                                                 @RequestParam(name = "productType") String productType,
                                                                 @RequestParam(name = "productName") String productName,
                                                                 @RequestParam(name = "qte") String qte,
                                                                 @RequestParam(name = "description") String description,
                                                                 @RequestParam(name = "total") String total
    ) {
        try {
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Define the desired date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            // Format the date and time using the formatter
            String formattedDateTime = now.format(formatter);

            long orderid = this.orderService.addOrderFastCheckoutClient(formattedDateTime, fullName,phone,address,Float.parseFloat(shippingFees),Float.parseFloat(totalWithTax),"CLIENT",Long.parseLong(productID),Integer.parseInt(qte)
                        ,description,Float.parseFloat(total),productType,productName,principal.getName());

            if (orderid == -1){
                return new ResponseEntity<>(Map.of("message","out of stock."), HttpStatus.OK);
            }
            return new ResponseEntity<>(Map.of("message","Order added successfully."+orderid), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/orders/getOrders")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity <List<MyOrder>> getOrdersByUser(Principal principal) {
        try {
            List<MyOrder> myOrderList = this.orderService.getOrdersByUserId(principal.getName());
            if (myOrderList.size() == 0) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(myOrderList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/orders/cancelOrder")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<Map<String, String>> cancelOrderClient(Principal principal, @RequestParam(name = "orderID") String orderID) {
        try {
            this.orderService.cancelOrder(Long.parseLong(orderID),principal.getName());
            return new ResponseEntity<>(Map.of("message","Order cancelled successfully."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/orders/addClaim")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<Map<String, String>> addClaimClient(Principal principal,
                                                              @RequestParam(name = "subject") String subject,
                                                              @RequestParam(name = "comment") String comment,
                                                              @RequestParam(name = "orderID") String orderID) {
        try {
            this.claimService.addClaim(subject,comment,Long.parseLong(orderID));
            return new ResponseEntity<>(Map.of("message","Claim added success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

}
