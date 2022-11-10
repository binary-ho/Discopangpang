# Discopangpang
MySQL을 이용해 DB 설계를 공부하기 위한 프로젝트 <br>
코피팡팡 <br>
쿠팡의 DB를 관계 모델 부터 ER Diagram, 그리고 Spring Entity까지 설계해보는 프로젝트.

## 1. 쿠팡 Database 테이블 분석하기
사진과 같은 쿠팡 화면을 통해 간단하게 추정해본 도메인과 구성 필드들은 아래와 같습니다.
1. **회원 테이블**
- 회원 번호
- 회원 이름
- 이메일
- 전화번호
- 비밀번호
- 주소(임베디드)
- 와우 회원 여부

2. **상품 테이블**
- 상품 번호
- 판매자 정보 
- 상품명
- 상품 사진
- 리뷰
- 가격
- 상품 수량
- 옵션(금액 추가
3. **주문**
- 주문 번호 
- 상품 번호 
- 주문 회원 
- 주문 날짜
- 배송 상태
- 주문 수량
- 결제 금액
- 결제 수단
- 주문일

4. **배송**
- 송장번호 
- 주문 번호
- 구매자 번호 
- 판매자 번호 
- 배송지
- 발송일
- 도착일
- 배송 상태
- 배송 요청사항

## 2. 관계 모델 그려보기
위에서 추측한 내용을 토대로 간략한 관계 모델을 그려보면 아래와 같습니다.
![KakaoTalk_20221110_164357170_04](https://user-images.githubusercontent.com/71186266/201029925-85c278b9-a794-47c8-8360-18e97fe3f40a.jpg)

<br>

1. 주소는 회원이 존재해야 존재할 수 있다.
2. 회원은 구매자와 판매자로 나뉠 수 있다. 판매자의 등록 판매 수와 같은 필드는 예시로 넣은 것
3. 등록 상품은 판매자가 존재해야만 존재할 수 있는 약개체이다. 특정 상품을 지칭하는 것이 아니라, 판매자가 올린 물건을 뜻하는 것이다. 예를 들어 내가 올린 연필과 친구가 올린 연필은 모델이 같아도 다르게 올라간다.
4. 주문은 구매자와 등록상품이 있어야 존재할 수 있다. 
5. 하나의 구매건은 한명의 구매자, 여러 상품, 하나의 주문으로 이루어진다.
6. 배송은 주문이 있어야 존재할 수 있다. 한 주문당 하나의 배송이 있다.


## 3. ER Diagram 그려보기
![discopangpangerd](https://user-images.githubusercontent.com/71186266/200966471-2501173d-4af0-43fb-9920-1be21d2d24d9.png)

<br>

위에서 그려본 관계모델을 통해 ER Diagram을 그려봤다.

## 4. 엔티티 기본 설계
스키마를 설정하는 작업은 생략했다. 바로 엔티티를 구현해보겠다. <br>
각 엔티티 코드 아래의 설명은 꼭 필요한 것만 적겠다. 이전 엔티티에서 설명한 개념은 생략한다. <br>

`@Setter`를 전체적으로 사용하였는데, 실제 구현에서는 이렇게 하면 안 된다. <br>
본 프로젝트는 복잡한 관계 모델을 직접 엔티티로 구현하는 것에 의의를 두었다.

### 1. User Entity
```java
@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @NotEmpty private String name;
    @NotEmpty private String email;
    @NotEmpty private String contact;
    @NotEmpty private String password;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private Membership membership;

    @OneToMany(mappedBy = "user")
    private List<Product> sellingProducts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    private List<Delivery> waitingDeliveries = new ArrayList<>();

    @OneToMany(mappedBy = "seller")
    private List<Delivery> sendDeliveries = new ArrayList<>();
}
```
1. 기본적으로 id는 PK로 해주었고, 이름을 "user_id"로 해주었다.
2. 비어있으면 안 되는 정보들에 대해 `@NotEmpty`를 걸어 주었다.
3. Address와 같은 요소는 따로 객체를 만들어서 `@Embedded` 해주었다.
4. `@Enumerated`를 통해 맴버쉽 상태를 구현했다.
#### 5. user를 매핑하는 곳들을 전부 user에서 조회할 수 있는 처리를 해 주었다. 
이런 처리를 해준 경우, 주의해야 할 점이 있다. <br> 
API를 구현 할 떄, Entity를 직접 노출시키지 않고, DTO를 사용해야 한다는 점이다. <br>
불필요한 리스트들이 전부 조회될 수 있다. `@JsonIgnore` 어노테이션을 통해 나가지 않게 해줄 수 있지만, DTO의 사용은 다른 장점도 있기 떄문에, 그냥 후에 DTO를 도입함으로서 처리해주겠다.

6. 유저와 유저의 주문들, 유저와 배송을 기다리는 물건들은 `@OneToMany`관계를 가지고 있다.
7. 판매자와 판매 물건, 발송한 물건들은 `@OneToMany`관계를 가지고 있다.


### 2. Product Entity
판매자가 판매하는 물건들에 대한 엔티티
```java
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
```
1. 외래키로 user의 id를 참조하고 있다. 컬럼에선 `seller_id`로 나타난다. 
2. 이를 `@JoinCoulumn`으로 나타내었다.
3. 유저와 판매 물건은 `@ManyToOne`관계이다. 한 판매자는 여러 물건을 판매 등록할 수 있다.
4. 비어있으면 안 되는 필드들에 `@NotEmpty`를 달아 주었다.
5. 한 물건은 여러 주문건에 속해있을 수 있다. 이에, order와 `@OneToMany` 관계를 설정해 주었다.

### 3. Order Entity
```java
@Entity
@Table(name = "orders")
@Getter @Setter
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

  @NotEmpty private Date date;
  @NotEmpty private int order_quantity;
  @NotEmpty private int order_price;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @Enumerated(EnumType.STRING)
  private Payment payment;
}
```
1. `@Table(name = "orders")`를 통해 테이블 이름을 orders로 해주었다. 표준 SQL `ORDER BY` 명령어의 존재로 테이블의 이름이 order인 경우 실수나 오류가 발생할 수도 있기 떄문이다.
2. product의 id와 user의 id를 각각 `@JoinColumn`을 통해 `product_id`, `buyer_id`로 가져오고 있다.
3. Delivery와 1대 1 관계를 갖는다. 하나의 주문에는 하나의 배송이 있다. 이 떄, 주문이 우선으로 배송이 관계의 주인이 된다.
4. 주문 상태와 결제 수단은 `@Enumerated`로 enum으로 구성해주었다.

### 4. Delivery Entity
```java
@Entity
@Getter @Setter
public class Delivery {

  @Id
  @GeneratedValue
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

  @NotEmpty private Date send_date;
  @NotEmpty private Date receive_date;

  @Enumerated(EnumType.STRING)
  private DeliveryStatus deliveryStatus;

  @NotEmpty private String delivery_details;
}
```
가장 복잡해 보이지만, 가장 구성이 간단하다.
1. User의 id를 2개 가져오고 있다. `@JoinColumn`을 통해서 쉽게 처리할 수 있었다. `@JoinColumn`의 대략적인 동작을 이해해야 구현 가능하다. `@JoinColumn`는 객체 안에서 알아서 PK값을 찾아 참조해주는 마법을 부린다.

## 5. 엔티티 비즈니스 로직
한 엔티티의 필드만을 직접 조작하거나, 아무렇게나 생성해서는 안 되는 경우, <br> 
엔티티 안에 비즈니스 로직을 넣어줄 수 있다.