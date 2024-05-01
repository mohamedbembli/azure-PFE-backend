package tn.dymes.store.dtos;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record VariantDTO(
        List<String> attributes,
        Integer stock,
        Float price,
        boolean defaultVariant,
        String image,
        boolean actif
) {}