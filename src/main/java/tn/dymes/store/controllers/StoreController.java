package tn.dymes.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.dtos.AddUpSellDTO;
import tn.dymes.store.entites.Store;
import tn.dymes.store.repositories.StoreRepository;
import tn.dymes.store.services.IStoreService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/store")
@CrossOrigin("*")
public class StoreController {

    @Autowired
    StoreRepository storeRepository;
    @Autowired
    IStoreService storeService;


    @PutMapping(path = "/updateShippingFees")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> saveShippingFees(@RequestParam("shippingFees") String shippingFees, @RequestParam("totalCMD") String totalCMD ) {
        try {
            storeService.updateShippingFees(Float.parseFloat(shippingFees),Float.parseFloat(totalCMD));
            return new ResponseEntity<>(Map.of("message","Shipping Fees Data updated success."), HttpStatus.OK);
        }
        catch (NumberFormatException ex){
            Float FshippingFees = shippingFees.isEmpty() ? null : Float.parseFloat(shippingFees);
            Float FtotalCMD = totalCMD.isEmpty() ? null : Float.parseFloat(totalCMD);
            if ( FshippingFees instanceof Float && !(FtotalCMD instanceof Float)){
                storeService.updateShippingFees(FshippingFees,null);
            }
            if (!(FshippingFees instanceof Float) && FtotalCMD instanceof Float){
                storeService.updateShippingFees(null,FtotalCMD);
            }
            if (!(FshippingFees instanceof Float) && !(FtotalCMD instanceof Float)){
                storeService.updateShippingFees(null,null);
            }
            return new ResponseEntity<>(Map.of("message","Shipping Fees Data updated success."), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/updateSEO")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> saveSEOData(@RequestParam("seoDescription") String seoDescription, @RequestParam("seoStoreUrl") String seoStoreUrl, @RequestParam("seoTitle") String seoTitle) {
        try {
            storeService.addSEO(seoDescription,seoStoreUrl,seoTitle);
            return new ResponseEntity<>(Map.of("message","SEO Data updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/updateSocialMediaData")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> saveSocialMediaData(@RequestParam("facebookLink") String facebookLink, @RequestParam("instagramLink") String instagramLink, @RequestParam("linkedinLink") String linkedinLink,
                                                                   @RequestParam("whatsappLink") String whatsappLink, @RequestParam("snapchatLink") String snapchatLink, @RequestParam("tiktokLink") String tiktokLink,
                                                                   @RequestParam("youtubeLink") String youtubeLink) {
        try {
            storeService.addSocialMediaData(facebookLink,instagramLink,linkedinLink,whatsappLink,snapchatLink,tiktokLink,youtubeLink);
            return new ResponseEntity<>(Map.of("message","Social Media Data updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/updatePayMethodes")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> savePayMethodeData(@RequestParam("payWithBank") String payWithBank, @RequestParam("payInDelivery") String payInDelivery, @RequestParam("paymentByDefault") String paymentByDefault ) {
        try {
            storeService.addPayMethodesData(Boolean.parseBoolean(payInDelivery),Boolean.parseBoolean(payWithBank),paymentByDefault);
            return new ResponseEntity<>(Map.of("message","PayMethodes Data updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/updateLogos")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> updateLogos(@RequestParam(name = "imagesList", required = false) List<MultipartFile> imagesList) {
        try {
            System.out.println("imagesList ="+imagesList);

            this.storeService.addLogos(imagesList);
            return new ResponseEntity<>(Map.of("message","Logos updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getStoreData")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Store> getStoreData() {
        try {
            Store store = this.storeRepository.findById("Dymes").orElse(null);
            return new ResponseEntity<>(store, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping(path = "/updateStoreData")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> saveStoreData(@RequestParam("storeName") String storeName, @RequestParam("tvaCode") String tvaCode, @RequestParam("phone1") String phone1,
                                                             @RequestParam("phone2") String phone2, @RequestParam("state") String state, @RequestParam("city") String city,
                                                             @RequestParam("address") String address, @RequestParam("zipCode") String zipCode, @RequestParam("pays") String pays,
                                                             @RequestParam("email") String email ) {
        try {
            storeService.addStoreData(storeName,tvaCode,phone1,phone2,state,city,address,zipCode,pays,email);
            return new ResponseEntity<>(Map.of("message","Store Data updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/updateBankData")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> saveBankData(@RequestParam("bnkName") String bnkName, @RequestParam("designation") String designation, @RequestParam("agence") String agence,
                                                             @RequestParam("swift") String swift, @RequestParam("iban") String iban ) {
        try {
            storeService.addBankData(bnkName,designation,agence,swift,iban);
            return new ResponseEntity<>(Map.of("message","Bank Data updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }
}
