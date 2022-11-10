package domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Product {

    @Id @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User user;

    @NotEmpty private String product_name;
    private String product_photo_url;
    private String review;
    @NotEmpty private int product_price;
    @NotEmpty private int stock;
    private int option_price;
    private String product_details;

    @OneToMany(mappedBy = "product")
    private List<Order> orders = new ArrayList<>();
}
