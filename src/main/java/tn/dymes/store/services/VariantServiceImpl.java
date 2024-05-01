package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.Product;
import tn.dymes.store.entites.Variant;
import tn.dymes.store.repositories.VariantRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class VariantServiceImpl implements IVariantService{

    @Autowired
    VariantRepository variantRepository;

    @Override
    public byte[] getPublicPhoto(long variantId) throws IOException {
        Variant variant = variantRepository.findById(variantId).orElse(null);
        String variantPath = "src/main/resources/static/uploads/products/variants";
        Path path;

        if (variant != null) {
            if (variant.getImage() != null) {
                path = Paths.get(variantPath, variant.getImage());
                return Files.readAllBytes(path);
            } else {
                throw new IllegalStateException("No variant image found!");
            }
        } else {
            throw new IllegalArgumentException("Variant not found with ID: " + variantId);
        }
    }
}
