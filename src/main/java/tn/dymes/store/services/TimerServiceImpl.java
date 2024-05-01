package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.Product;
import tn.dymes.store.entites.Timer;
import tn.dymes.store.repositories.ProductRepository;
import tn.dymes.store.repositories.TimerRepository;

@Service
@Slf4j
public class TimerServiceImpl implements ITimerService{

    @Autowired
    TimerRepository timerRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public void addTimer(long idProduct, float nbHours) {
        Product product = productRepository.findById(idProduct).orElse(null);
        if (product != null){
            Timer timer = new Timer();
            timer.setNbHours(nbHours);
            timer.setProduct(product);
            timerRepository.save(timer);
        }

    }

    @Override
    public void deleteTimer(long idTimer) {
        timerRepository.deleteById(idTimer);
    }




}
