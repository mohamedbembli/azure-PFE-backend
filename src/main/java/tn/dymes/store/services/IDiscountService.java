package tn.dymes.store.services;

import tn.dymes.store.dtos.DiscountDTO;

import java.util.List;

public interface IDiscountService {
    void addDiscounts(List<DiscountDTO> discountTable, long idProduct);
}
