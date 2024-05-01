package tn.dymes.store.entites;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyOrder implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    long id;
    String date;
    Float totalWithTax;
    String paymentType;
    String clientType;
    @Column(nullable = true)
    String fullName;
    @Column(nullable = true)
    String address;
    @Column(nullable = true)
    String city;
    @Column(nullable = true)
    String state;
    @Column(nullable = true)
    String zipCode;
    @Column(nullable = true)
    String pays;
    Float shippingFees;
    @Column(nullable = true)
    String TVAcode;
    @Column(nullable = true)
    String principalPhone;
    @Column(nullable = true)
    String secodaryPhone;
    @Column(nullable = true)
    String email;
    @Column(nullable = true)
    String comment;
    String status;

    @OneToOne
    Promotion promotion;

    @OneToOne
    User user;

    @OneToMany
    private List<MyOrderProductData> myOrderProductsData;

    @OneToMany
    private List<Claim> claims;




}
