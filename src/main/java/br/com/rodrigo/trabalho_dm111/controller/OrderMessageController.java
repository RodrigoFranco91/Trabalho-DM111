package br.com.rodrigo.trabalho_dm111.controller;

import br.com.rodrigo.trabalho_dm111.model.OrderMessage;
import br.com.rodrigo.trabalho_dm111.model.User;
import br.com.rodrigo.trabalho_dm111.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/api/ordermessage")
public class OrderMessageController {

    private static final Logger log = Logger.getLogger("MessageController");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .setDatabaseUrl("https://dm111homework.firebaseio.com").build();
            FirebaseApp.initializeApp(options);
            log.info("FirebaseApp configurado");
        } catch (IOException e) {
            log.info("Falha ao configurar FirebaseApp");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/sendmessage")
    public ResponseEntity<OrderMessage> sendMessage(@RequestBody OrderMessage orderMessage) {
        Optional<User> optUser = userRepository.getByCPF(orderMessage.getCpf());
        if (optUser.isPresent()) {
            User user = optUser.get();
            String registrationToken = user.getFcmRegId();
            try {
                Message message = Message.builder()
                        .putData("product", objectMapper.writeValueAsString(orderMessage))
                        .setToken(registrationToken)
                        .build();

                String response = FirebaseMessaging.getInstance().send(message);
                log.info("Mensagem enviada ao usuario " + user.getUsername());
                log.info("Reposta do FCM: " + response);
                return new ResponseEntity<OrderMessage>(orderMessage, HttpStatus.OK);
            } catch (FirebaseMessagingException | JsonProcessingException e) {
                log.severe("Falha ao enviar mensagem pelo FCM: " + e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            log.severe("Usuário não encontrado");
            return new ResponseEntity<OrderMessage>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<List<String>> sendMessagePrice(List<User> userList, Long productId, Double price) {
        List<String> sentMessages = new ArrayList<>();
        String textUpdateProduct = "O produto " + productId + " teve seu preço alterado para " + price;
        sentMessages.add(textUpdateProduct);
        String textUser = "Mensagens enviadas:";
        sentMessages.add(textUser);
        for (User aux : userList) {
            String txt = "Olá " + aux.getUsername() + " o produto " + productId + " agora custa " + price;
            String registrationToken = aux.getFcmRegId();
            try {
                Message message = Message.builder()
                        .putData("product", objectMapper.writeValueAsString("txt"))
                        .setToken(registrationToken)
                        .build();

                String response = FirebaseMessaging.getInstance().send(message);
                log.info("Mensagem enviada ao usuario " + aux.getUsername());
                log.info("Reposta do FCM: " + response);
                sentMessages.add(txt);
            } catch (FirebaseMessagingException | JsonProcessingException e) {
                log.severe("Falha ao enviar mensagem pelo FCM: " + e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<List<String>>(sentMessages, HttpStatus.OK);
    }

}