package tn.dymes.store.services;

import tn.dymes.store.entites.Promotion;

public interface IPromotionService {
    void addPromo(String codePromo, String typePromo, String startDate, String endDate, String discountType, Float discountValue);
    void updatePromo(long idPromo, String codePromo, String typePromo, String startDate, String endDate, String discountType, Float discountValue);
    boolean isPromoExist(String codePromo);

    void changeStatus(long promoID, String status);

    Promotion findPromotionByCode(String code);
}
