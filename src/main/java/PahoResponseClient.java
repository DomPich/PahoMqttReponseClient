import org.eclipse.paho.client.mqttv3.*;

import java.util.logging.Logger;

import static org.eclipse.paho.client.mqttv3.MqttClient.generateClientId;

public class PahoResponseClient {

    private static final Logger log = Logger.getLogger(PahoResponseClient.class.getName());

    // TODO change this variables to fit your needs
    private static final String BROKER_URL="tcp://localhost:1883";
    private static final int MAX_RECONNECT_ATTEMPTS = 10;
    private static final String PUBLISHING_TOPIC = "my/publishing/topic";
    private static final String SUBSCRIPTION_TOPIC = "my/subscription/topic";
    private static final String RESPONSE_TOPIC = "my/response/topic";
    private static final String LAST_WILL_TOPIC = "my/lastwill/topic";
    private static final int QoS = 2;

    private MqttClient client;
    private MqttConnectOptions options;
    private String ID;

    public static void main(String[] args) {
        new PahoResponseClient().start();
    }

    /**
     * Initialize the connection, send a first message, set a callback and subscribe to a topic
     */
    private void start(){

        ID = "myFancyID";   // You can generate a random ID with the paho-built-in function generateClientId().

        try {
            client = new MqttClient(BROKER_URL, ID);

            // TODO change options to fit your needs
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setWill(LAST_WILL_TOPIC, ("Sorry, I died").getBytes(), 2, false);

            if(!connectToBroker()){
                log.warning("Could not connect to Broker after " + MAX_RECONNECT_ATTEMPTS + " attempts. " +
                        "Will exit now!");
                System.exit(1);
            }

            publishMessage(PUBLISHING_TOPIC, "I am alive");

            client.setCallback(new SubscriberCallback());

            client.subscribe(SUBSCRIPTION_TOPIC);


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the connection with the Broker
     */
    private void stop(){
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Publish a message
     *
     * @param topic The topic where the message is published
     * @param pubMsg The message to be published
     */
    private void publishMessage(String topic, String pubMsg){
        MqttDeliveryToken token;
        MqttTopic mqttTopic = client.getTopic(topic);

        MqttMessage message = new MqttMessage(pubMsg.getBytes());
        message.setQos(QoS);

        // Publish the message
        log.info("Publishing to topic:" + topic + ". QOS: " + QoS);

        try {
            // Publish to the broker
            token = mqttTopic.publish(new MqttMessage(pubMsg.getBytes()));
        } catch (Exception e) {
            log.warning("Publishing to topic:" + topic + ". QOS: " + QoS + "failed." + e.getCause());
        }

        log.info("Message sent");
    }

    /**
     * This method connects to the MQTT-Broker. It tries to do it {@link PahoResponseClient#MAX_RECONNECT_ATTEMPTS}
     * times before returning false.
     *
     * @return true, if connection-attempt was successful. False otherwise
     */
    private boolean connectToBroker(){

        log.info("Connect to Broker");

        for(int i = 0; i < MAX_RECONNECT_ATTEMPTS; i++){
            try {
                client.connect(options);
            } catch (MqttException e) {

                log.warning("Could not establish connection to Broker, because " + e.getCause());

                log.info("Trying to reconnect in 10 seconds. " + (MAX_RECONNECT_ATTEMPTS - i) + " attempts left");

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                continue;

            }

            log.info("Connection established");
            return true;
        }


        return false;
    }

    private class SubscriberCallback implements MqttCallback{

        /**
         * Called, if connection is lost. Client tries to reconnect
         *
         * @param cause
         */
        public void connectionLost(Throwable cause) {

            log.warning("Lost Connection." + cause.getCause());
            log.info("Trying to reconnect");

            if(!connectToBroker()){
                log.info("Could not reconnect. Will exit now!");
                System.exit(1);
            }

        }

        /**
         * Called, if client receives a message.
         *
         * @param topic
         * @param mqttMessage
         * @throws Exception
         */
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

            log.info("Message arrived: " + new String(mqttMessage.getPayload(), "UTF-8"));
            log.info("Starting Response");

            publishMessage(RESPONSE_TOPIC, "Hey, here is my response");

        }

        /**
         * Called, if message is delivered
         *
         * @param iMqttDeliveryToken
         */
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            log.info("Message with " + iMqttDeliveryToken + " delivered.");
        }
    }
}
