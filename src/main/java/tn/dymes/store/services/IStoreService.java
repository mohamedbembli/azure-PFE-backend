package tn.dymes.store.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IStoreService {

   void addStoreData(String storeName,String tvaCode, String phone1, String phone2, String state, String city, String address, String zipCode, String pays, String email);
   void addBankData(String bnkName,String designation, String agence, String swift, String iban);

   void addPayMethodesData(boolean payInDelivery, boolean payWithBank, String paymentByDefault);

   void addSEO(String seoDescription, String seoStoreUrl, String seoTitle);
   void updateShippingFees(Float shippingFees, Float totalCMD);
   void addSocialMediaData(String facebookLink, String instagramLink, String linkedinLink, String whatsappLink, String snapchatLink, String tiktokLink, String youtubeLink);

   void addLogos(List<MultipartFile> imagesList) throws IOException;

   MultipartFile processImages(List<MultipartFile> imagesList, String keyword);

   Optional<MultipartFile> findImageByName(List<MultipartFile> imagesList, String keyword);

   String uploadStoreImage(MultipartFile file) throws IOException;

   void deleteImage(String fileName) throws  IOException;

   byte[] getPublicPhoto(String type) throws IOException;
}
