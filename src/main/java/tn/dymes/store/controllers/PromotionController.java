package tn.dymes.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.dymes.store.entites.Product;
import tn.dymes.store.entites.Promotion;
import tn.dymes.store.repositories.PromotionRepository;
import tn.dymes.store.services.IPromotionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/promotion")
@CrossOrigin("*")
public class PromotionController {

    @Autowired
    IPromotionService promotionService;

    @Autowired
    PromotionRepository promotionRepository;

    @PutMapping(path = "/update")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> updatePromotion(@RequestParam("idPromo") String idPromo, @RequestParam("promoCode") String promoCode, @RequestParam("promoType") String promoType, @RequestParam("discountType") String discountType, @RequestParam(name = "discountValue", required = false) String discountValue, @RequestParam(name = "startDate", required = false) String startDate, @RequestParam(name = "endDate", required = false) String endDate) {
        try {
                Promotion promotion = promotionRepository.findById(Long.parseLong(idPromo)).orElse(null);
                if (promotion != null){
                    Float discount = null;
                    if (discountValue != null && !discountValue.isEmpty()) {
                        discount = Float.parseFloat(discountValue);
                    }
                    this.promotionService.updatePromo(Long.parseLong(idPromo), promoCode, promoType, startDate, endDate, discountType, discount);
                    return new ResponseEntity<>(Map.of("message", "Promotion updated successfully."), HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(Map.of("message", "Promotion not exist."), HttpStatus.OK);

                }

        } catch (NumberFormatException e) {
            Float discount = null;
            this.promotionService.updatePromo(Long.parseLong(idPromo), promoCode, promoType, startDate, endDate, discountType, discount);
            return new ResponseEntity<>(Map.of("message", "Promotion updated successfully."), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getAll")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        try {
            List<Promotion> promotions = promotionRepository.findAll();
            System.out.println("Number of promotions found: " + promotions.size());
            if (promotions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(promotions, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/updateStatus")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> UpdatePromotionStatus(@RequestParam("promoID") String promoID,@RequestParam("status") String status ) {
        try {
            this.promotionService.changeStatus(Long.parseLong(promoID),status);
            return new ResponseEntity<>(Map.of("message", "Promotion status updated."), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/delete/{promotionID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> deletePromotion(@PathVariable long promotionID) {
        try {
            this.promotionRepository.deleteById(promotionID);
            return new ResponseEntity<>(Map.of("message", "Promo deleted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> addPromotion(@RequestParam("promoCode") String promoCode, @RequestParam("promoType") String promoType, @RequestParam("discountType") String discountType, @RequestParam(name = "discountValue", required = false) String discountValue, @RequestParam(name = "startDate", required = false) String startDate, @RequestParam(name = "endDate", required = false) String endDate) {
        try {
            if (promotionService.isPromoExist(promoCode)){
                return new ResponseEntity<>(Map.of("message", "Promotion already exist."), HttpStatus.OK);
            }else{
                Float discount = null;
                if (discountValue != null && !discountValue.isEmpty()) {
                    discount = Float.parseFloat(discountValue);
                }
                this.promotionService.addPromo(promoCode, promoType, startDate, endDate, discountType, discount);
                return new ResponseEntity<>(Map.of("message", "Promotion added successfully."), HttpStatus.OK);
            }
        } catch (NumberFormatException e) {
            Float discount = null;
            this.promotionService.addPromo(promoCode, promoType, startDate, endDate, discountType, discount);
            return new ResponseEntity<>(Map.of("message", "Promotion added successfully."), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }
}
