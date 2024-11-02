package api.account.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.Text;


import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "account_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAccount;

    private int idCustomer;

    private int customer_radical;

    @Column(length = 45)
    private String compagnecode;

    @Column(length = 45)
    private String device;

    private int categorie;

    @Temporal(TemporalType.DATE)
    private Date date_ouverture;

    @Column(length = 45)
    private String RIB;

    private int codeagence;

    private int solde;

    @Column(length = 225)
    private String email;

    @Column(length = 45)
    private String telephone;

}
