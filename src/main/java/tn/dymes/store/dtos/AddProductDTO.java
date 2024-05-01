package tn.dymes.store.dtos;

import java.util.List;

public record AddProductDTO(
        String productName,
        String description,
        String productBarCode,
        String productRef,
        String productSupplier,
        String productStatus,
        Long productCategoryID,
        Float shippingPrice,
        Float buyPrice,
        Float sellPrice,
        Integer tax,
        String reductionType,
        Float reductionValue,
        List<DiscountDTO> discountTable,
        Integer stock,
        List<VariantDTO> variants,
        Integer visitorStartRange,
        Integer visitorsEndRange,
        Integer nbHoursTimer
) {}


