package domain;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue()
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Embedded
    private Address receive_address;

    @NotEmpty
    private Date send_date;
    @NotEmpty
    private Date receive_date;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @NotEmpty
    private String delivery_details;
}