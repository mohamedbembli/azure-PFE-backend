package tn.dymes.store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.entites.ProductCategory;
import tn.dymes.store.repositories.ProductCategoryRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductCategoryServiceImpl implements IProductCategoryService{

    @Autowired
    private ProductCategoryRepository productCategoryRepository;


    @Override
    public ProductCategory addProductCategory(String name,String description, String parentName) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName(capitalizeFirstLetter(name.toLowerCase()));
        if (parentName.equals("null"))
            productCategory.setCategoryParent("null");
        else
            productCategory.setCategoryParent(capitalizeFirstLetter(parentName.toLowerCase()));
        productCategory.setDescription(capitalizeFirstLetter(description));
        productCategory.setStatus(true);
        productCategoryRepository.save(productCategory);
        return productCategory;
    }

    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Override
    public void changeStatus(Long pCategoryID, boolean status) {
        ProductCategory productCategory = productCategoryRepository.findById(pCategoryID).orElse(null);
        if (productCategory != null){
            productCategory.setStatus(status);
            productCategoryRepository.save(productCategory);
        }
    }

    @Override
    public ProductCategory updateProductCategory(Long pCategoryID, String name, String parentName, String description) {
        ProductCategory productCategory = productCategoryRepository.findById(pCategoryID).orElse(null);
        if (productCategory != null){
            productCategory.setName(capitalizeFirstLetter(name));
            if (parentName.equals("null"))
                 productCategory.setCategoryParent("null");
            else
                productCategory.setCategoryParent(capitalizeFirstLetter(parentName));
            productCategory.setDescription(capitalizeFirstLetter(description));
            productCategoryRepository.save(productCategory);
            return productCategory;
        }
        return null;
    }

    @Override
    public void deleteProductCategory(Long pCategoryID) {
        ProductCategory productCategory = productCategoryRepository.findById(pCategoryID).orElse(null);
        List<ProductCategory> productCategories = (List<ProductCategory>) productCategoryRepository.findAll();
        if (productCategory != null){
            System.out.println("delete productCategory found");
            if (productCategory.getCategoryParent().equals("null")){
                System.out.println("categorie parente en cours de suppression");
                if (productCategories.size() != 0){
                    for (ProductCategory pCategory: productCategories) {
                        if ( !pCategory.getCategoryParent().equals("null") && productCategory.getName().equals(pCategory.getCategoryParent())){
                            pCategory.setCategoryParent("null");
                            productCategoryRepository.save(pCategory);
                        }
                    }
                }
            }
            // delete product category cover
            String profilePath = "src/main/resources/static/uploads/productCategory/";
            String fileNameToDelete = productCategory.getImage(); // Specify the file name you want to delete
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
            productCategoryRepository.deleteById(pCategoryID);
        }
    }


    public String generateUniqueFileName(String originalFileName) {
        // generate unique string
        return "unique_" + System.currentTimeMillis() + "_" + originalFileName;
    }

    @Override
    public void updateCoverPhoto(MultipartFile file, Long pCategoryID) throws IOException {
        // Define the uploads directory within the static resources
        String uploadsDirPath = "src/main/resources/static/uploads/productCategory/";

        // Create the uploads directory if it doesn't exist
        File directory = new File(uploadsDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the uploaded file to the uploads directory
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = Paths.get(uploadsDirPath, fileName);
        Files.write(filePath, file.getBytes());

        // Update profile photo URL in the database
        ProductCategory productCategory = productCategoryRepository.findById(pCategoryID).orElse(null);
        if (productCategory != null) {
            String filename = fileName;
            System.out.println(filename);
            productCategory.setImage(filename);
            productCategoryRepository.save(productCategory);
        }
    }

    @Override
    public byte[] getPublicPhoto(Long pCategoryID) throws IOException {
        ProductCategory productCategory = productCategoryRepository.findById(pCategoryID).orElse(null);
        String profilePath = "src/main/resources/static/uploads/productCategory/";
        Path path=Paths.get(profilePath,productCategory.getImage());
        return Files.readAllBytes(path);
    }

    @Override
    public ProductCategory isProductCategoryExist(String name, String parentName) {
        String formatedName = capitalizeFirstLetter(name.toLowerCase());
        String formatedParentName = capitalizeFirstLetter(parentName.toLowerCase());
        List<ProductCategory> productCategories = (List<ProductCategory>) productCategoryRepository.findAll();
        if (productCategories.size() != 0){
            for (ProductCategory productCategory: productCategories){
                if  ((formatedParentName!= null && productCategory.getCategoryParent().equals(formatedParentName)) && (productCategory.getName().equals(formatedName))){
                    System.out.println("PRODUCT CATEGORY FOUND");
                    return productCategory;
                }
                else if  ((formatedParentName == null && productCategory.getCategoryParent() == null) && (productCategory.getName().equals(formatedName))){
                    System.out.println("PRODUCT CATEGORY FOUND");
                    return productCategory;
                }
            }
        }
        System.out.println("PRODUCT CATEGORY NOT FOUND");
        return null;
    }

    @Override
    public List<ProductCategory> retrieveAllProductCategory() {
        List<ProductCategory> productCategories = (List<ProductCategory>) productCategoryRepository.findAll();
        return  productCategories;
    }
}
