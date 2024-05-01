package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.ProductImage;
import tn.dymes.store.entites.Variant;
import tn.dymes.store.repositories.ProductImageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class ProductImageServiceImpl implements IProductImageService{
    @Autowired
    ProductImageRepository productImageRepository;

    @Override
    public byte[] getPublicPhoto(long productImageId) throws IOException {
        ProductImage productImage = productImageRepository.findById(productImageId).orElse(null);
        String variantPath = "src/main/resources/static/uploads/products";
        Path path;

        if (productImage != null) {
            path = Paths.get(variantPath, productImage.getImageName());
            return Files.readAllBytes(path);
        } else {
            throw new IllegalStateException("No variant image found!");
        }

    }

    public List<ProductImage> getSortedProductImages() {
        return productImageRepository.findAllByOrderByPositionAsc();
    }

    @Override
    public void deleteProductImagesByProductId(long productId) {
        productImageRepository.deleteByProductId(productId);
    }
}
