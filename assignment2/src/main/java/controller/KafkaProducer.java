package controller;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.springframework.stereotype.Component;

import java.util.Properties;
/**
 * Created by aravind on 4/15/15.
 */
@Component
public class KafkaProducer {

    private static Producer<Integer, String> producer;
    private final Properties properties = new Properties();
    String sjsuID = "010056956";
    String topic = "cmpe273-topic";

    public KafkaProducer() {
        properties.put("metadata.broker.list", "54.149.84.25:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");
        producer = new Producer<Integer, String>(new ProducerConfig(properties));
    }

    public void sendExpiredMessage(String email, int[] results) {
        String str1 = "[Android="+String.valueOf(results[0])+",iPhone="+String.valueOf(results[1])+"]";
        String msg = email+":"+sjsuID+":"+"Poll Result "+ str1;
        KeyedMessage<Integer, String> data = new KeyedMessage<Integer, String>(topic, msg);
        producer.send(data);

    }

    public void closeProducer(){
        producer.close();
    }
}