package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.entites.Store;
import tn.dymes.store.repositories.StoreRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class StoreServiceImpl implements IStoreService {

    @Autowired
    StoreRepository storeRepository;


    @Override
    public byte[] getPublicPhoto(String type) throws IOException {
        Store store = storeRepository.findById("Dymes").orElse(null);
        String profilePath = "src/main/resources/static/uploads/store/images/";
        Path path = null;
        if (type.equals("logo")){
            path = Paths.get(profilePath,store.getLogo());
        }
        if (type.equals("icon")){
            path = Paths.get(profilePath,store.getIcon());
        }

        return Files.readAllBytes(path);
    }


    @Override
    public void addLogos(List<MultipartFile> imagesList) throws IOException {
        Store store = storeRepository.findById("Dymes").orElse(null);
        if (store != null){
            if (imagesList != null){
                MultipartFile logoImage = processImages(imagesList, "logo");
                MultipartFile iconImage = processImages(imagesList, "icon");
                // logo
                if (logoImage != null){
                    if (store.getLogo() != null){
                        // remove previous logo
                        deleteImage(store.getLogo());
                        String fileName = uploadStoreImage(logoImage);
                        store.setLogo(fileName);
                    }
                    else{
                        String fileName = uploadStoreImage(logoImage);
                        store.setLogo(fileName);
                    }
                    storeRepository.save(store);
                }
                // icon
                if (iconImage != null){
                    if (store.getIcon() != null){
                        deleteImage(store.getIcon());
                        String fileName = uploadStoreImage(iconImage);
                        store.setIcon(fileName);
                    }
                    else{
                        String fileName = uploadStoreImage(iconImage);
                        store.setIcon(fileName);
                    }
                    storeRepository.save(store);
                }
            }
        }
    }

    @Override
    public String uploadStoreImage(MultipartFile file) throws IOException {
        // Define the uploads directory within the static resources
        String uploadsDirPath = "src/main/resources/static/uploads/store/images/";

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

    @Override
    public void deleteImage(String fileName) throws IOException {
        String profilePath = "src/main/resources/static/uploads/store/images/";
        String fileNameToDelete = fileName; // Specify the file name you want to delete
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

    public String generateUniqueFileName(String originalFileName) {
        // Generate unique UUID
        String uuid = UUID.randomUUID().toString();

        // Extract the file extension from the original filename
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // Combine UUID and file extension to create a unique filename
        return "unique_" + uuid + fileExtension;
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
    public void addStoreData(String storeName, String tvaCode, String phone1, String phone2, String state, String city, String address, String zipCode, String pays, String email) {
        Store store = storeRepository.findById("Dymes").orElse(null);
        if (store != null){
            //storeName
            if (storeName.equals("null")){
                store.setName(null);
            }
            else{
                store.setName(storeName);
            }
            //storeName
            if (tvaCode.equals("null")){
                store.setTvaCode(tvaCode);
            }
            else{
                store.setTvaCode(tvaCode);
            }
            //phone1
            if (phone1.equals("null")){
                store.setPhone1(null);
            }
            else{
                store.setPhone1(phone1);
            }
            //phone2
            if (phone2.equals("null")){
                store.setPhone2(null);
            }
            else{
                store.setPhone2(phone2);
            }
            //state
            if (state.equals("null")){
                store.setState(null);
            }
            else{
                store.setState(state);
            }
            //City
            if (city.equals("null")){
                store.setCity(null);
            }
            else{
                store.setCity(city);
            }
            //address
            if (address.equals("null")){
                store.setAddress(null);
            }
            else{
                store.setAddress(address);
            }
            //zipCode
            if (address.equals("null")){
                store.setZipCode(null);
            }
            else{
                store.setZipCode(zipCode);
            }
            //pays
            if (pays.equals("null")){
                store.setPays(null);
            }
            else{
                store.setPays(pays);
            }
            //email
            if (email.equals("null")){
                store.setEmail(null);
            }
            else{
                store.setEmail(email);
            }
            storeRepository.save(store);
        }
    }

    @Override
    public void addBankData(String bnkName, String designation, String agence, String swift, String iban) {
        Store store = storeRepository.findById("Dymes").orElse(null);
        if (store != null) {
            //bnkName
            if (bnkName.equals("null")){
                store.setBankName(null);
            }
            else{
                store.setBankName(bnkName);
            }
            //designation
            if (designation.equals("null")){
                store.setDesignation(null);
            }
            else{
                store.setDesignation(designation);
            }
            //agence
            if (agence.equals("null")){
                store.setAgence(null);
            }
            else{
                store.setAgence(agence);
            }
            //swift
            if (swift.equals("null")){
                store.setSwift(null);
            }
            else{
                store.setSwift(swift);
            }
            //iban
            if (iban.equals("null")){
                store.setIban(null);
            }
            else{
                store.setIban(iban);
            }
            storeRepository.save(store);
        }
    }

    @Override
    public void addPayMethodesData(boolean payInDelivery, boolean payWithBank, String paymentByDefault) {
        Store store = storeRepository.findById("Dymes").orElse(null);
        if (store != null){
            store.setPayInDelivery(payInDelivery);
            store.setPayWithBank(payWithBank);
            if (paymentByDefault.equals("null")){
                store.setPaymentByDefault(null);
            }
            else{
                store.setPaymentByDefault(paymentByDefault);
            }
            storeRepository.save(store);
        }
    }

    @Override
    public void addSEO(String seoDescription, String seoStoreUrl, String seoTitle) {
        Store store = storeRepository.findById("Dymes").orElse(null);
        if (store != null){
            //description
            if (seoDescription.equals("null")){
                store.setSeoDescription(null);
            }
            else{
                store.setSeoDescription(seoDescription);
            }
            //store url
            if (seoStoreUrl.equals("null")){
                store.setSeoStoreUrl(null);
            }
            else{
                store.setSeoStoreUrl(seoStoreUrl);
            }
            //title
            if (seoTitle.equals("null")){
                store.setSeoTitle(null);
            }
            else{
                store.setSeoTitle(seoTitle);
            }
            storeRepository.save(store);
        }
    }

    @Override
    public void updateShippingFees(Float shippingFees, Float totalCMD) {
        Store store = storeRepository.findById("Dymes").orElse(null);
        if (store != null){
            if (shippingFees == null){
                store.setShippingFees(null);
            }
            else{
                store.setShippingFees(shippingFees);
            }
            if (totalCMD == null){
                store.setGlobalDiscountShipping(totalCMD);
            }
            else{
                store.setGlobalDiscountShipping(totalCMD);
            }

            storeRepository.save(store);
        }
    }

    @Override
    public void addSocialMediaData(String facebookLink, String instagramLink, String linkedinLink, String whatsappLink, String snapchatLink, String tiktokLink, String youtubeLink) {
        Store store = storeRepository.findById("Dymes").orElse(null);
        if (store != null) {
            //facebookLink
            if (facebookLink.equals("null")){
                store.setFacebookLink(null);
            }
            else{
                store.setFacebookLink(facebookLink);
            }
            //instagramLink
            if (instagramLink.equals("null")){
                store.setInstagramLink(null);
            }
            else{
                store.setInstagramLink(instagramLink);
            }
            //linkedinLink
            if (linkedinLink.equals("null")){
                store.setLinkedinLink(null);
            }
            else{
                store.setLinkedinLink(linkedinLink);
            }
            //whatsappLink
            if (whatsappLink.equals("null")){
                store.setWhatsappLink(null);
            }
            else{
                store.setWhatsappLink(whatsappLink);
            }
            //snapchatLink
            if (snapchatLink.equals("null")){
                store.setSnapchatLink(null);
            }
            else{
                store.setSnapchatLink(snapchatLink);
            }
            //tiktokLink
            if (tiktokLink.equals("null")){
                store.setTiktokLink(null);
            }
            else{
                store.setTiktokLink(tiktokLink);
            }
            //youtubeLink
            if (youtubeLink.equals("null")){
                store.setYoutubeLink(null);
            }
            else{
                store.setYoutubeLink(youtubeLink);
            }
            storeRepository.save(store);
        }
    }


}
