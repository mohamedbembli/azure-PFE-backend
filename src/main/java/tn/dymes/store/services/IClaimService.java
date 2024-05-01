package tn.dymes.store.services;

public interface IClaimService {

    void addClaim(String subject, String comment, long orderID);
    void changeClaimStatus(long claimID);
}
