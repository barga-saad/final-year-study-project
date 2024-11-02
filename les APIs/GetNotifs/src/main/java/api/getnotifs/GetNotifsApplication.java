package api.getnotifs;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GetNotifsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GetNotifsApplication.class, args);
    }

}