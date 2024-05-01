package tn.dymes.store.services;


import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.entites.ProductCategory;
import tn.dymes.store.entites.Role;

import java.io.IOException;
import java.util.List;

public interface IProductCategoryService {
    ProductCategory addProductCategory(String name,String description, String parentName);

    void changeStatus(Long pCategoryID, boolean status);

    ProductCategory updateProductCategory(Long pCategoryID, String name, String parentName, String description);

    void deleteProductCategory(Long pCategoryID);

    byte[] getPublicPhoto(Long pCategoryID) throws IOException;

    void updateCoverPhoto(MultipartFile file, Long id) throws IOException;

    ProductCategory isProductCategoryExist(String name, String parentName);

    List<ProductCategory> retrieveAllProductCategory();



}
