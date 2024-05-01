package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.dtos.DiscountDTO;
import tn.dymes.store.entites.Discount;
import tn.dymes.store.entites.Product;
import tn.dymes.store.repositories.DiscountRepository;
import tn.dymes.store.repositories.ProductRepository;

import java.util.List;

@Service
@Slf4j
public class DiscountServiceImpl implements IDiscountService {

    @Autowired
    DiscountRepository discountRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public void addDiscounts(List<DiscountDTO> discountTable, long idProduct) {
    }
}
