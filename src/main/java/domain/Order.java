package domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User user;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @NotEmpty
    private Date date;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @NotEmpty
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    private Payment payment;

    @OneToMany
    private List<Bundle> orderBundles;

    // TODO
    public static Order createOrder(User user, Delivery delivery, Product... products) {
        Order order = new Order();
        return order;
    }
}