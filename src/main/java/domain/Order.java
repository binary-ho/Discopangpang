package domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

  @Id
  @GeneratedValue
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id")
  private User user;

  @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
  private Delivery delivery;

  @NotEmpty
  private Date date;
  @NotEmpty
  private int order_quantity;
  @NotEmpty
  private int order_price;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @Enumerated(EnumType.STRING)
  private Payment payment;
}