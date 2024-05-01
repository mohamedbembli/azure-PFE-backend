package tn.dymes.store.services;

import tn.dymes.store.entites.Rating;

import java.util.List;

public interface IRatingService {
    void addRating(String fullname,String emailOrPhone, String comment, int stars, long productID);
    void acceptRating(long ratingID);
    void deleteRating(long ratingID);

    List<Rating> getRatingListByProductID(long productID);
}
