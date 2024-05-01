package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.Product;
import tn.dymes.store.entites.VisitorsRange;
import tn.dymes.store.repositories.ProductRepository;
import tn.dymes.store.repositories.VisitorsRangeRepository;

@Service
@Slf4j
public class VisitorsRangeImpl implements IVisitorsRange{

    @Autowired
    ProductRepository productRepository;
    @Autowired
    VisitorsRangeRepository visitorsRangeRepository;

    @Override
    public void addVisitorsRange(int startRange, int endRange, long idProduct) {
        Product product = productRepository.findById(idProduct).orElse(null);
        if (product !=null){
            VisitorsRange visitorsRange = new VisitorsRange();
            visitorsRange.setRangeFrom(startRange);
            visitorsRange.setRangeTo(endRange);
            visitorsRange.setProduct(product);
            visitorsRangeRepository.save(visitorsRange);
        }
    }
}
