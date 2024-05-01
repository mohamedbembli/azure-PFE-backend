package tn.dymes.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.entites.OrderLifeCycle;
import tn.dymes.store.entites.Upsell;
import tn.dymes.store.repositories.OrderLifeCycleRepository;
import tn.dymes.store.services.IOrderLifeCycleService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orderLifeCycle")
@CrossOrigin("*")
public class OrderLifeCycleController {
    @Autowired
    OrderLifeCycleRepository orderLifeCycleRepository;

    @Autowired
    IOrderLifeCycleService orderLifeCycleService;


    @GetMapping(path = "/getAll")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<List<OrderLifeCycle>> getAllSteps() {
        try {
            List<OrderLifeCycle> orderLifeCycles = this.orderLifeCycleService.findAll();
            System.out.println("Number of upsells found: " + orderLifeCycles.size());
            if (orderLifeCycles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(orderLifeCycles, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/updateStatus")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> updateStatus(@RequestParam("stepID") String stepID, @RequestParam("status") String status) {
        try {
                this.orderLifeCycleService.updateStatus(Long.parseLong(stepID),Boolean.parseBoolean(status));
                return new ResponseEntity<>(Map.of("message","Status updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/updatePosition")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> updatePosition(@RequestParam("oldPosition") String oldPosition, @RequestParam("newPosition") String newPosition) {
        try {
            this.orderLifeCycleService.updatePosition(Integer.parseInt(oldPosition),Integer.parseInt(newPosition));
            return new ResponseEntity<>(Map.of("message","Position updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> addStep(@RequestParam("stepName") String stepName, @RequestParam("action") String action, @RequestParam(name = "logo", required = false) MultipartFile logo) {
        try {
                System.out.println("IS STEP exist ? = "+this.orderLifeCycleService.isExist(stepName));
                if (this.orderLifeCycleService.isExist(stepName)){
                    return new ResponseEntity<>(Map.of("message","Step already exist."), HttpStatus.OK);
                }else{
                    this.orderLifeCycleService.addStep(stepName,action,logo);
                    return new ResponseEntity<>(Map.of("message","Step added success."), HttpStatus.OK);
                }

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/delete/{stepID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> DeleteStep(@PathVariable String stepID) {
        try {
            this.orderLifeCycleService.deleteStep(Long.parseLong(stepID));
            return new ResponseEntity<>(Map.of("message","Step deleted successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/update")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> updateStep(@RequestParam("stepID") String stepID, @RequestParam("stepName") String stepName, @RequestParam("action") String action, @RequestParam(name = "logo", required = false) MultipartFile logo) {
        try {
                this.orderLifeCycleService.updateStep(Long.parseLong(stepID),stepName,action,logo);
                return new ResponseEntity<>(Map.of("message","Step updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }
}
