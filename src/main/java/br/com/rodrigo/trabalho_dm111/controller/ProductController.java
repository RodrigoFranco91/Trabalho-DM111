package br.com.rodrigo.trabalho_dm111.controller;

import br.com.rodrigo.trabalho_dm111.model.User;
import br.com.rodrigo.trabalho_dm111.repository.DesiredProductsRepository;
import br.com.rodrigo.trabalho_dm111.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/product")
public class ProductController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DesiredProductsRepository desiredProductsRepository;

    @Autowired
    private OrderMessageController orderMessageController;

    @Autowired
    private ObjectMapper objectMapper;

    @PutMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> updateProduct(@RequestParam("productId") Long productId, @RequestParam("price") Double price) throws JsonProcessingException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter filterByProductId = new Query.FilterPredicate("productId", Query.FilterOperator.EQUAL, productId);
        Query query = new Query("DesiredProducts").setFilter(filterByProductId);
        List<Entity> desiredProductsEntity = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        List<User> userList = new ArrayList<User>();
        for (Entity aux : desiredProductsEntity) {
            User user;
            Double oldPrice = (Double) aux.getProperty("desiredPrice");
            if (price <= oldPrice) {
                user = userRepository.getByCPF((String) aux.getProperty("cpf")).get();
                userList.add(user);
            }
        }
        return orderMessageController.sendMessagePrice(userList, productId, price);
    }
}