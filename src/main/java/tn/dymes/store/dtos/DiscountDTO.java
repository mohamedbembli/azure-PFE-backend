package tn.dymes.store.dtos;

public record DiscountDTO(
        int qte,
        String type,
        float reduction,
        float finalPrice
) {}
