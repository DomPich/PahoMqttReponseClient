Simple Paho MQTT Client that responds in the messageArrived(...) callback
=============

I've struggled a lot finding a way on how my mqtt-client can respond to a message from within the messageArrived(...) callback.

My problem was, that the publish(...) inside messageArrived() blocked.

So here is the way I've done it.

Sorry that there is no fancy readme...

I've added some TODOs. You should change this parts to fit your needs.

I hope it is useful.


Test - Environment
------------------

OS X 10.11.4

Broker: mosquitto version 1.4.8 (build date 2016-02-14 11:22:37-0800)