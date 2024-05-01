package tn.dymes.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.dymes.store.entites.Attribute;
import tn.dymes.store.services.IAttributeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attributes")
@CrossOrigin("*")
public class AttributeController {

    @Autowired
    IAttributeService attributeService;

    @GetMapping(path = "/getNbElements/{attributeID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> getNbElements(@PathVariable Long attributeID) {
        try {
            int nb = this.attributeService.getNbElementsByID(attributeID);
            if (nb == 0) {
                return new ResponseEntity<>(Map.of("message","There are no elements in this attribute"), HttpStatus.OK);
            }
            else if (nb == -1) {
                return new ResponseEntity<>(Map.of("message","There are no attribute"), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(Map.of("message", String.valueOf(nb)), HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/update")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, Attribute>>  updateAttribute(@RequestParam("attributeID") Long attributeID, @RequestParam("name") String name, @RequestParam("type") String type) {
        try {
            Attribute attribute = this.attributeService.updateAttribute(attributeID,name,type);
            return new ResponseEntity<>(Map.of("message",attribute), HttpStatus.OK);
        } catch (Exception e) {
            // Handle the exception and send a custom error response.
            Map<String, Attribute> errorResponse = new HashMap<>();
            errorResponse.put("error", null); // You can set an error message or leave it as null.
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> saveAttribute(@RequestParam("name") String name, @RequestParam("type") String type)
    {
        try {
            attributeService.addAttribute(name,type);
            return new ResponseEntity<>(Map.of("message","Attribute added Successfully"), HttpStatus.OK);
        } catch (Exception e) {
            // Return an error response if any exception occurs during the upload process.
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getAll")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<List<Attribute>> getAllAttributes() {
        try {
            List<Attribute> attributes = this.attributeService.getAll();
            if (attributes == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(attributes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/delete/{attributeID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> DeleteAttribute(@PathVariable Long attributeID) {
        try {
            this.attributeService.removeAttribute(attributeID);
            return new ResponseEntity<>(Map.of("message","Attribute deleted successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

}
