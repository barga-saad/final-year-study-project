package api.account.Model;

public class AccountCreationMessage {
    private String message;
    private String email;
    private String telephone;
    private Long idAccount;

    public AccountCreationMessage(String message, String email ,String telephone ,Long idAccount) {
        this.message = message;
        this.email = email;
        this.telephone = telephone;
        this.idAccount = idAccount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Long getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(Long idAccount) {
        this.idAccount = idAccount;
    }
}
