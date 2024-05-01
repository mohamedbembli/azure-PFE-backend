package tn.dymes.store.services;

import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.dtos.AddProductDTO;
import tn.dymes.store.entites.Product;

import java.io.IOException;
import java.util.List;

public interface IProductService {

    Product getProduct(long id);
    void addProduct(AddProductDTO addProductDTO, List<MultipartFile> images,List<MultipartFile> variantImages) throws IOException;
    List<Product> getAll();

    Product updateProduct(long productID, AddProductDTO addProductDTO, List<MultipartFile> images,List<MultipartFile> variantImages) throws IOException;

    byte[] getPublicPhoto(long productID) throws IOException;
    int getVariantsStock(long productID);

}
