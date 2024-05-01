package tn.dymes.store.entites;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Promotion  implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    long id;
    String code;
    String promoType;
    @Column(nullable = true)
    String startDate;
    @Column(nullable = true)
    String expiryDate;
    String discountType;
    @Column(nullable = true)
    Float discountValue;
    String status;
}
