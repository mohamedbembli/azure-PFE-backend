package tn.dymes.store.entites;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Store implements Serializable {
    @Id
    String entreprise;
    //Coordonnées de la boutique
    @Column(nullable = true)
    String name;
    @Column(nullable = true)
    String tvaCode;
    @Column(nullable = true)
    String phone1;
    @Column(nullable = true)
    String phone2;
    @Column(nullable = true)
    String state;
    @Column(nullable = true)
    String city;
    @Column(nullable = true)
    String address;
    @Column(nullable = true)
    String zipCode;
    @Column(nullable = true)
    String pays;
    @Column(nullable = true)
    String email;
    //Coordonnées bancaires
    @Column(nullable = true)
    String bankName;
    @Column(nullable = true)
    String designation;
    @Column(nullable = true)
    String agence;
    @Column(nullable = true)
    String swift;
    @Column(nullable = true)
    String iban;
    // logos
    @Column(nullable = true)
    String logo;
    @Column(nullable = true)
    String icon;
    //Méthode de paiement
    @Column(nullable = true)
    boolean payWithBank;
    @Column(nullable = true)
    boolean payInDelivery;
    @Column(nullable = true)
    String paymentByDefault;
    //Réseaux sociaux
    String facebookLink;
    String instagramLink;
    String linkedinLink;
    String whatsappLink;
    String snapchatLink;
    String tiktokLink;
    String youtubeLink;
    //SEO Ref
    String seoDescription;
    String seoStoreUrl;
    String seoTitle;
    //Shipping fees
    Float shippingFees;
    Float globalDiscountShipping;



}
