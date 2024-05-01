package tn.dymes.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.dtos.AddUpSellDTO;
import tn.dymes.store.entites.Upsell;
import tn.dymes.store.repositories.UpsellRepository;
import tn.dymes.store.services.IUpsellService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upsell")
@CrossOrigin("*")
public class UpSellController {
    @Autowired
    UpsellRepository upsellRepository;

    @Autowired
    IUpsellService upsellService;

    @PostMapping(path = "/changeStatus")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, String>> changeStatusUpsell(@RequestParam("upsellID") String upsellID, @RequestParam("status") boolean status) {
        try {
            this.upsellService.changeStatus(Long.parseLong(upsellID),status);
            return new ResponseEntity<>(Map.of("message","UpSell status updated successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/delete/{upsellID}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> DeleteUpsell(@PathVariable String upsellID) {
        try {
            this.upsellService.deleteUpSell(Long.parseLong(upsellID));
            return new ResponseEntity<>(Map.of("message","UpSell deleted successfully"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> addUpsell(@RequestParam("dataToSend") String dataToSend, @RequestParam(name = "imagesList", required = false) List<MultipartFile> imagesList) {
        try {
            System.out.println("imagesList ="+imagesList);
            System.out.println("dataToSend ="+dataToSend);

            ObjectMapper objectMapper = new ObjectMapper();
            AddUpSellDTO addUpSellDTO = objectMapper.readValue(dataToSend, AddUpSellDTO.class);

            if (upsellService.isExist(addUpSellDTO.upSellName())){
                return new ResponseEntity<>(Map.of("message","UpSell already exist."), HttpStatus.OK);
            }
            this.upsellService.addUpsell(addUpSellDTO,imagesList);
            return new ResponseEntity<>(Map.of("message","UpSell added success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/update")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<Map<String, String>> updateUpsell(@RequestParam("dataToSend") String dataToSend, @RequestParam(name = "imagesList", required = false) List<MultipartFile> imagesList) {
        try {
            System.out.println("imagesList ="+imagesList);
            System.out.println("dataToSend ="+dataToSend);

            ObjectMapper objectMapper = new ObjectMapper();
            AddUpSellDTO addUpSellDTO = objectMapper.readValue(dataToSend, AddUpSellDTO.class);

            this.upsellService.updateUpsell(Long.parseLong(addUpSellDTO.upsellID()),addUpSellDTO,imagesList);
            return new ResponseEntity<>(Map.of("message","UpSell updated success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/getAll")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || hasAuthority('SCOPE_EMPLOYEE') ")
    public ResponseEntity<List<Upsell>> getAllUpsells() {
        try {
            List<Upsell> upsells = this.upsellRepository.findAll();
            System.out.println("Number of upsells found: " + upsells.size());
            if (upsells.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(upsells, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
