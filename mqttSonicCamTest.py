import paho.mqtt.client as mqtt
from threading import Thread, Event
from time import sleep
import picamera
import paho.mqtt.publish as publisher
import RPi.GPIO as gpio
import time

gpio.setmode(gpio.BCM)
gpio.setwarnings(False)

trig1 = 23
echo1 = 24
led1 = 25
trig2 = 17
echo2 = 27
led2 = 22

count1 = 0
count2 = 0

gpio.setup(trig1, gpio.OUT)
gpio.setup(echo1, gpio.IN)
gpio.setup(led1, gpio.OUT)
gpio.setup(trig2, gpio.OUT)
gpio.setup(echo2, gpio.IN)
gpio.setup(led2, gpio.OUT)

gpio.output(trig1, False)
gpio.output(trig2, False)

with picamera.PiCamera() as camera:
    camera.start_preview()
time.sleep(3)

try:
    while True:
        gpio.output(trig1, True)
        time.sleep(0.5)
        gpio.output(trig1, False)
        
        while gpio.input(echo1) == 0:
            start1 = time.time()
        while gpio.input(echo1) == 1:
            stop1 = time.time()
        
        gpio.output(trig2, True)
        time.sleep(0.5)
        gpio.output(trig2, False)
        
        while gpio.input(echo2) == 0:
            start2 = time.time()
        while gpio.input(echo2) == 1:
            stop2 = time.time()

        distance1 = (stop1 - start1) * 34300 / 2
        print("거리 1 : %.1f cm" % distance1)
        
        distance2 = (stop2 - start2) * 34300 / 2
        print("거리 2 : %.1f cm" % distance2)
        
        if distance2 <50:
            gpio.output(led2, gpio.LOW)
            if count2 <= 5:
                count2 +=1
            else:
                count2=0
        if distance2 >= 50:
            gpio.output(led2,gpio.HIGH)
        
        if distance1 < 100:
            gpio.output(led1, gpio.LOW)

            if count1 <= 5:
                count1 += 1
                with picamera.PiCamera() as camera:
                    camera.hflip
                    camera.vflip
                    camera.capture("/home/pi/mywork/test123.jpg")
                    file = open("/home/pi/mywork/test123.jpg", "rb")
                    filedata = file.read()
                    bytefiledata = bytearray(filedata)
                    publisher.single("mydata/file", bytefiledata, hostname="13.52.187.248")
            else:
                count1 = 0
        if distance1 >= 100:
            print
            gpio.output(led1, gpio.HIGH)
            sleep(1.4)
        
    
except KeyboardInterrupt:
    pass
finally:
    with picamera.Picamera() as camera:
        camera.stop_preview()
        print("카메라 정지")
    sleep(0.2)
    print("종료")
    gpio.cleanup()