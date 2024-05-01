package tn.dymes.store.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Upsell implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    long id;
    String name;
    @Column(nullable = true)
    String headerType;
    @Column(nullable = true)
    String header;
    @Column(nullable = true)
    String bodyType;
    @Column(nullable = true)
    String body;
    @Column(nullable = true)
    String footerType;
    @Column(nullable = true)
    String footer;
    String buttonsPosition;
    String btnConfirmText;
    String btnConfirmTextColor;
    String btnConfirmColor;
    String btnConfirmSize;
    String btnCancelText;
    String btnCancelTextColor;
    String btnCancelColor;
    String btnCancelSize;
    Integer countYes;
    Integer countNo;
    boolean status;
    String creationDate;
    long nextProductID;
    long upsellProductID;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Upsell otherUpsell = (Upsell) obj;
        return id == otherUpsell.id;
    }

}
