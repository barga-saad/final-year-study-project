package api.servicebridge.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@JsonIgnoreProperties(ignoreUnknown = true)

@Entity
public class KafkaMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private String email;
    private String telephone;
    private Long idAccount;


    public KafkaMessage() {
    }


    public KafkaMessage(String message, String email , String telephone ,Long idAccount) {
        this.message = message;
        this.email = email;
        this.telephone = telephone;
        this.idAccount = idAccount;
    }


}

