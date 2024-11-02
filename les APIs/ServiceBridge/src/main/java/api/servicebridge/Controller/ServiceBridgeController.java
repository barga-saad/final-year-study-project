package api.servicebridge.Controller;

import api.servicebridge.Model.KafkaMessage;
import api.servicebridge.Repository.KafkaMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;



@RestController
@RequestMapping("/Notifications")
public class ServiceBridgeController {

    @Autowired
    private KafkaMessageRepository kafkaMessageRepository;


    @GetMapping
    public ResponseEntity<List<KafkaMessage>> getAllMessages() {
        List<KafkaMessage> kafkaMessages = kafkaMessageRepository.findAll();

        // Log the retrieved messages
        System.out.println("Retrieved KafkaMessages: " + kafkaMessages);

        if (kafkaMessages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(kafkaMessages);
    }




}