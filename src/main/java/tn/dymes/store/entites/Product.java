package tn.dymes.store.entites;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    long id;
    @Column(nullable = true)
    String barCode;
    @Column(nullable = true)
    String reference;
    String name;
    @Column(nullable = true)
    String supplier;
    String description;
    String status;
    @Column(nullable = true)
    Float shippingPrice;
    Float buyPrice;
    Float sellPrice;
    @Column(nullable = true)
    Integer stock;
    int TVA;
    String simpleDiscountType;
    @Column(nullable = true)
    Float simpleDiscountValue;
    String creation_date;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @OneToOne(mappedBy = "product", cascade = CascadeType.REMOVE)
    private Timer timer;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Upsell> upsells = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Discount> discounts = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Variant> variants = new ArrayList<>();

    @OneToOne(mappedBy = "product", cascade = CascadeType.REMOVE)
    private VisitorsRange visitorsRange;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<Rating> ratings = new ArrayList<>();






}
