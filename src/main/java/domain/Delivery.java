package domain;

import static javax.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
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