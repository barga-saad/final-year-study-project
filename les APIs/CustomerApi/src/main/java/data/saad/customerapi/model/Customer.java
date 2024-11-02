package data.saad.customerapi.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "costumer_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCustomer;

    @Column(length = 45)
    private String nom;

    @Column(length = 45)
    private String prenom;

    @Column(length = 45)
    private String legalId;

    private int legalDocName;

    @Column(length = 45)
    private String gender;

    private int accountOfficerId;
    private int customerStatus;

    @Column(length = 45)
    private String country;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(length = 45)
    private String ville;

    @Lob
    private String adress;

    @Column(length = 45)
    private String codeTribunal;

    @Column(length = 45)
    private String mnemonic;

}
