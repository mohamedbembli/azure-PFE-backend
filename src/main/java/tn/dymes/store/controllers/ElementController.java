package tn.dymes.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.dymes.store.entites.Element;
import tn.dymes.store.services.IElementService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/elements")
@CrossOrigin("*")
public class ElementController {

    @Autowired
    IElementService elementService;

    @PostMapping(path = "/delete")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> DeleteElement(@RequestParam("attributeID") long attributeID, @RequestParam("name") String name, @RequestParam("reference") String reference) {
        try {
            this.elementService.removeElement(attributeID,name,reference);
            return new ResponseEntity<>(Map.of("message","Element deleted successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> saveElement(@RequestParam("attributeID") long attributeID, @RequestParam("name") String name, @RequestParam("reference") String reference)
    {
        try {
            if (elementService.isExist(attributeID,name,reference))
                return new ResponseEntity<>(Map.of("message","Element already exist"), HttpStatus.OK);
            else{
                elementService.addElement(attributeID,name,reference);
                return new ResponseEntity<>(Map.of("message","Element added Successfully"), HttpStatus.OK);
            }
        } catch (Exception e) {
            // Return an error response if any exception occurs during the upload process.
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/update")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, Element>>  updateElement(@RequestParam("elementID") Long elementID, @RequestParam("name") String name, @RequestParam("reference") String reference) {
        try {
            Element element = this.elementService.updateElement(elementID,name,reference);
            return new ResponseEntity<>(Map.of("message",element), HttpStatus.OK);
        } catch (Exception e) {
            // Handle the exception and send a custom error response.
            Map<String, Element> errorResponse = new HashMap<>();
            errorResponse.put("error", null); // You can set an error message or leave it as null.
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/getElements/{attributeID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<List<Element>> getElementsByAttributeID(@PathVariable Long attributeID) {
        try {
            List<Element> elements = this.elementService.getElementsByAttributeID(attributeID);
            if (elements == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(elements, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
