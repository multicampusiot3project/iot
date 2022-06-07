## ****🖥 IoT Team****

- [김진우](https://github.com/jinu12)
- [강기범](https://github.com/paramore0)
- [프로젝트상세보기](https://github.com/multicampusiot3project/info)

## **🛒 시각 장애인 마트 쇼핑 서비스**

### **✔ 목적**

- 시각 장애인의 쇼핑 편의 향상

### **📢 방향**

- 제품 이미지 학습 및 쇼핑 경로 안내

### **🕹 수행 방법▪도구**

- 라이브러리 : (안드로이드) 카메라 api, mosquitto , android studio, arduino, sqllite
- 언어 : python 3.8.4, kotlin
- 센서 : ISD1820 센서, pi camera v2 , hc sr04(초음파거리측정),비콘 송수신기
- 비콘 발신기 ~/6000 수신기 ~/4000

### **⭐ 필수 기능**

- 거리 측정 센서를 이용해서 앞에 물체가 있는지 확인
- ISD1820 음성재생 모듈 음성 인식이 나오도록 구현
- 안드로이드 카메라로 상품 인식하기
- 비콘 삼각측량으로 사용자 위치 파악. 오차는 배제

### **📚 데이터**

- ISD1820 [https://www.devicemart.co.kr/goods/view?no=1326916](https://www.devicemart.co.kr/goods/view?no=1326916) vat 포함 %%4000 한개 필요함.
- pi camera v2  한개 필요하므로 기존 보유한 것으로 사용가능.
- [https://smartstore.naver.com/jy-soft/products/5563264345](https://smartstore.naver.com/jy-soft/products/5563264345)
- wps 시스템
- [http://redpin.org/](http://redpin.org/)
- 비콘 시스템
- [https://navigine.com/ko](https://navigine.com/ko)
- 다양한 실내 위치추적 오픈소스
- [https://www.opensourceagenda.com/tags/indoor-positioning](https://www.opensourceagenda.com/tags/indoor-positioning)

- 비콘 판매처
    
    발신기
    
    수신기
    
- 상황 제한.
    
    안드로이드 마트 도착까지 걸리는 시간은 gps로 사용자위치 마트위치 직선거리로 계산
    실제 환경에서 테스트 할 것이 아니기 때문
    버튼 누르면 사용자의 gps 위치 발신
    마트 고객센터에서 대략적인 도착예정시간 담당자에게 전달
    마트에 도착하면 담당자가 지정된 카트를 전달.
    입구가 여러곳이라면 카트를 전달하기위해 장소를 특정하기위해
    입구를 제한할 필요가 있음
    카트에는 카메라, 거리감지센서, 비콘발신기, 음성재생기, 범퍼 필요
    범퍼는 단순 충격에 대비한것
    카트가 전달되면 비콘 통신을 이용해 위치를 특정하고
    지정된 상품을 구매하기위해 경로를 작성해야함.
    작성된 경로를 따라 사용자가 이동
    사용자가 상품을 선택하고, 선택한 상품이 맞는지 확인하기위해
    안드로이드로 상품을 촬영해서 상품의 일치를 확인
    계산대로 이동해서 상품을 계산 후 이동...

### iot 플로우 차트

![iot 플로우차트](https://user-images.githubusercontent.com/99372065/171769590-070f758a-f712-425c-bc76-2dcbff60eb6a.png)

### 안드로이드 와이어 프레임

## 🛒 1. 전체 시나리오
### 1. 장바구니 내역 생성
- 빅데이터 : 상품 카테고리 데이터 저장 후 장바구니 내역 상품의 카테고리 분류 진행
- 고려 사항 : 테스트 데이터 설정
![image](https://user-images.githubusercontent.com/52309288/171770105-18c850b5-04ab-4775-92ab-fe9899787df9.PNG)
### 2. 장바구니 내역에 따른 쇼핑 경로 생성
- 빅데이터 : 매장 지도 정보 수집
-  고려 사항: 상품 픽업 우선순위 / 최단 경로
### 3. 상품이 위치한 구역으로 카트 안내 시작
- 빅데이터 : 매장 정보 구축
- AI : 최적 쇼핑 경로 생성, 장애물 회피
- IoT : 위치 추적 - 3점측량법, 장애물 회피
- 음성 안내 (TTS) : 경로 안내, 도착 후 해당 상품 소개
![image](https://user-images.githubusercontent.com/52309288/171770108-36ac6b52-1b9a-458a-89e7-dc109f42653b.PNG)
![image](https://user-images.githubusercontent.com/52309288/171770270-26ac4188-fade-4d25-ac15-360984267a0d.PNG)
### 4. 상품 촬영 ▪ 정보 제공
![image](https://user-images.githubusercontent.com/52309288/171770110-4150bfa5-529f-48c0-a055-b01770971c8e.PNG)
![image](https://user-images.githubusercontent.com/52309288/171770111-d2030e13-69cd-499f-ac04-f87751f7b603.PNG)
### 5. 앱 내에서 제스쳐로 구매 여부 결정
- 고려 사항 :  음성 안내 + 제스쳐
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



[참고자료](https://www.notion.so/e5fbcf9d5bee4f2ab445c399c209993e)

용산 

계획한 기능을 모두 구현했다면 추가적으로 진행해도 좋을 것 같다

[https://youtu.be/w_6MqQQQxNY](https://youtu.be/w_6MqQQQxNY)

선택한 옷에 부착된 nfc를 리딩하면 음성으로 옷에 대한 정보를 제공한다.
