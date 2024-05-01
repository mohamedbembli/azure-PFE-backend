package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.Claim;
import tn.dymes.store.entites.MyOrder;
import tn.dymes.store.repositories.ClaimRepository;
import tn.dymes.store.repositories.MyOrderRepository;

@Service
@Slf4j
public class ClaimServiceImpl implements IClaimService{

    @Autowired
    ClaimRepository claimRepository;

    @Autowired
    MyOrderRepository myOrderRepository;

    @Override
    public void addClaim(String subject, String comment, long orderID) {
        MyOrder myOrder = myOrderRepository.findById(orderID).orElse(null);
        if (myOrder != null){
            Claim claim = new Claim();
            claim.setSubject(subject);
            claim.setComment(comment);
            claim.setStatus("Non traitée");
            claim.setMyOrder(myOrder);
            claimRepository.save(claim);
            myOrder.getClaims().add(claim);
            myOrderRepository.save(myOrder);
        }
    }

    @Override
    public void changeClaimStatus(long claimID) {
        Claim claim = claimRepository.findById(claimID).orElse(null);
        if (claim != null){
            claim.setStatus("Traitée");
            claimRepository.save(claim);
        }
    }

}
