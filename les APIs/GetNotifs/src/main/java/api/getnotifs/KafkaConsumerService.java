package api.getnotifs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    private FirebaseService firebaseService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "accountTopic", groupId = "myGroup")
    public void listen(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String notificationMessage = jsonNode.get("message").asText();
            System.out.println("Received Message in group myGroup: " + notificationMessage);
            firebaseService.sendNotificationToFirebase(notificationMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

