package com.codejava.feedbackservice.services;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DLTFeedbackService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    public void sendToDLT(ConsumerRecord<?,?>record){
        kafkaTemplate.send("feedback-topic.DLT",record.key().toString(), record.value().toString());
    }
}
