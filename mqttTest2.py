import base64
import paho.mqtt.publish as publisher
from threading import Thread, Event
import datetime
import time

now = datetime.datetime.now()



count = 0


class MqttWorker:
    def __init__(self):
        self.client = mqtt.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message

        self.exit_event = Event()

        # self.HC_SR04 = HC_SR04(self.client)
        # self.HC_SR04.start()

        # self.camera =  MyCamera(self.client)
        # self.camera.start(self.client)

    def signal_handler(self, signum, frame):
        print("signal_handler===================================")
        self.exit_event.set()  # 이벤트객체가 갖고 있는 플래그 변수가 True로 셋팅
        self.led.clean()
        # 현재 이벤트 발생을 체크하고 다른 작업을 수행하기 위한 코드 - 다른 메소드에서 처리
        if self.exit_event.is_set():
            print(
                "이벤트객체의 플래그변수가 Ture로 바뀜 - 이벤트가 발생하면 어떤 작업을 실행하기 위해서(다른 메소드 내부에서 반복문 빠져나오기~....)")
            exit(0)

    def mymqtt_connect(self):  # 사용자정의 함수 - mqtt서버연결과 쓰레드생성 및 시작을 사용자정의 함수로 정의
        try:
            print("브로커 연결 시작하기")
            self.client.connect("13.52.187.248", 1883, 60)
            mythreadobj = Thread(target=self.client.loop_forever)
            mythreadobj.start()
            publisher.single("android/picture", "product1", hostname="13.52.187.248")
        except KeyboardInterrupt:
            pass
        finally:
            print("종료")

    def on_connect(self, client, userdata, flags, rc):  # broker접속에 성공하면 자동으로 호출되는 callback함수
        print("connect..."+str(rc))  # rc가 0이면 성공 접속, 1이면 실패
        if rc == 0:  # 연결이 성공하면 구독신청
            client.subscribe("iot/#")
            client.subscribe("web")
            client.subscribe("mydata/file")
            client.subscribe("android/product")
            client.subscribe("android/productAI")

        else:
            print("연결실패.....")

    # 라즈베리파이가 메시지를 받으면 호출되는 함수이므로 받은 메시지에 대한 처리를 구현
    def on_message(self, client, userdata, message):
        try:
            print("test~~~~~")
            print(message.topic+"-----")
            myval2 = message.topic.split("/")
            print(myval2[1])
            global count
            if myval2[1] == "file":
                count += 1
                f = open("/home/ec2-user/image/result/test{}.jpg".format(str(count)), "wb")
                f.write(message.payload)
                print("메시지 수신 완료 - 파일저장하기 완료")
                f.close()
                publisher.single("iot/picamera","okay", hostname="13.52.187.248")
            if myval2[1] == "product":
                count += 1
                print("안드로이드 수신완료")
                f = open("/home/ec2-user/image/product_image/{}product{}.jpg".format(now, count), "wb")
                data = base64.b64decode(message.payload)
                f.write(data)
                print("이미지 저장 완료")
                f.close()
                publisher.single("android/picture", "/image/product_image/{}product{}.jpg".format(now,count), hostname="13.52.187.248")


        except:
            pass
        finally:
            pass


# 테스트 작업을 위한 클래스
if __name__ == '__main__':
    try:
        mqtt = MqttWorker()
        mqtt.mymqtt_connect()  # callback 함수가 아니므로 인스턴스 변수를 이용해서 호출해야 한다.
        for i in range(10):
            print(i)

    except KeyboardInterrupt:
        pass

    finally:

        print("종료")
