package tn.dymes.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.dymes.store.entites.CheckoutField;
import tn.dymes.store.repositories.CheckoutFieldRepository;
import tn.dymes.store.services.ICheckoutFieldService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkoutField")
@CrossOrigin("*")
public class CheckoutFieldController {

    @Autowired
    CheckoutFieldRepository checkoutFieldRepository;

    @Autowired
    ICheckoutFieldService checkoutFieldService;

    @PutMapping(path = "/updateOrderConfirmationMSG")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> UpdateOrderConfirmationMSG(@RequestParam("msg") String msg) {
        try {
            this.checkoutFieldService.updateOrderConfirmationMsg(msg);
            return new ResponseEntity<>(Map.of("message","Confirm MSG updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/updateShown")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> updateShown(@RequestParam("fieldID") String fieldID) {
        try {
            this.checkoutFieldService.updateShown(Long.parseLong(fieldID));
            return new ResponseEntity<>(Map.of("message","Shown updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/updateIsRequired")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> updateIsRequired(@RequestParam("fieldID") String fieldID) {
        try {
            this.checkoutFieldService.updateIsRequired(Long.parseLong(fieldID));
            return new ResponseEntity<>(Map.of("message","isRequired updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getAll")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<List<CheckoutField>> getAllFields() {
        try {
            List<CheckoutField> checkoutFields = this.checkoutFieldService.getAll();
            System.out.println("Number of checkoutFields found: " + checkoutFields.size());
            if (checkoutFields.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(checkoutFields, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
