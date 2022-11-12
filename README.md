# Discopangpang
MySQL을 이용해 DB 설계를 공부하기 위한 프로젝트 <br>
코피팡팡 <br>
쿠팡의 DB를 관계 모델 부터 ER Diagram, 그리고 Spring Entity까지 설계해보는 프로젝트.

## 1. 쿠팡 Database 테이블 분석하기
사진과 같은 쿠팡의 화면을 통해 간단하게 추정해본 도메인과 구성 필드들은 아래와 같습니다.
1. **회원**
- 회원 번호
- 회원 이름
- 이메일
- 전화번호
- 비밀번호
- 주소(임베디드)
- 와우 회원 여부

2. **상품**
- 상품 번호
- 판매자 정보 
- 상품명
- 상품 사진
- 리뷰
- 가격
- 상품 수량
- 옵션(추가 금액)

3. **주문 상품 묶음**
- 주문 상품 묶음 번호
- 제품 아이디
- 수량
- 총 금액
- 주문 아이디

4. **주문**
- 주문 번호 
- 주문 상품 묶음 번호 
- 주문 회원 
- 주문 날짜
- 배송 상태
- 주문 수량
- 결제 금액
- 결제 수단
- 주문일

5**배송**
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
![KakaoTalk_20221112_130438685_05](https://user-images.githubusercontent.com/71186266/201459483-44a21461-4dc3-42ce-af6f-5150d10415d7.jpg)



<br>

1. 주소는 회원이 존재해야 존재할 수 있다. (따로 테이블로 구현하지는 않음)
2. 회원은 구매자와 판매자로 나뉠 수 있다. (판매자의 등록 판매 수와 같은 필드는 예시로 넣은 것)
3. 등록 상품은 판매자가 존재해야만 존재할 수 있는 약개체이다. 특정 상품을 지칭하는 것이 아니라, 판매자가 올린 물건을 뜻하는 것이다. 예를 들어 내가 올린 연필과 친구가 올린 연필은 같은 회사 같은 제품이여도, 다르게 올라간다.
4. 주문은 구매자와 등록상품이 있어야 존재할 수 있다. 등록 상품을 묶어 번들을 만들고 그 번들들이 주문 건으로 들어간다.
5. 하나의 구매건은 한명의 구매자, 여러 상품 번들, 하나의 주문으로 이루어진다.
6. 배송은 주문이 있어야 존재할 수 있다. 한 주문당 하나의 배송이 있다.


## 3. ER Diagram 그려보기
![discopangpangerd2](https://user-images.githubusercontent.com/71186266/201459771-43f1082d-97c9-461b-8105-2afa507ac02a.png)

<br>

위에서 그려본 관계모델을 통해 ER Diagram을 그려봤다. <br>
주소는 테이블로 들어가기 보다는 그냥 객체를 User에 넣어 주었다. 아마 엔티티를 개발 할 때는 객체가 풀어져서 들어갈 것 같다. <br>

## 4. 테이블과 엔티티를 설계하며 고민했던 부분들
### 4.1 엔티티와 테이블의 차이
관련 강의나 학교에서 들은 수업으로는 차이를 명확하게 느끼지 못 했었다. <br>
그러다 보니, 직접 짜는 과정에서 문제들이 발생했다. <br>
예를 들어, 유저를 통해 유저가 주문한 주문들이나, 배송을 조회하고 싶다. 판매자가 등록한 물건들을 조회하고 싶다. <br>
이런 경우 당연히 **엔티티** 수준에서 서로를 알아야 한다. 요컨데, **유저 엔티티가 등록 상품이나 주문 리스트를 객체로서 가지고 있어야 한다는 것이다.**  <br>
어찌보면 너무나도 당연한 소리이지만, 테이블을 짜는 경우 **유저가 상품 리스트를 가진걸 어떻게 표현할 것인가?** <br>

**여기서 관계의 주인이라는 개념이 적용되는 것이였다.** <br>
내가 듣는 강의에서는 관계의 주인은 일대다의 경우 다 쪽이 갖고, 그 쪽이 외래키를 갖는다고 설명하였다. 
들었을 때는 그냥 그렇구나 하고 받아들였는데, 이제서야 그 의미를 알게 되었다. <br>
**테이블 끼리는 일-대-다 관계에서 굳이 서로를 알 필요 없다! 관계의 주인만 반대 편을 알면 된다!**  
**즉, 테이블에서는 주문이 유저의 정보를, 등록 상품이 판매자의 정보를 가지면 그만이지 유저가 그들의 정보를 가질 필요는 없다는 것이다!**
**엔티티 단위에서나 '다' 쪽을 객체로 가지고 있으면 되는 것이다!** <br>
나는 정말 많이 고민하고 나서 이해하게 되었는데, 이 프로젝트와 별개로 진행하는 스터디 팀원들은 이에 대한 고민이 없어 보였다. 쉽게 이해한 듯 하다.  <br>

### 4.2 필드 명에 대한 고민
필드 명들을 구성할 떄 많은 고민들이 있었다. 물론 컨벤션의 영역이라 깊게 고민할 필요는 없었으나,
그동안 배운 지식이나 선배님들의 조언에 따라, 각 테이블은 각자의 PK를 그냥 ID로 가지고 있고, 참조 될 때나 User의 id면 user_id와 같이 붙여주기로 했다. <br>
**즉, 각 테이블은 자신의 테이블만 신경 쓰도록 짜 주었다.** 이 편이 객체 지향성에 맞는 명명이라고 생각했다. <br>
물론 애초에 user_id로 가지고 있으면 컬럼 이름만 보고도 파악이 되니 좋을 수도 있다는 의견도 많았지만, 이 쪽이 더 좋아 보였다. <br>
(별거 아닌거 같지만 꽤나 오래 고민했다..)

### 4.3 설계 오류
프로젝트의 엔티티를 거의 완성 시켰을 때, 큰 오류를 발견했다. 한 주문에는 여러 상품들이 참여할 수 있다고 머리로는 생각해 놓고, 관계와 엔티티에는 반영이 안 되었다. <br>
Order가 Product의 id를 가지고 있었는데, 이건 절대 일-대-다 관계를 올바르게 나타낸 것이 아니다. 알고 보니 나는 다-대-다 관계를 짜고 있었던 것이다. <br>
개인 프로젝트인 만큼 김영한 선생님의 강의와 거리가 먼 설계를 해보고 싶었는데, 김영한 선생님이 조심해야 할 부분이라고 언급한 실수를 그대로 저지르고 있었다. 
이에, 강의 내용과 유사하게 **다-대-다 관계를 일-대-다, 다-대-일 관계로 완화시켜 줄 Bundle 엔티티를 도입했다.** <br>
결국 강의에서 보여준 예시와 비슷한 구성이 되었으나 더 적절한 관계를 짜게 되었다. <br>

(유저와 물품이 관계를 맺는다는 점과, 관계 모델, ER Diagram을 세세하게 짠다는 점이 강의와의 차별점이기는 하다 ㅠㅠ) <br>

<details>
<summary> <b>버리기 아까워서 올리는 실패한 설계들 모음</b> </summary>
<img src="https://user-images.githubusercontent.com/71186266/201459524-88b060d5-8da7-49a0-865d-56450fb58d5a.jpg" width=60% alt=""> 

- 집단화는 부적절하다. 배송은 '주문'과 연관 되어야한다. 일련의 주문과정 전체와 연관될 필요가 없다! 과정 전체와 관련된 attribute가 필요할 떄만 집단화를 해주는 것이 맞다.

<br> <br>
<img src="https://user-images.githubusercontent.com/71186266/201029925-85c278b9-a794-47c8-8360-18e97fe3f40a.jpg" width=60% alt=""> 
<img src="https://user-images.githubusercontent.com/71186266/200966471-2501173d-4af0-43fb-9920-1be21d2d24d9.png" width=60% alt="">

- 이렇게 되면 주문 하나에 한 제품 밖에 참여 못 한다. 여러 제품이 참여할 수 있어야한다.

<br>

</details>


## 5. 엔티티 기본 설계
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
3. User는 Product와 1:다 관계이므로, 외래키는 Product에 있다. **따라서 Product가 연관 관계의 주인이다!**
      이에, `@JoinCoulumn`가 User에 걸렸고, Product에는 `Mapped By`가 걸렸다.
4. 유저와 판매 물건은 `@ManyToOne`관계이다. 한 판매자는 여러 물건을 판매 등록할 수 있다.
5. 비어있으면 안 되는 필드들에 `@NotEmpty`를 달아 주었다.
6. 한 물건은 여러 주문건에 속해있을 수 있다. 이에, order와 `@OneToMany` 관계를 설정해 주었다.

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

  @OneToOne(mappedBy = "order", fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
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
4. **Order가 지워질 경우 Delivery를 삭제한다.** `@OneToOne(cascade = CascadeType.ALL, ...)` Order에 있는 이유는, 주문이 사라지면 배송건도 사라지는게 자연스럽고, **배송이 어쩌다 삭제 되는 에러가 발생해도, 주문이 남아 있는 것이 자연스럽고 복구에도 적절하다.**
5. 주문 상태와 결제 수단은 `@Enumerated`로 enum으로 구성해주었다.

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
1. User의 id를 2개 가져오고 있다. `@JoinColumn`을 통해서 쉽게 처리할 수 있었다. `@JoinColumn`의 대략적인 동작을 이해해야 구현 가능하다. `@JoinColumn`는 객체 안에서 알아서 PK값을 찾아 참조해주는 마법을 부린다.
## 5. 엔티티 비즈니스 로직
한 엔티티의 필드만을 직접 조작하거나, 별도의 생성 메서드가 필수인 경우, <br> 
엔티티 안에 비즈니스 로직을 넣어줄 수 있다.

[comment]: <> (연관관계 주인 다시 보기.)
[comment]: <> (UUID랑 OrderItems를 도입해야 한다 ㅠㅠ)