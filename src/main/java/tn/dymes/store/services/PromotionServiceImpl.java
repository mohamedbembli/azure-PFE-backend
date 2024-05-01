package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.Promotion;
import tn.dymes.store.repositories.PromotionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PromotionServiceImpl implements IPromotionService {
    @Autowired
    PromotionRepository promotionRepository;

    public Promotion findPromotionByCode(String code) {
        return promotionRepository.findByCode(code);
    }

    @Override
    public void addPromo(String codePromo, String typePromo, String startDate, String endDate, String discountType, Float discountValue) {
        Promotion promotion = new Promotion();
        promotion.setCode(codePromo.toUpperCase());
        promotion.setPromoType(typePromo);
        promotion.setDiscountType(discountType);
        promotion.setDiscountValue(discountValue);
        promotion.setStartDate(startDate);
        promotion.setExpiryDate(endDate);
        promotion.setStatus("actif");
        promotionRepository.save(promotion);

    }

    @Override
    public void updatePromo(long idPromo, String codePromo, String typePromo, String startDate, String endDate, String discountType, Float discountValue) {
        Promotion promotion = promotionRepository.findById(idPromo).orElse(null);
        if (promotion != null){
            promotion.setCode(codePromo.toUpperCase());
            promotion.setPromoType(typePromo);
            promotion.setDiscountType(discountType);
            promotion.setDiscountValue(discountValue);
            promotion.setStartDate(startDate);
            promotion.setExpiryDate(endDate);
            promotionRepository.save(promotion);
        }
    }

    @Override
    public boolean isPromoExist(String codePromo) {
        List<Promotion> promotionList = promotionRepository.findAll();
        for(Promotion promotion: promotionList){
            if (promotion.getCode().equals(codePromo.toUpperCase()))
                return true;
        }
        return false;
    }

    @Override
    public void changeStatus(long promoID, String status) {
        Promotion promotion = promotionRepository.findById(promoID).orElse(null);
        if (promotion != null){
            promotion.setStatus(status);
            promotionRepository.save(promotion);
        }
    }

}
