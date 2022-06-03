import paho.mqtt.client as mqtt
from threading import Thread, Event
from time import sleep
import picamera
import paho.mqtt.publish as publisher
import RPi.GPIO as gpio
import time

gpio.setmode(gpio.BCM)
gpio.setwarnings(False)

trig = 23
echo = 24
led1 = 25

count = 1

gpio.setup(trig, gpio.OUT)
gpio.setup(echo, gpio.IN)
gpio.setup(led1, gpio.OUT)

gpio.output(trig, False)
time.sleep(5)

try:
    while True:
        gpio.output(trig, True)
        time.sleep(1)
        gpio.output(trig, False)

        while gpio.input(echo) == 0:
            start = time.time()
        while gpio.input(echo) == 1:
            stop = time.time()

        distance = (stop - start) * 34300 / 2
        print("거리 : %.1f cm" % distance)

        if distance < 100:
            gpio.output(led1, gpio.LOW)

            if count <= 5:
                count += 1
                with picamera.PiCamera() as camera:
                    camera.start_preview
                    camera.hflip
                    camera.vflip
                    sleep(1)
                    camera.capture("/home/pi/mywork/projecttest1/test123.jpg")
                    camera.stop_preview()
                    file = open("/home/pi/mywork/projecttest1/test123.jpg", "rb")
                    filedata = file.read()
                    bytefiledata = bytearray(filedata)
                    publisher.single("mydata/file", bytefiledata, hostname="13.52.187.248")
            else:
                sleep(1)
                count = 1
        if distance >= 100:
            print
            gpio.output(led1, gpio.HIGH)
            sleep(1)
except KeyboardInterrupt:
    print("종료")
    gpio.cleanup()