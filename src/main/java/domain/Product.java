package domain;

import java.lang.reflect.Member;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_id")
    private User user;

    @NotEmpty
    private String name;

    private String photo_url;
    private String review;

    @NotEmpty
    private int price;

    @NotEmpty
    private int stock;

    private int option_price;
    private String details;
}
