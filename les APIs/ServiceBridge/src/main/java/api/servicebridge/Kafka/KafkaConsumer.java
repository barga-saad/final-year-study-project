package api.servicebridge.Kafka;


import api.servicebridge.Model.KafkaMessage;
import api.servicebridge.Repository.KafkaMessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private KafkaMessageRepository kafkaMessageRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "accountTopic", groupId = "myGroup")
    public void consume(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String messageContent = jsonNode.get("message").asText();
            String email = jsonNode.get("email").asText();
            String telephone = jsonNode.get("telephone").asText();
            Long idAccount = jsonNode.get("idAccount").asLong();

            KafkaMessage kafkaMessage = new KafkaMessage(message, email ,telephone, idAccount);
            kafkaMessageRepository.save(kafkaMessage);
            LOGGER.info(String.format("Message saved -> %s", message));
        } catch (Exception e) {
            LOGGER.error("Failed to parse and save message", e);
        }
    }
}

