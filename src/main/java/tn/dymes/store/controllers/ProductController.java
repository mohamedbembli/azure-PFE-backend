package tn.dymes.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.dtos.AddProductDTO;
import tn.dymes.store.entites.Product;
import tn.dymes.store.services.IProductService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    IProductService productService;

    @GetMapping(path = "/getAll")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = this.productService.getAll();
            System.out.println("Number of products found: " + products.size());
            if (products.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/update")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> updateProduct(@RequestParam("productID") long productID, @RequestParam("dataToSend") String dataToSend, @RequestParam(name = "imagesToUpload", required = false) List<MultipartFile> imagesToUpload, @RequestParam(name = "variantsImages", required = false) List<MultipartFile> variantsImages) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            AddProductDTO addProductDTO = objectMapper.readValue(dataToSend, AddProductDTO.class);
            Product product = this.productService.updateProduct(productID,addProductDTO,imagesToUpload,variantsImages);
            if (product != null) {
                return new ResponseEntity<>(Map.of("message", "Product updated success."), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(Map.of("message", "Product updated failed."), HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> addProduct(@RequestParam("dataToSend") String dataToSend, @RequestParam(name = "imagesToUpload", required = false) List<MultipartFile> imagesToUpload, @RequestParam(name = "variantsImages", required = false) List<MultipartFile> variantsImages) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            AddProductDTO addProductDTO = objectMapper.readValue(dataToSend, AddProductDTO.class);
            this.productService.addProduct(addProductDTO,imagesToUpload,variantsImages);
            return new ResponseEntity<>(Map.of("message","Product added success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/get/{productID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Product> getProduct(@PathVariable long productID) {
        try {

            Product product = this.productService.getProduct(productID);
            if (product != null){
                return new ResponseEntity<>(product, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
