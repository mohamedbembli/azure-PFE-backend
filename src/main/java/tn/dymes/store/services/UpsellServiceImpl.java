package tn.dymes.store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.dtos.AddUpSellDTO;
import tn.dymes.store.entites.Product;
import tn.dymes.store.entites.Upsell;
import tn.dymes.store.repositories.ProductRepository;
import tn.dymes.store.repositories.UpsellRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UpsellServiceImpl implements IUpsellService{

    @Autowired
    UpsellRepository upsellRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public void addUpsell(AddUpSellDTO addUpSellDTO, List<MultipartFile> imagesList) throws IOException {
        Upsell upsell = new Upsell();
        upsell.setName(capitalizeFirstLetter(addUpSellDTO.upSellName().toLowerCase()));

        upsell.setHeaderType(addUpSellDTO.headerType());
        // check if headerType is image
        if (addUpSellDTO.headerType().equals("image")){
            MultipartFile headerImage = processImages(imagesList, "header");
            if (headerImage != null){
                // add and upload image
                String fileName = uploadUpSellImage(headerImage);
                upsell.setHeader(fileName);
            }
        }
        else{
            upsell.setHeader(addUpSellDTO.headerContent());
        }
        // end
        upsell.setBodyType(addUpSellDTO.bodyType());
        // check if bodyType is image
        if (addUpSellDTO.bodyType().equals("image")){
            MultipartFile bodyImage = processImages(imagesList, "body");
            if (bodyImage != null){
                String fileName = uploadUpSellImage(bodyImage);
                upsell.setBody(fileName);
            }
        }
        else{
            upsell.setBody(addUpSellDTO.bodyContent());
        }
        // end
        upsell.setFooterType(addUpSellDTO.footerType());
        // check if footerType is image
        if (addUpSellDTO.footerType().equals("image")){
            MultipartFile footerImage = processImages(imagesList, "footer");
            if (footerImage != null){
                String fileName = uploadUpSellImage(footerImage);
                upsell.setFooter(fileName);
            }
        }
        else{
            upsell.setFooter(addUpSellDTO.footerContent());
        }
        // end
        upsell.setButtonsPosition(addUpSellDTO.buttonsPosition());
        upsell.setBtnConfirmText(addUpSellDTO.btnConfirmText());
        upsell.setBtnConfirmTextColor(addUpSellDTO.btnConfirmTextColor());
        upsell.setBtnConfirmColor(addUpSellDTO.btnConfirmColor());
        upsell.setBtnConfirmSize(addUpSellDTO.btnConfirmSize());
        upsell.setBtnCancelText(addUpSellDTO.btnCancelText());
        upsell.setBtnCancelTextColor(addUpSellDTO.btnCancelTextColor());
        upsell.setBtnCancelColor(addUpSellDTO.btnCancelColor());
        upsell.setBtnCancelSize(addUpSellDTO.btnCancelSize());
        upsell.setCountYes(0);
        upsell.setCountNo(0);
        upsell.setStatus(true);
        upsell.setCreationDate(addUpSellDTO.creationDate());
        Product product = productRepository.findById(addUpSellDTO.productID()).orElse(null);
        if (product != null){
            upsell.setProduct(product);
            upsell.setUpsellProductID(addUpSellDTO.productID());
        }
        Product nextProduct = productRepository.findById(addUpSellDTO.nextProductID()).orElse(null);
        if (nextProduct != null){
            upsell.setNextProductID(addUpSellDTO.nextProductID());
        }
        upsellRepository.save(upsell);
    }

    @Override
    public void updateUpsell(long upsellID, AddUpSellDTO addUpSellDTO, List<MultipartFile> imagesList) throws IOException {
        Upsell upsell = upsellRepository.findById(upsellID).orElse(null);
        if (upsell != null){
            upsell.setName(capitalizeFirstLetter(addUpSellDTO.upSellName().toLowerCase()));

            // check if headerType is image
            if (addUpSellDTO.headerType().equals("image") && imagesList != null){
                MultipartFile headerImage = processImages(imagesList, "header");
                if (headerImage != null){
                    if (upsell.getHeaderType().equals("image")){
                        // delete previous image
                        String profilePath = "src/main/resources/static/uploads/upsells/";
                        String fileNameToDelete = upsell.getHeader(); // Specify the file name you want to delete
                        try {
                            Path filePath = Paths.get(profilePath, fileNameToDelete);
                            // Check if the file exists before attempting to delete it
                            if (Files.exists(filePath)) {
                                Files.delete(filePath);
                                System.out.println("File deleted successfully: " + fileNameToDelete);
                                // add and upload image
                                String fileName = uploadUpSellImage(headerImage);
                                upsell.setHeader(fileName);
                            } else {
                                System.out.println("File not found: " + fileNameToDelete);
                            }
                        } catch (IOException e) {
                            System.err.println("Error deleting the file: " + e.getMessage());
                        }
                    }
                    else {
                        // add and upload image
                        String fileName = uploadUpSellImage(headerImage);
                        upsell.setHeader(fileName);
                    }
                }
                }
            else{
                if (addUpSellDTO.headerContent() != null){
                    upsell.setHeader(addUpSellDTO.headerContent());
                }
            }
            upsell.setHeaderType(addUpSellDTO.headerType());
            // end
            // check if bodyType is image
            if (addUpSellDTO.bodyType().equals("image") && imagesList != null){
                MultipartFile bodyImage = processImages(imagesList, "body");
                if (bodyImage != null) {
                    if (upsell.getBodyType().equals("image")) {
                        // delete previous image
                        String profilePath = "src/main/resources/static/uploads/upsells/";
                        String fileNameToDelete = upsell.getBody(); // Specify the file name you want to delete
                        try {
                            Path filePath = Paths.get(profilePath, fileNameToDelete);
                            // Check if the file exists before attempting to delete it
                            if (Files.exists(filePath)) {
                                Files.delete(filePath);
                                System.out.println("File deleted successfully: " + fileNameToDelete);
                                // add and upload image
                                String fileName = uploadUpSellImage(bodyImage);
                                upsell.setBody(fileName);
                            } else {
                                System.out.println("File not found: " + fileNameToDelete);
                            }
                        } catch (IOException e) {
                            System.err.println("Error deleting the file: " + e.getMessage());
                        }
                    } else {
                        // add and upload image
                        String fileName = uploadUpSellImage(bodyImage);
                        upsell.setBody(fileName);
                    }
                }
            }
            else{
                if (addUpSellDTO.bodyContent() != null){
                    upsell.setBody(addUpSellDTO.bodyContent());
                }
            }
            upsell.setBodyType(addUpSellDTO.bodyType());
            // end
            // check if footerType is image
            if (addUpSellDTO.footerType().equals("image") && imagesList != null){
                MultipartFile footerImage = processImages(imagesList, "footer");
                if (footerImage != null) {
                    if (upsell.getFooterType().equals("image")) {
                        // delete previous image
                        String profilePath = "src/main/resources/static/uploads/upsells/";
                        String fileNameToDelete = upsell.getFooter(); // Specify the file name you want to delete
                        try {
                            Path filePath = Paths.get(profilePath, fileNameToDelete);
                            // Check if the file exists before attempting to delete it
                            if (Files.exists(filePath)) {
                                Files.delete(filePath);
                                System.out.println("File deleted successfully: " + fileNameToDelete);
                                // add and upload image
                                String fileName = uploadUpSellImage(footerImage);
                                upsell.setFooter(fileName);
                            } else {
                                System.out.println("File not found: " + fileNameToDelete);
                            }
                        } catch (IOException e) {
                            System.err.println("Error deleting the file: " + e.getMessage());
                        }
                    }
                else {
                    // add and upload image
                    String fileName = uploadUpSellImage(footerImage);
                    upsell.setFooter(fileName);
                }
                }
            }
            else{
                if (addUpSellDTO.footerContent() != null){
                    upsell.setFooter(addUpSellDTO.footerContent());
                }
            }
            upsell.setFooterType(addUpSellDTO.footerType());
            // end
            upsell.setButtonsPosition(addUpSellDTO.buttonsPosition());
            upsell.setBtnConfirmText(addUpSellDTO.btnConfirmText());
            upsell.setBtnConfirmTextColor(addUpSellDTO.btnConfirmTextColor());
            upsell.setBtnConfirmColor(addUpSellDTO.btnConfirmColor());
            upsell.setBtnConfirmSize(addUpSellDTO.btnConfirmSize());
            upsell.setBtnCancelText(addUpSellDTO.btnCancelText());
            upsell.setBtnCancelTextColor(addUpSellDTO.btnCancelTextColor());
            upsell.setBtnCancelColor(addUpSellDTO.btnCancelColor());
            upsell.setBtnCancelSize(addUpSellDTO.btnCancelSize());
            upsell.setCountYes(0);
            upsell.setCountNo(0);
            upsell.setStatus(true);
            if (upsell.getCreationDate() == null){
                upsell.setCreationDate(addUpSellDTO.creationDate());
            }
            Product product = productRepository.findById(addUpSellDTO.productID()).orElse(null);
            if (product != null){
                upsell.setProduct(product);
                upsell.setUpsellProductID(addUpSellDTO.productID());
            }
            Product nextProduct = productRepository.findById(addUpSellDTO.nextProductID()).orElse(null);
            if (nextProduct != null){
                upsell.setNextProductID(addUpSellDTO.nextProductID());
            }
            upsellRepository.save(upsell);
        }

    }

    public String uploadUpSellImage(MultipartFile file) throws IOException {
        // Define the uploads directory within the static resources
        String uploadsDirPath = "src/main/resources/static/uploads/upsells/";

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
    public boolean isExist(String name) {
        List<Upsell> upsells = upsellRepository.findAll();
        if (upsells != null){
            for (Upsell upsell: upsells){
                if (capitalizeFirstLetter(name.toLowerCase()).equals(upsell.getName()))
                    return true;
            }
        }
        return false;
    }

    @Override
    public MultipartFile processImages(List<MultipartFile> imagesList, String keyword) {
        Optional<MultipartFile> headerImage = findImageByName(imagesList, keyword);
        // Check if an image with the specified name was found
        if (headerImage.isPresent()) {
            MultipartFile foundImage = headerImage.get();
            return foundImage;
        } else {
           return null;
        }
    }

    @Override
    public Optional<MultipartFile> findImageByName(List<MultipartFile> imagesList, String keyword) {
        return imagesList.stream()
                .filter(image -> image.getOriginalFilename() != null && image.getOriginalFilename().contains(keyword))
                .findFirst();
    }

    @Override
    public void changeStatus(long upsellID, boolean status) {
        Upsell upsell = upsellRepository.findById(upsellID).orElse(null);
        if (upsell != null){
            upsell.setStatus(status);
            upsellRepository.save(upsell);
        }
    }

    @Override
    public byte[] getPublicPhoto(long upsellID, String position) throws IOException {
            Upsell upsell = upsellRepository.findById(upsellID).orElse(null);
            String profilePath = "src/main/resources/static/uploads/upsells";
            Path path = null;
           if (position.equals("header")){
                 path = Paths.get(profilePath,upsell.getHeader());
            }
            if (position.equals("body")){
                 path = Paths.get(profilePath,upsell.getBody());
            }
            if (position.equals("footer")){
                 path = Paths.get(profilePath,upsell.getFooter());
            }

        return Files.readAllBytes(path);
    }

    @Override
    public void deleteUpSell(long upsellID) {
        //
        Upsell upsell = upsellRepository.findById(upsellID).orElse(null);
        if (upsell != null){
            // header
            if (upsell.getHeaderType() != null && upsell.getHeaderType().equals("image")){
                String profilePath = "src/main/resources/static/uploads/upsells/";
                String fileNameToDelete = upsell.getHeader(); // Specify the file name you want to delete
                try {
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
            //body
            if (upsell.getBodyType() != null && upsell.getBodyType().equals("image")){
                String profilePath = "src/main/resources/static/uploads/upsells/";
                String fileNameToDelete = upsell.getBody(); // Specify the file name you want to delete
                try {
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
            //footer
            if (upsell.getFooterType() != null && upsell.getFooterType().equals("image")){
                String profilePath = "src/main/resources/static/uploads/upsells/";
                String fileNameToDelete = upsell.getFooter(); // Specify the file name you want to delete
                try {
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
            // delete upSell
            this.upsellRepository.deleteById(upsellID);
        }
    }

    @Override
    public void cancelUpsell(long upsellID) {
        Upsell upsell = upsellRepository.findById(upsellID).orElse(null);
        if (upsell != null){
            upsell.setCountNo(upsell.getCountNo()+1);
            upsellRepository.save(upsell);
        }
    }

    @Override
    public void approveUpsell(long upsellID) {
        Upsell upsell = upsellRepository.findById(upsellID).orElse(null);
        if (upsell != null){
            upsell.setCountYes(upsell.getCountYes()+1);
            upsellRepository.save(upsell);
        }
    }

    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
