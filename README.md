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
    

[참고자료](https://www.notion.so/e5fbcf9d5bee4f2ab445c399c209993e)

용산 

계획한 기능을 모두 구현했다면 추가적으로 진행해도 좋을 것 같다

[https://youtu.be/w_6MqQQQxNY](https://youtu.be/w_6MqQQQxNY)

선택한 옷에 부착된 nfc를 리딩하면 음성으로 옷에 대한 정보를 제공한다.
