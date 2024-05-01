package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.CheckoutField;
import tn.dymes.store.entites.OrderLifeCycle;
import tn.dymes.store.repositories.CheckoutFieldRepository;

import java.util.List;


@Service
@Slf4j
public class CheckoutFieldServiceImpl implements ICheckoutFieldService{

    @Autowired
    CheckoutFieldRepository checkoutFieldRepository;

    @Override
    public void addCheckoutField(String name, boolean shown, boolean isRequired) {
        CheckoutField checkoutField = new CheckoutField();
        checkoutField.setName(name);
        checkoutField.setShown(shown);
        checkoutField.setRequired(isRequired);
        checkoutFieldRepository.save(checkoutField);
    }

    @Override
    public List<CheckoutField> getAll() {
        List<CheckoutField> checkoutFields = (List<CheckoutField>) checkoutFieldRepository.findAll();
        return checkoutFields;
    }

    @Override
    public void updateShown(long fieldID) {
        CheckoutField checkoutField = checkoutFieldRepository.findById(fieldID).orElse(null);
        checkoutField.setShown(!checkoutField.isShown());
        checkoutFieldRepository.save(checkoutField);
    }

    @Override
    public void updateIsRequired(long fieldID) {
        CheckoutField checkoutField = checkoutFieldRepository.findById(fieldID).orElse(null);
        checkoutField.setRequired(!checkoutField.isRequired());
        checkoutFieldRepository.save(checkoutField);
    }

    @Override
    public void updateOrderConfirmationMsg(String msg) {
        CheckoutField checkoutField = checkoutFieldRepository.findById((long)1).orElse(null);
        if (msg != null && msg.length() > 0)
             checkoutField.setOrderConfirmationMsg(msg);
        else
            checkoutField.setOrderConfirmationMsg(null);
        checkoutFieldRepository.save(checkoutField);
    }
}
