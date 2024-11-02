/*package api.getnotifs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import api.getnotifs.service.NotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications")
    public List<Map<String, Object>> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/notifications/{idAccount}")
    public List<Map<String, Object>> getNotificationsByAccountId(@PathVariable Long idAccount) {
        return notificationService.getNotificationsByIdAccount(idAccount);
    }
}
*/