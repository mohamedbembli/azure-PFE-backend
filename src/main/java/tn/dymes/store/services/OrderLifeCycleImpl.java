package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.entites.OrderLifeCycle;
import tn.dymes.store.repositories.OrderLifeCycleRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderLifeCycleImpl implements IOrderLifeCycleService {

    @Autowired
    OrderLifeCycleRepository orderLifeCycleRepository;


    @Override
    public byte[] getPublicPhoto(long idStep) throws IOException {
        OrderLifeCycle orderLifeCycle = orderLifeCycleRepository.findById(idStep).orElse(null);
        String profilePath = "src/main/resources/static/uploads/OLC/";
        Path path = Paths.get(profilePath,orderLifeCycle.getLogo());
        return Files.readAllBytes(path);
    }

    @Override
    public void updateStatus(long stepID, boolean status) {
        OrderLifeCycle step = orderLifeCycleRepository.findById(stepID).orElse(null);
        if (step != null){
            step.setStatus(status);
            orderLifeCycleRepository.save(step);
        }
    }


    @Override
    public void updateStep(long stepID, String stepName, String action, MultipartFile logo) throws IOException {
        OrderLifeCycle orderLifeCycle = orderLifeCycleRepository.findById(stepID).orElse(null);
        if (orderLifeCycle != null){
            orderLifeCycle.setStepName(stepName);
            if (action.equals("null")){
                orderLifeCycle.setAction(null);
            }
            else{
                orderLifeCycle.setAction(action);
            }
            if (logo != null){
                if (orderLifeCycle.getLogo() != null){
                    // delete previous logo
                    try {
                        String profilePath = "src/main/resources/static/uploads/OLC/";
                        String fileNameToDelete = orderLifeCycle.getLogo(); // Specify the file name you want to delete
                        Path filePath = Paths.get(profilePath, fileNameToDelete);
                        // Check if the file exists before attempting to delete it
                        if (Files.exists(filePath)) {
                            Files.delete(filePath);
                            System.out.println("File deleted successfully: " + fileNameToDelete);
                        } else {
                            System.out.println("File not found: " + fileNameToDelete);
                        }
                    } catch (IOException e) {
                        System.err.println("Error deleting the file: " + e.getMessage());
                    }
                }
                String fileName = uploadStepLogo(logo);
                orderLifeCycle.setLogo(fileName);
            }
            else{
                if (orderLifeCycle.getLogo() == null){
                    orderLifeCycle.setLogo(null);
                }
            }
            orderLifeCycleRepository.save(orderLifeCycle);
        }
    }
    @Override
    public void addStep(String stepName, String action, MultipartFile logo) throws IOException {
        OrderLifeCycle orderLifeCycle = new OrderLifeCycle();
        orderLifeCycle.setStepName(stepName);
        if (action.equals("null")){
            orderLifeCycle.setAction(null);
        }
        else{
            orderLifeCycle.setAction(action);
        }
        orderLifeCycle.setStatus(true);
        if (this.findAll().size() > 0){
            orderLifeCycle.setPosition(this.findAll().size()+1);
        }
        else{
            orderLifeCycle.setPosition(1);
        }
        if (logo != null){
            String fileName = uploadStepLogo(logo);
            orderLifeCycle.setLogo(fileName);
        }
        else{
            orderLifeCycle.setLogo(null);
        }
        orderLifeCycleRepository.save(orderLifeCycle);
    }

    @Override
    public void deleteStep(long stepID) {
        OrderLifeCycle orderLifeCycle = orderLifeCycleRepository.findById(stepID).orElse(null);
        List<OrderLifeCycle> orderLifeCycleList = this.findAll();
        if(orderLifeCycle != null){
           // position - 1 pour tous elements
            for (OrderLifeCycle olc: orderLifeCycleList ){
                olc.setPosition(olc.getPosition()-1);
                orderLifeCycleRepository.save(olc);
            }

            // check if there are a logo
            if (orderLifeCycle.getLogo() != null){
                try {
                    String profilePath = "src/main/resources/static/uploads/OLC/";
                    String fileNameToDelete = orderLifeCycle.getLogo(); // Specify the file name you want to delete
                    Path filePath = Paths.get(profilePath, fileNameToDelete);
                    // Check if the file exists before attempting to delete it
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                        System.out.println("File deleted successfully: " + fileNameToDelete);
                    } else {
                        System.out.println("File not found: " + fileNameToDelete);
                    }
                } catch (IOException e) {
                    System.err.println("Error deleting the file: " + e.getMessage());
                }
            }
            orderLifeCycleRepository.deleteById(stepID);
        }
    }

    @Override
    public void updatePosition(int oldPosition, int newPosition) {
        OrderLifeCycle orderLifeCycleOLD = orderLifeCycleRepository.findByPosition(oldPosition);
        OrderLifeCycle orderLifeCycleNEW = orderLifeCycleRepository.findByPosition(newPosition);

        if (orderLifeCycleOLD != null && orderLifeCycleNEW != null) {
            List<OrderLifeCycle> orderLifeCycleList = this.findAll();

            // Calculate new positions for relevant steps
            for (OrderLifeCycle olc : orderLifeCycleList) {
                if (olc.getPosition() == oldPosition) {
                    olc.setPosition(newPosition);
                } else if (oldPosition < newPosition && olc.getPosition() > oldPosition && olc.getPosition() <= newPosition) {
                    olc.setPosition(olc.getPosition() - 1);
                } else if (oldPosition > newPosition && olc.getPosition() >= newPosition && olc.getPosition() < oldPosition) {
                    olc.setPosition(olc.getPosition() + 1);
                }
                orderLifeCycleRepository.save(olc);
            }
        }
    }


    @Override
    public String uploadStepLogo(MultipartFile file) throws IOException {
        // Define the uploads directory within the static resources
        String uploadsDirPath = "src/main/resources/static/uploads/OLC/";

        // Create the uploads directory if it doesn't exist
        File directory = new File(uploadsDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the uploaded file to the uploads directory
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = generateUniqueFileName(originalFileName);
        Path filePath = Paths.get(uploadsDirPath, uniqueFileName);
        Files.write(filePath, file.getBytes());

        return uniqueFileName;

    }

    public String generateUniqueFileName(String originalFileName) {
        // Generate unique UUID
        String uuid = UUID.randomUUID().toString();

        // Extract the file extension from the original filename
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // Combine UUID and file extension to create a unique filename
        return "unique_" + uuid + fileExtension;
    }

    @Override
    public boolean isExist(String stepName) {
        List<OrderLifeCycle> orderLifeCycles = (List<OrderLifeCycle>) orderLifeCycleRepository.findAll();
        if (orderLifeCycles.size() > 0){
            for (OrderLifeCycle step: orderLifeCycles){
                if ((step.getStepName().toLowerCase()).equals(stepName.toLowerCase()))
                    return true;
            }
        }
        return false;
    }

    @Override
    public List<OrderLifeCycle> findAll() {
        List<OrderLifeCycle> orderLifeCycles = (List<OrderLifeCycle>) orderLifeCycleRepository.findAllByOrderByPositionAsc();
        return orderLifeCycles;
    }

    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
