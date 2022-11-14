## 실패한 디자인들
<img src="https://user-images.githubusercontent.com/71186266/201459524-88b060d5-8da7-49a0-865d-56450fb58d5a.jpg" width=60% alt=""> 

- 집단화는 부적절하다. 배송은 '주문'과 연관 되어야한다. 일련의 주문과정 전체와 연관될 필요가 없다! 과정 전체와 관련된 attribute가 필요할 떄만 집단화를 해주는 것이 맞다.

<br> <br>
<img src="https://user-images.githubusercontent.com/71186266/201029925-85c278b9-a794-47c8-8360-18e97fe3f40a.jpg" width=60% alt=""> 
<img src="https://user-images.githubusercontent.com/71186266/200966471-2501173d-4af0-43fb-9920-1be21d2d24d9.png" width=60% alt="">

- 이렇게 되면 주문 하나에 한 제품 밖에 참여 못 한다. 여러 제품이 참여할 수 있어야한다.

<br> <br>

![KakaoTalk_20221112_130438685_05](https://user-images.githubusercontent.com/71186266/201459483-44a21461-4dc3-42ce-af6f-5150d10415d7.jpg)

- 1:1 관계 릴레이션에서 키를 서로 참조하면서 이행적 종속성이 생겨 제 3 정규형이 크게 깨진다.
- 그리고 서로를 참조하면서 주문이나 배송을 지울 수가 없게 되어버리는 이상한 상황이 벌어진다.
