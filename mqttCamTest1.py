import paho.mqtt.client as mqtt
from threading import Thread, Event
from time import sleep
import picamera
import paho.mqtt.publish as publisher


# camera.hflip=True
# camera.vflip=True
# camera.resolution=(1280,720)
# camera.framerate=15
class MqttWorker:
    def __init__(self):
        self.client = mqtt.Client()
        self.client.on_connect = self.on_connect

        self.exit_event = Event()

        # self.HC_SR04 = HC_SR04(self.client)
        # self.HC_SR04.start()

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
            with picamera.PiCamera() as camera:
                camera.start_preview
                camera.hflip
                camera.vflip
                sleep(5)
                camera.capture("/home/pi/mywork/projecttest1/test123.jpg")
                camera.stop_preview()
                file = open("/home/pi/mywork/projecttest1/test123.jpg", "rb")
                filedata = file.read()
                bytefiledata = bytearray(filedata)

            publisher.single("mydata/file", bytefiledata, hostname="13.52.187.248")
        except KeyboardInterrupt:
            pass
        finally:
            print("종료")

    def on_connect(self, client, userdata, flags, rc):  # broker접속에 성공하면 자동으로 호출되는 callback함수
        print("connect..." + str(rc))  # rc가 0이면 성공 접속, 1이면 실패
        if rc == 0:  # 연결이 성공하면 구독신청
            client.subscribe("iot/#")
        else:
            print("연결실패.....")


# 테스트 작업을 위한 클래스
if __name__ == '__main__':
    try:
        mqtt = MqttWorker()
        mqtt.mymqtt_connect()

    except:
        pass

    finally:
        print("@@@@@@@@@@@@@@@")
