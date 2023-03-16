package cn.bywin.business.util;//package cn.bywin.business.util;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.config.KafkaListenerContainerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaConfig {
//
//    @Value("${sourceServer}")
//    private String sourceServer;
//    @Value("${sourceGroup}")
//    private String sourceGroup;
//
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> propsMap = new HashMap<>();
//        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, sourceServer);
//        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
//        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
//        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, sourceGroup);
//
//        return propsMap;
//    }
//
//
//    @Bean(name = "cooperation")
//    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactorya() {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        DefaultKafkaConsumerFactory kafkaConsumerFactory = new DefaultKafkaConsumerFactory(consumerConfigs());
//        factory.setConsumerFactory(kafkaConsumerFactory);
//        factory.setConcurrency(4);
//        factory.setBatchListener(true);
//        factory.getContainerProperties().setPollTimeout(3000);
//        return factory;
//    }
//
//    public ProducerFactory<String, String> producerFactory() {
//
//        DefaultKafkaProducerFactory kafkaConsumerFactory = new DefaultKafkaProducerFactory(producerConfigs());
//
//        return kafkaConsumerFactory;
//    }
//    @Bean
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> propsMap = new HashMap<>();
//        propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, sourceServer);
//        propsMap.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
//        propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        return propsMap;
//    }
//
//
//    @Bean
//    public KafkaTemplate<String, String> kafkaTemplate() {
//        return new KafkaTemplate<String, String>(producerFactory());
//    }
//
//}
