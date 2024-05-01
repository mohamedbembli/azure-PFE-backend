package tn.dymes.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.entites.ProductCategory;
import tn.dymes.store.repositories.ProductCategoryRepository;
import tn.dymes.store.services.IProductCategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productCategory")
@CrossOrigin("*")
public class ProductCategoryController {

    @Autowired
    IProductCategoryService productCategoryService;

    @Autowired
    ProductCategoryRepository productCategoryRepository;


    @PutMapping(path = "/update")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, ProductCategory>>  updateProductCategory(@RequestParam("categoryID") Long categoryID, @RequestParam("name") String name, @RequestParam("parentName") String parentName, @RequestParam("description") String description) {
        try {
            ProductCategory productCategory = this.productCategoryService.updateProductCategory(categoryID,name,parentName,description);
            return new ResponseEntity<>(Map.of("message",productCategory), HttpStatus.OK);
        } catch (Exception e) {
            // Handle the exception and send a custom error response.
            Map<String, ProductCategory> errorResponse = new HashMap<>();
            errorResponse.put("error", null); // You can set an error message or leave it as null.
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(path = "/changeStatus")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> changeStatus(@RequestParam("pCategoryID") Long pCategoryID, @RequestParam("status") boolean status) {
        try {
            this.productCategoryService.changeStatus(pCategoryID,status);
            return new ResponseEntity<>(Map.of("message","productCategory status updated successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/delete/{productCategoryID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> DeleteEmployee(@PathVariable Long productCategoryID) {
        try {
            this.productCategoryService.deleteProductCategory(productCategoryID);
            return new ResponseEntity<>(Map.of("message","productCategory deleted successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, ProductCategory>> addProductCategory(@RequestParam("name") String name, @RequestParam("description") String description, @RequestParam("parentName") String parentName) {
        try {
            ProductCategory productCategory = this.productCategoryService.addProductCategory(name,description,parentName);
                return new ResponseEntity<>(Map.of("message",productCategory), HttpStatus.OK);
        } catch (Exception e) {
            // Handle the exception and send a custom error response.
            Map<String, ProductCategory> errorResponse = new HashMap<>();
            errorResponse.put("error", null); // You can set an error message or leave it as null.
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/getAll")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<List<ProductCategory>> getAllProductCategory() {
        try {
            List<ProductCategory> productCategories = this.productCategoryService.retrieveAllProductCategory();
            System.out.println("Number of product category found: " + productCategories.size());
            if (productCategories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(productCategories, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/saveCoverImage")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, String>> updateCoverImage(@RequestParam("file") MultipartFile file,@RequestParam("categoryID") Long pCategoryID) {
        try {
            this.productCategoryService.updateCoverPhoto(file,pCategoryID);
            return new ResponseEntity<>(Map.of("message","Photo updated successfully"), HttpStatus.OK);
        } catch (Exception e) {
            // Return an error response if any exception occurs during the upload process.
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/checkProductCategory")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE')")
    public ResponseEntity<Map<String, ProductCategory>> checkProductCategory(@RequestParam("name") String name, @RequestParam("parentName") String parentName) {
        try {
            ProductCategory productCategory = this.productCategoryService.isProductCategoryExist(name,parentName);
            if (productCategory == null){
                Map<String, ProductCategory> errorResponse = new HashMap<>();
                errorResponse.put("message", null); // You can set an error message or leave it as null.
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            else
                return new ResponseEntity<>(Map.of("message",productCategory), HttpStatus.OK);
        } catch (Exception e) {
            // Handle the exception and send a custom error response.
            Map<String, ProductCategory> errorResponse = new HashMap<>();
            errorResponse.put("message", null); // You can set an error message or leave it as null.
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
