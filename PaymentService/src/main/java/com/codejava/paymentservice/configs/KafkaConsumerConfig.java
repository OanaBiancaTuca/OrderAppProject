package com.codejava.paymentservice.configs;

import com.codejava.paymentservice.errors.NotRetryableException;
import com.codejava.paymentservice.errors.RetryableException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    Environment environment;

    @Autowired
    public KafkaConsumerConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public ConsumerFactory<Long, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        //when errorhandling the serializer class is used as a value deserializer then it will use this json serializer  class to deserializer message payload
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"));
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<Long, Object> kafkaListenerContainerFactory(
            ConsumerFactory<Long, Object> consumerFactory, KafkaTemplate<Long, Object> kafkaTemplateDLT) {

        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplateDLT),
                new FixedBackOff(5000, 3));

        defaultErrorHandler.addNotRetryableExceptions(NotRetryableException.class);
        defaultErrorHandler.addRetryableExceptions(RetryableException.class);

        ConcurrentKafkaListenerContainerFactory<Long, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(defaultErrorHandler); //this means that error occurs durring message consumption by Kafka
        //listener, the default error handler listener will be used to handle the error
        return factory;
    }

    //DEAD LETTER TOPIC
    @Bean
    KafkaTemplate<Long, Object> kafkaTemplateDLT(ProducerFactory<Long, Object> producerFactoryDLT) {
        return new KafkaTemplate<>(producerFactoryDLT);
    }

    @Bean
    ProducerFactory<Long, Object> producerFactoryDLT() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }


}
