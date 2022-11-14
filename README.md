# Discopangpang
Database Systems 수업과 JPA 강의를 들으며, 이론적으로 학습한 내용들을 직접 적용해보기 위해 만든 프로젝트입니다. <br>
직접 처음 부터 끝까지 설계하고 만드는 과정에서 이론적인 이해가 높아지고, 사용법에 익숙해지길 기대하며 만들었습니다. <br> <br>


디스코 팡팡은 쿠팡의 DB를 관계 모델 부터 ER Diagram, 그리고 Spring Entity까지 설계해보는 프로젝트입니다! <br>
- Spring Boot + JPA
- MySQL
- **(with google java style guide)** 
- [전체 흐름] <br>
**화면 보며 DB 테이블 분석하기 -> 관계 모델 그려보기 -> ER 다이어그램 그려보기 -> 정규화 -> 엔티티 구현 -> 비즈니스 로직 구현 -> 화면 구현**

<br>

[테이블과 엔티티 설계에서 고민했던 점들 바로가기](#4-테이블과-엔티티를-설계하며-고민했던-부분들)

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

5. **배송**
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

## 4.0 정규화 체크
각 테이블의 구성이 올바른지 확인하기 위해 기본 정규화를 진행하겠다. (Diagram 그리기 이전에 실시하는 것이 옳으나, 늦게 정규화의 존재를 알게 됨.) <br>
1. **제 1 정규형:** 모든 필드의 vaule가 하나임.
2. **제 2 정규형:** 모든 릴레이션의 기본 키가 id 하나이다. **부분 함수 종속성이 존재하지 않고, 모든 속성이 기본키에 완전 함수 종속됨.** <br> **제 2 정규형 만족**
3. **제 3 정규형:** Order와 Delivery에서 **이행적 함수 종속성은 발견되지 않음** 제 3 정규형 만족
4. **Boyce/Codd:** 후보키인 delivery_id가 다른 모든 요소를 결정할 수 있다!! 엔티티의 수정이 필요하다. 고민이 풀리지 않는다 교수님께 여쭈어 보자. (TODO: 22/11/12)


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

(테이블 간의 관계가 다른 면이 꽤 있고.. 관계 모델, ER Diagram을 세세하게 짠다는 점이 강의와의 차별점이기는 하다 ㅠㅠ) <br>


### 4.4 How to Generate the Primary Key Value
처음에는 강의와 같이 AI로 만들어 주었으나, Url에 pk가 유출되는 요소들은, AI를 쓰지 말아야 한다는 사실을 후에 배우게 되었다.
예를 들어 유저의 경우, 경쟁사들이 숫자를 대입하며 손쉽게 전체 가입자 수를 유추할 수가 있게 된다. <br>

따라서, url에 id가 노출될 경우 예민할 수 있는 유저, 주문, 배송 Entity에 한해 UUID 정책으로 id값을(PK) 정해 주었다. 

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
      @GeneratedValue(generator = "uuid2")
      @GenericGenerator(name = "uuid2", strategy = "uuid2")
      private Long id;

      @NotEmpty
      private String name;
      @NotEmpty
      private String email;
      @NotEmpty
      private String contact;
      @NotEmpty
      private String password;

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
      private List<Delivery> sentDeliveries = new ArrayList<>();
}
```
1. 비어있으면 안 되는 정보들에 대해 `@NotEmpty`를 걸어 주었다.
2. Address와 같은 요소는 따로 객체를 만들어서 `@Embedded` 해주었다.
3. `@Enumerated`를 통해 맴버쉽 상태를 구현했다.
#### 4. user에서 파는 물건, 주문한 주문건들, 배송을 기다리는 물품, 발송한 물품등을 조회 가능하게 만들어 주었다.
전부 리스트의 형태로 가지고 있는데, 이런 처리를 해준 경우, 주의해야 할 점이 있다. <br> 
API를 구현 할 떄, Entity를 직접 노출시키지 않고, DTO를 사용해야 한다는 점이다. <br>
불필요한 리스트들이 전부 조회될 수 있다. 이는 원하는 상황도 아니고, 4개의 리스트가 한번에 딸려 나가므로 무겁다. <br>
`@JsonIgnore` 어노테이션을 통해 나가지 않게 해줄 수 있지만, DTO의 사용은 다른 장점도 있기 떄문에, 그냥 후에 DTO를 도입함으로서 처리해주겠다.

### 2. Product Entity
판매자가 판매하는 물건들에 대한 엔티티
```java
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
```
1. 외래키로 user의 id를 참조하고 있다. 컬럼에선 `seller_id`로 나타난다. 이를 `@JoinCoulumn`으로 나타내었다.
2. User는 Product와 1:다 관계이므로, 외래키는 Product에 있다. **따라서 Product가 연관 관계의 주인이다!**
      이에, Product table에 user의 id가 오게 되므로, `@JoinCoulumn`가 User에 걸렸고, <br> User에 있는 Product list인 `sellingProducts`에는 `Mapped By`가 걸렸다.
3. 판매 물건과 유저는 `@ManyToOne`관계이다. 한 판매자는 여러 물건을 판매 등록할 수 있다.
4. `OneToOne`과 `@ManyToOne`은 기본적으로 Eager fetch이다. eager한 fetch는 추적이 어렵고 JPA에서 N+1 문제를 유발 할 수 있기 때문에 LAZY하게 바꾸어 주었다.
5. 비어있으면 안 되는 필드들에 `@NotEmpty`를 달아 주었다.
6. 한 물건은 여러 주문건에 속해있을 수 있다. 이에, order와 `@OneToMany` 관계를 설정해 주었다.

### 3. Order Entity
```java
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
}
```
1. `@Table(name = "orders")`를 통해 테이블 이름을 orders로 해주었다. 표준 SQL `ORDER BY` 명령어의 존재로 테이블의 이름이 order인 경우 실수나 오류가 발생할 수도 있기 떄문이다.
2. product의 pk 값과 user의 pk 값을 각각 `@JoinColumn`을 통해 `product_id`, `buyer_id`로 가져오고 있다.
3. Delivery와 1대 1 관계를 갖는다. 하나의 주문에는 하나의 배송이 있다. 이 떄, 주문이 우선으로 배송이 관계의 주인이 된다.
4. **Order가 지워질 경우 Delivery를 삭제한다.** `@OneToOne(cascade = CascadeType.ALL, ...)` Order에 있는 이유는, 주문이 사라지면 배송건도 사라지는게 자연스럽고, **배송이 어쩌다 삭제 되는 에러가 발생해도, 주문이 남아 있는 것이 자연스럽고 복구에도 적절하다.**
5. 주문 상태와 결제 수단은 `@Enumerated`로 enum으로 구성해주었다.

### 4. Bundle Entity
```java
@Entity
@Getter
@Setter
@NotEmpty
public class Bundle {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;

    private int quantity;
    private int bundleAmount;
}
```
1. Product와 Order는 **다대다 관계를 가졌다.** 다대다 관계는 테이블을 구성할 때 PK값이 명확하지 않은 등의 문제가 많으므로 중간에 완화 시켜줄 Bundle을 도입했다.
2. 실생활에도 이것이 자연스럽다. 한 주문에 여러 종류의 물건이 1개 이상 참여하고, 한 물건은 다양한 주문건에 다양한 갯수로 참여 가능하니, 중간에 엔티티를 하나 더 도입해줬다.
3. 이에 Product와 Order에 대해 일대다의 관계를 가지게 되어서 Bundle Entitiy에 둘의 id가 필드로 들어왔다.

### 5. Delivery Entity
```java
@Entity
@Getter
@Setter
public class Delivery {

      @Id
      @GeneratedValue(generator = "uuid2")
      @GenericGenerator(name = "uuid2", strategy = "uuid2")
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
```
1. User의 id를 2개 가져오고 있다. `@JoinColumn`을 통해서 쉽게 처리할 수 있었다. 이는 `@JoinColumn`의 대략적인 동작을 알면 이해가 빠른데, `@JoinColumn`는 객체 안에서 알아서 PK 값을 찾아 참조해주는 마법을 부린다.



## 6. 엔티티 비즈니스 로직 (도입 예정)
여기까지는 기본적인 엔티티를 구성하는 작업이였다. <br>
한 엔티티의 필드만을 직접 조작하거나, 별도의 생성 메서드가 필수인 경우, <br> 
엔티티 안에 비즈니스 로직을 넣어줄 수 있다. <br>
조금만 더 배우고 나서 도입할 예정이다

[comment]: <> (연관관계 주인 다시 보기.)
[comment]: <> (UUID랑 OrderItems를 도입해야 한다 ㅠㅠ)

[comment]: <> (    @Column&#40;columnDefinition = "BINARY&#40;16&#41;"&#41;)