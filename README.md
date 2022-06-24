# IOT part

## ****🖥 IoT Team****

- [김진우](https://github.com/jinu12)
- [강기범](https://github.com/paramore0)

## **🛒 시각 장애인 마트 쇼핑 서비스**

### **✔ 목적**

- 시각 장애인의 쇼핑 편의 향상

### **📢 방향**

- 제품 이미지 학습 및 쇼핑 경로 안내
- 시각 장애인 전용 안드로이드 앱 만들기

### **🕹 수행 방법▪도구**

- 사용
- 라이브러리 : (안드로이드) 카메라 api, mosquitto , android studio, arduino, sqllite
- 언어 : python 3.8.4, kotlin
- 센서 : pi camera v2 , hc sr04(초음파거리측정)

### **⭐ 필수 기능**

- 임베디드
    - 거리 측정 센서를 이용해서 앞에 물체가 있는지 확인
    - 비콘 삼각측량으로 사용자 위치 파악. 오차는 배제
- 안드로이드
    - 안드로이드로 쇼핑리스트 작성
    - 안드로이드 카메라로 상품 인식하기
    - 안드로이드 내장 tts로 상품 위치 안내

### iot 플로우 차트

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/2c4b26f7-8969-49ba-90ec-7472dc7a418e/Untitled.png)

![https://user-images.githubusercontent.com/99372065/171769590-070f758a-f712-425c-bc76-2dcbff60eb6a.png](https://user-images.githubusercontent.com/99372065/171769590-070f758a-f712-425c-bc76-2dcbff60eb6a.png)

## 클라우드 아키텍처

### 안드로이드 와이어 프레임

[https://drive.google.com/file/d/1a3Ei98LtoAY8IDAtKYGZlSafnTqI163x/view?usp=sharing](https://drive.google.com/file/d/1a3Ei98LtoAY8IDAtKYGZlSafnTqI163x/view?usp=sharing)

## 🛒 1. 전체 시나리오

### 1. 장바구니 내역 생성

- 빅데이터 : 상품 카테고리 데이터 저장 후 장바구니 내역 상품의 카테고리 분류 진행
- 고려 사항 : 테스트 데이터 설정
    
    ![https://user-images.githubusercontent.com/52309288/171770105-18c850b5-04ab-4775-92ab-fe9899787df9.PNG](https://user-images.githubusercontent.com/52309288/171770105-18c850b5-04ab-4775-92ab-fe9899787df9.PNG)
    

### 2. 장바구니 내역에 따른 쇼핑 경로 생성

- 빅데이터 : 매장 지도 정보 수집
- 고려 사항: 상품 픽업 우선순위 / 최단 경로

### 3. 상품이 위치한 구역으로 카트 안내 시작

- 빅데이터 : 매장 정보 구축
- AI : 최적 쇼핑 경로 생성, 장애물 회피
- IoT : 위치 추적 - 3점측량법, 장애물 회피
- 음성 안내 (TTS) : 경로 안내, 도착 후 해당 상품 소개
    
    ![https://user-images.githubusercontent.com/52309288/171770108-36ac6b52-1b9a-458a-89e7-dc109f42653b.PNG](https://user-images.githubusercontent.com/52309288/171770108-36ac6b52-1b9a-458a-89e7-dc109f42653b.PNG)
    
    ![https://user-images.githubusercontent.com/52309288/171770270-26ac4188-fade-4d25-ac15-360984267a0d.PNG](https://user-images.githubusercontent.com/52309288/171770270-26ac4188-fade-4d25-ac15-360984267a0d.PNG)
    

### 4. 상품 촬영 ▪ 정보 제공

![https://user-images.githubusercontent.com/52309288/171770110-4150bfa5-529f-48c0-a055-b01770971c8e.PNG](https://user-images.githubusercontent.com/52309288/171770110-4150bfa5-529f-48c0-a055-b01770971c8e.PNG)

![https://user-images.githubusercontent.com/52309288/171770111-d2030e13-69cd-499f-ac04-f87751f7b603.PNG](https://user-images.githubusercontent.com/52309288/171770111-d2030e13-69cd-499f-ac04-f87751f7b603.PNG)

### 5. 앱 내에서 제스쳐로 구매 여부 결정

- 고려 사항 : 음성 안내 + 제스쳐

### 6. 장바구니 내역 상품 모두 픽업 시 계산대 경로 안내

### 7. 결제 후 카트 반납

## 🛍 2. 장바구니 작성 시나리오

- TTS, STT : 구글/네이버 API(CLOVA Speech Recognition, CLOVA Voice)
- 안드로이드 내장 TTS
- UI 구성
마트 지정

### 2-1. 장바구니

- 기능 : 설명 듣기, 뒤로 가기, 장바구니 추가, 장바구니 삭제, 카트에 정보 전송 등
- 고려 사항 : 음성 안내 + 제스쳐

## 📸 3. 상품 인식 시나리오

1. 상품 검색 클릭
2. 상품 촬영 클릭
3. 상품 상세 설명 (+ 유사 상품 추천)
4. 상세 설명 종료
5. 구매 결정 / 선택 취소

### **📚 데이터**

- pi camera v2  한개 필요하므로 기존 보유한 것으로 사용가능.
- 사용자 위치추적 시스템은 와이파이, 블루투스, 비콘 등 모두 동일하게 삼각측량법을 사용
- 다른것은 설치가능한 기준점의 갯수인데 와이파이의 기준점이 가장 많다.
- 와이파이는 근본적으로 정확도가 떨어지지만 기준점 다량 설치로 정확도를 끌어올릴 수 있다.
- 무엇보다 비용이 적게 드는데 와이파이 공유기를 이미 가지고 있고 기본적으로 모두의 휴대폰에 관련기술이 준비되어있다
- 인터넷 활성화가 되지 않은 공유기도 신호 쏘는게 가능하다 5g와 2g 차이가 있다
- [wps 시스템](http://redpin.org/)
- [비콘 시스템](https://navigine.com/ko)
- [다양한 실내 위치추적 오픈소스](https://www.opensourceagenda.com/tags/indoor-positioning)
    - wifiindoorpositioning / ble indoor positioning / whereami / positioning navigation system
- wifiindoorpositioning
- 공유기 위치마다 액세스 포인트 설정을 해야한다
- 와이파이의 비밀번호 알 필요 x 공유기의 위치와 와이파이 이름을 정확히 알면 됨
- x축과 y축 값을 설정하는 것을 말하는데 이렇게 하면 직접 지도를 그리는게 가능할것 같다
- 1번 와이파이는 x 0 y 0 2번 와이파이는 x 30 y 0 3번 와이파이는 x0 y 30 이런식으로…
- 구형 폰으로만 테스트 가능…
- 상황 제한
    - 안드로이드 마트 도착까지 걸리는 시간은 gps로 사용자위치 마트위치 직선거리로 계산
    - 실제 환경에서 테스트 할 것이 아니기 때문
    - 버튼 누르면 사용자의 gps 위치 발신
    - 마트 고객센터에서 대략적인 도착예정시간 담당자에게 전달
    - 마트에 도착하면 담당자가 지정된 카트를 전달.
    - 입구가 여러곳이라면 카트를 전달하기위해 장소를 특정하기위해
    - 입구를 제한할 필요가 있음
    - 카트에는 카메라, 거리감지센서, 비콘발신기, 음성재생기, 범퍼 필요
    - 범퍼는 단순 충격에 대비한것
    - 카트가 전달되면 비콘 통신을 이용해 위치를 특정하고
    - 지정된 상품을 구매하기위해 경로를 작성해야함.
    - 작성된 경로를 따라 사용자가 이동
    - 사용자가 상품을 선택하고, 선택한 상품이 맞는지 확인하기위해
    - 안드로이드로 상품을 촬영해서 상품의 일치를 확인
    - 계산대로 이동해서 상품을 계산 후 이동...

[참고자료](https://www.notion.so/e5fbcf9d5bee4f2ab445c399c209993e)

- 와이파이 공유기 5개
- [선택한 옷에 부착된 nfc를 리딩하면 음성으로 옷에 대한 정보를 제공하는 서비스](https://youtu.be/w_6MqQQQxNY)

### 멘토님 피드백(6.4)

- 계획한 기능을 모두 구현했다면 추가적으로 진행해도 좋을 것 같다
- 명확한 목표가 필요
- 과연 매대의 어느 정도 수준까지 안내를 할것인가
- 매대의 상품까지 안내를 한다면 어느 정도의 정확도가 필요한지?
- 정확하다면 얼마나 정확한가

### 멘토님 벤치 마킹 사이트

- [스마트 글래스](https://github.com/ankursikarwar/TetraChrome-Lenses)
- [시각장애 도우미](https://github.com/mansi1710/Assistant-for-visually-impaired)
- [냉장고무현금자판기](https://github.com/aws-samples/smart-cooler/blob/master/README.en.md)

### IBeacon

- 비콘과 관계없이 gps로 구현한 [카카오맵에 초정밀 버스](https://www.hankyung.com/it/article/2019090910801)
- https://github.com/andreyukD/BLE-Beacon-Indoor-Positioning
- https://github.com/neXenio/BLE-Indoor-Positioning
- https://github.com/andreacanepa/Beacons-Indoor-Positioning-System : indoor-positioning
- 깃허브 토픽 : https://github.com/topics/indoor-positioning아
- [아두이노(ESP32)를 비콘(iBeacon)으로 사용하기](https://www.youtube.com/watch?v=mijgQe3IdOk&t=376s)

### dwm 1000

- [dwm1000을 이용한 아두이노 실내 위치 추적 시스템(거리 측정 테스트)](https://blog.naver.com/PostView.naver?blogId=mapes_khkim&logNo=222330798815&parentCategoryNo=&categoryNo=6&viewDate=&isShowPopularPosts=true&from=search)
- [아두이노 실내 위치 추적 시스템 - 모형 자동차 테스트](https://www.youtube.com/watch?v=1quNgt3ticw)
- [수학 공식](https://math.stackexchange.com/questions/884807/find-x-location)
- [삼각 측량 수학 설명](https://youtu.be/r5_0c3Su3xM?t=410)
- 자세하게 적혀있고 설명도 잘 되어있음
- 설명에 사용된 링크가 나와있어 확인하기 좋음
- 아두이노를 사용해서 다른 부분이 있지만 실내위치추적에 대한 자료가 많음
- [가장 유사 시스템](https://github.com/YoungJung93/Exit_Guidance)
- [dwm 외국 프로젝트](https://medium.com/@newforestberlin/precise-realtime-indoor-localization-with-raspberry-pi-and-ultra-wideband-technology-decawave-191e4e2daa8c)
- [dwm 외국 github 자료](https://github.com/gsongsong/dw1000-positioning)
- [https://www.mouser.kr/ProductDetail/Qorvo/DWM1000?qs=TiOZkKH1s2R6b5D6df63Pg%3D%3D](https://www.mouser.kr/ProductDetail/Qorvo/DWM1000?qs=TiOZkKH1s2R6b5D6df63Pg%3D%3D)
- [https://github.com/F-Army/arduino-dw1000-ng/tree/master/examples](https://github.com/F-Army/arduino-dw1000-ng/tree/master/examples)
- [https://makernambo.com/162](https://makernambo.com/162)

## 안드로이드 관련 자료

- [시각장애인 안드로이드 이용방법](https://www.youtube.com/watch?v=6jT-POAaB0c)
- 안드로이드 이미지 인코딩,디코딩
[https://imleaf.tistory.com/82](https://imleaf.tistory.com/82)

## 📚 데이터 베이스

![group3.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/05624464-e553-4091-8808-1df2f4650fc7/group3.png)

## 🕹️ 기술 구현

## 임베디드

- [x]  거리 감지 센서를 이용해서 이미지 전달
- [x]  mqtt 통신을 통해 aws s3 버킷파일에 이미지 전달

## 안드로이드

- [x]  안드로이드 tts 구현
- [x]  aws에서 django rest api 를 통해 로그인 구현
- [x]  안드로이드 쇼핑 리스트 작성
- [x]  mqtt 통신을 통해 aws s3 버킷파일에 이미지 전달
- [x]  tts로 모든 버튼이 1번 누르면 음성이 2번 거기에 해당되는 기능
- [x]  mqtt로 ai에서 보낸 메시지를 음성으로 나오게
- [x]  쇼핑리스트 작성된 쇼핑리스트를 mysql db에 전달

## 🤖 수행과정

[https://lh4.googleusercontent.com/denjHCCjjyL4JHxvpFrehnlBHAusIOo7573y8nM8hm5jR01MKelkTH_fo1DkruRLbZxu4lI4ZF5Q8gBd6wX2uJvFhoEZqeXhBvstAya_7JCZNxOGWWvJHc71K-H_TLdkG-4mF_BlChgvz1P-xitZvQ](https://lh4.googleusercontent.com/denjHCCjjyL4JHxvpFrehnlBHAusIOo7573y8nM8hm5jR01MKelkTH_fo1DkruRLbZxu4lI4ZF5Q8gBd6wX2uJvFhoEZqeXhBvstAya_7JCZNxOGWWvJHc71K-H_TLdkG-4mF_BlChgvz1P-xitZvQ)

## dwm1000

- 설치 비용 비쌈
- 측위 오차 매우 낮음

[https://lh3.googleusercontent.com/kqN00iwMKq6xFJ3G_9q1o8zkdfQ-qPTTPO9dTofZWWaKAyQUqfjFkBDA1WIFEwRRfjAPzp6QlA0Ag8Anlti-w4qDTiF-I6uj_rBJiAb7cOzdhayqRscYKEyWJrMGRamvkVSRkL17qmnIRS3WmZjd1w](https://lh3.googleusercontent.com/kqN00iwMKq6xFJ3G_9q1o8zkdfQ-qPTTPO9dTofZWWaKAyQUqfjFkBDA1WIFEwRRfjAPzp6QlA0Ag8Anlti-w4qDTiF-I6uj_rBJiAb7cOzdhayqRscYKEyWJrMGRamvkVSRkL17qmnIRS3WmZjd1w)

## bluetooth / wifi

- 핑거프린팅
- 설치 비용 중간
- 측위 오차 중간
- 소요 시간 김
    
    [https://lh3.googleusercontent.com/mAaClfcqfgI5acW67f5FJSwqxF-eMF7wZKSDeJUrvr49PiyS_aF1VztObKsSGvsTOpfySw3jpJLa85qYQpej4lSppzPpy2ukTihaW9QjUHcKD7Ruxu4rzwXjni2b1zJErN6tZ8LJVwMWHhPGQPoT9A](https://lh3.googleusercontent.com/mAaClfcqfgI5acW67f5FJSwqxF-eMF7wZKSDeJUrvr49PiyS_aF1VztObKsSGvsTOpfySw3jpJLa85qYQpej4lSppzPpy2ukTihaW9QjUHcKD7Ruxu4rzwXjni2b1zJErN6tZ8LJVwMWHhPGQPoT9A)
    

## 카메라

- 설치 비용 매우비쌈
- 대상추적 어려움
    
    [https://lh4.googleusercontent.com/PaEfzbYuIBOEJo5tx7WnHL2JBk5ob2wHC92qVUW80PECoD8No7ehbRlinmpItBuGlnN31OvgS10Flp5axtZmDcqcfoaxd_QYfxNFlvG-qci9k21L9okw4vrlbbjkau4_OKQ4CGNuPS-0T9hiq0b6Jw](https://lh4.googleusercontent.com/PaEfzbYuIBOEJo5tx7WnHL2JBk5ob2wHC92qVUW80PECoD8No7ehbRlinmpItBuGlnN31OvgS10Flp5axtZmDcqcfoaxd_QYfxNFlvG-qci9k21L9okw4vrlbbjkau4_OKQ4CGNuPS-0T9hiq0b6Jw)
    

## gps

- 설치 비용 없음
- 측위 오차 매우 높음

[]()
