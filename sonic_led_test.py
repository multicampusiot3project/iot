import paho.mqtt.client as mqtt
import RPi.GPIO as gpio
import time

gpio.setmode(gpio.BCM)
gpio.setwarnings(False)

trig = 23
echo = 24
led1 = 25

gpio.setup(trig, gpio.OUT)
gpio.setup(echo, gpio.IN)
gpio.setup(led1, gpio.OUT)

gpio.output(trig, False)
time.sleep(5)

try:
    while True:
        gpio.output(trig, True)
        time.sleep(0.001)
        gpio.output(trig, False)

        while gpio.input(echo) == 0:
            start = time.time()
        while gpio.input(echo) == 1:
            stop = time.time()

        distance = (stop - start) * 34300 / 2
        print("거리 : %.1f cm" % distance)

        if distance < 100:
            gpio.output(led1, gpio.HIGH)
        if distance >= 100:
            gpio.output(led1, gpio.LOW)
        time.sleep(1)
except KeyboardInterrupt:
    print("종료")
    gpio.cleanup()