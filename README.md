Simple Paho MQTT Client that responds in the messageArrived(...) callback
=============

I've struggled a lot finding a way on how my mqtt-client can respond to a message from within the messageArrived(...) callback.

My problem was, that the publish(...) inside messageArrived() blocked.

So here is the way I've done it.

Sorry that there is no fancy readme...

I've added some TODOs. You should change this parts to fit your needs.

I hope it is useful.

How to start/test it
-------------------

All below commands are executed in OS X 10.11.4

1. **Install a broker**. 
I've used Mosquitto (http://mosquitto.org/download/)
Run *brew install mosquitto* from within the terminal.

2. **Start the mosquitto broker**: 
Run */usr/local/sbin/mosquitto -c /usr/local/etc/mosquitto/mosquitto.conf* from within a terminal.

3. **Start and Test**

* Run *mosquitto_sub -t "my/response/topic" -v* from within another terminal
* Run *mosquitto_sub -t "my/publishing/topic" -v* from within another terminal
* Run the *main()* Method in PahoResponseClient
* Run *mosquitto_pub -t "my/subscription/topic" -m "Hello World"* from within another terminal. Now the PahoResponseClient should invode the messageArrived() method, print out that it has received a message and publish a message to the reponse topic.


Test - Environment
------------------

OS X 10.11.4

Broker: mosquitto version 1.4.8 (build date 2016-02-14 11:22:37-0800)
