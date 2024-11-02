package api.servicebridge.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessageDTO {
    private String message;
    private String email;
    private String telephone;
    private Long idAccount;


    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("telephone")
    public String getTelephone() { return telephone; }

    public void setTelephone(String telephone) { this.telephone = telephone; }

    @JsonProperty("idAccount")
    public Long getIdAccount() { return idAccount; }

    public void setIdAccount(Long idAccount) { this.idAccount = idAccount; }


}
