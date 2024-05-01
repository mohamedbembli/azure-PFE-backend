package tn.dymes.store.services;

import tn.dymes.store.entites.CheckoutField;

import java.util.List;

public interface ICheckoutFieldService {
    void addCheckoutField(String name, boolean shown, boolean isRequired);
    List<CheckoutField> getAll();

    void updateShown(long fieldID);
    void updateIsRequired(long fieldID);

    void updateOrderConfirmationMsg(String msg);
}
