package api.account.Controller;


import api.account.Exception.ResourceNotFoundException;
import api.account.Model.Account;
import api.account.Model.AccountCreationMessage;
import api.account.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;


    @GetMapping
    public List<Account> getAllAccounts(){

        return accountRepository.findAll();
    }

    @GetMapping("{idAccount}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long idAccount){
        Account account = accountRepository.findById(idAccount).orElseThrow(() -> new ResourceNotFoundException("Account not exist with id:" + idAccount));
        return ok(account);
    }

    @PostMapping("/creataccount")
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        Account savedAccount = accountRepository.save(account);

        String messageText = String.format("Hello , we are happy to inform you that the creation of your account with ID %d has been successful. You will receive a mail at the following address: %s and a message in your phone number : %s",
                 savedAccount.getIdAccount(), savedAccount.getEmail(),savedAccount.getTelephone());

        AccountCreationMessage message = new AccountCreationMessage(messageText, savedAccount.getEmail() ,savedAccount.getTelephone() ,savedAccount.getIdAccount() );

        kafkaTemplate.send("accountTopic-topic", message);

        return ResponseEntity.ok(messageText);
    }

    @PutMapping("/{idAccount}")
    public ResponseEntity<String> updateAccount(@PathVariable Long idAccount, @RequestBody Account accountDetails) {
        Account account = accountRepository.findById(idAccount)
                .orElseThrow(() -> new ResourceNotFoundException("Account not exist with id: " + idAccount));

        account.setIdCustomer(accountDetails.getIdCustomer());
        account.setCustomer_radical(accountDetails.getCustomer_radical());
        account.setCompagnecode(accountDetails.getCompagnecode());
        account.setDevice(accountDetails.getDevice());
        account.setCategorie(accountDetails.getCategorie());
        account.setDate_ouverture(accountDetails.getDate_ouverture());
        account.setRIB(accountDetails.getRIB());
        account.setCodeagence(accountDetails.getCodeagence());
        account.setSolde(accountDetails.getSolde());
        account.setEmail(accountDetails.getEmail());
        account.setTelephone(accountDetails.getTelephone());

        Account updatedAccount = accountRepository.save(account);

        String messageText = String.format("Hello, your account with ID %d has been successfully updated. Please verify your updated details in your email: %s and phone number: %s",
                updatedAccount.getIdAccount(), updatedAccount.getEmail(), updatedAccount.getTelephone());

        kafkaTemplate.send("accountTopic", new AccountCreationMessage(messageText, updatedAccount.getEmail(), updatedAccount.getTelephone(), updatedAccount.getIdAccount()));

        return ResponseEntity.ok(messageText);
    }

    @DeleteMapping("/{idAccount}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long idAccount) {
        Account account = accountRepository.findById(idAccount)
                .orElseThrow(() -> new ResourceNotFoundException("Account not exist with id: " + idAccount));

        accountRepository.delete(account);

        String messageText = String.format("Hello, your account with ID %d has been successfully deleted.", account.getIdAccount());

        kafkaTemplate.send("accountTopic", new AccountCreationMessage(messageText, account.getEmail(), account.getTelephone(), account.getIdAccount()));

        return ResponseEntity.ok(messageText);
    }


}
