package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.Element;
import tn.dymes.store.entites.Product;
import tn.dymes.store.entites.Rating;
import tn.dymes.store.repositories.ProductRepository;
import tn.dymes.store.repositories.RatingRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RatingServiceImpl implements IRatingService{

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public void addRating(String fullname, String emailOrPhone, String comment, int stars, long productID) {
        Rating rating = new Rating();
        rating.setFullname(fullname);
        rating.setMailOrPhone(emailOrPhone);
        rating.setComment(comment);
        rating.setStars(stars);
        rating.setStatus("PENDING");
        // Create a Date object
        Date currentDate = new Date();
        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
        // Format the Date object to a string
        String formattedDate = dateFormat.format(currentDate);
        rating.setDate(formattedDate);
        Product product = this.productRepository.findById(productID).orElse(null);
        if (product != null){
            rating.setProduct(product);
        }
        ratingRepository.save(rating);
    }

    @Override
    public void acceptRating(long ratingID) {
        Rating rating = this.ratingRepository.findById(ratingID).orElse(null);
        if (rating != null){
            rating.setStatus("ACCEPTED");
            ratingRepository.save(rating);
        }
    }

    @Override
    public void deleteRating(long ratingID) {
        Rating rating = this.ratingRepository.findById(ratingID).orElse(null);
        if (rating != null){
            ratingRepository.deleteById(ratingID);
        }
    }

    @Override
    public List<Rating> getRatingListByProductID(long productID) {
        List<Rating> ratings = (List<Rating>) ratingRepository.findAll();
        List<Rating> filteredRatings = new ArrayList<>();

        for (Rating rating: ratings) {
            if (rating.getProduct().getId() == productID) {
                filteredRatings.add(rating);
            }
        }

        return filteredRatings;
    }
}
