/*package api.getnotifs.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getAllNotifications() {
        return jdbcTemplate.queryForList("SELECT * FROM kafka_message");
    }

    public List<Map<String, Object>> getNotificationsByIdAccount(Long idAccount) {
        String sql = "SELECT * FROM kafka_message WHERE id_Account = ?";
        return jdbcTemplate.queryForList(sql, new Object[]{idAccount});
    }



    public void sendFirebaseNotification(String token, String message) {
        Message msg = Message.builder()
                .putData("message", message)
                .setToken(token)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(msg);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            System.out.println("Failed to send message: " + e.getMessage());
        }
    }





}*/
