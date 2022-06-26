from tkinter import OFF
import paho.mqtt.client as mqtt
from threading import Thread, Event
from time import sleep
import picamera
import paho.mqtt.publish as publisher
import RPi.GPIO as gpio
import time
from gpiozero import PWMOutputDevice

gpio.setmode(gpio.BCM)
gpio.setwarnings(False)

trig1 = 23
echo1 = 24
trig2 = 17
echo2 = 27
trig3 = 26
echo3 = 19
motor = PWMOutputDevice(12, active_high=True, frequency=100)
motor.off()

count1 = 0
count2 = 0

gpio.setup(trig1, gpio.OUT)
gpio.setup(echo1, gpio.IN)
gpio.setup(trig2, gpio.OUT)
gpio.setup(echo2, gpio.IN)
gpio.setup(trig3, gpio.OUT)
gpio.setup(echo3, gpio.IN)

gpio.output(trig1, False)
gpio.output(trig2, False)
gpio.output(trig3, False)

with picamera.PiCamera() as camera:
    camera.start_preview()
    time.sleep(3)

try:
    while True:
        gpio.output(trig1, True)
        time.sleep(0.4)
        gpio.output(trig1, False)

        while gpio.input(echo1) == 0:
            start1 = time.time()
        while gpio.input(echo1) == 1:
            stop1 = time.time()

        distance1 = (stop1 - start1) * 34300 / 2
        print("거리 1 : %.1f cm" % distance1)

        gpio.output(trig2, True)
        time.sleep(0.4)
        gpio.output(trig2, False)

        while gpio.input(echo2) == 0:
            start2 = time.time()
        while gpio.input(echo2) == 1:
            stop2 = time.time()

        distance2 = (stop2 - start2) * 34300 / 2
        print("거리 2 : %.1f cm" % distance2)

        gpio.output(trig3, True)
        time.sleep(0.5)
        gpio.output(trig3, False)

        while gpio.input(echo3) == 0:
            start3 = time.time()
        while gpio.input(echo3) == 1:
            stop3 = time.time()

        distance3 = (stop3 - start3) * 34300 / 2
        print("거리 3 : %.1f cm" % distance3)

        if distance2 < 50 or distance3 < 50:
            if count2 <= 5:
                count2 += 1
                motor.value = 1
                sleep(0.2)
                motor.value = 0
                sleep(0.3)
            else:
                count2 = 0
        else:
            pass

        if distance1 < 100:
            if count1 <= 5:
                count1 += 1
                with picamera.PiCamera() as camera:
                    sleep(0.5)
                    camera.rotation = 180
                    camera.brightness = 55
                    camera.awb_mode = 'auto'
                    camera.capture("/home/pi/mywork/test123.jpg")
                    file = open("/home/pi/mywork/test123.jpg", "rb")
                    filedata = file.read()
                    bytefiledata = bytearray(filedata)
                    publisher.single("mydata/file", bytefiledata, hostname="13.52.187.248")
                    print("전송완료")
                    for i in range(1, 3):
                        motor.value = 1
                        sleep(0.2)
                        motor.value = 0
                        sleep(0.2)
            else:
                count1 = 0
        if distance1 >= 100:
            sleep(1.4)


except KeyboardInterrupt:
    with picamera.PiCamera() as camera:
        camera.stop_preview()
        print("카메라 정지")
    sleep(0.2)
    pass
finally:
    print("종료")
    gpio.cleanup()