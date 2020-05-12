package br.com.rodrigo.trabalho_dm111.controller;

import br.com.rodrigo.trabalho_dm111.exception.UserAlreadyExistsException;
import br.com.rodrigo.trabalho_dm111.exception.UserNotFoundException;
import br.com.rodrigo.trabalho_dm111.model.DesiredProducts;
import br.com.rodrigo.trabalho_dm111.model.User;
import br.com.rodrigo.trabalho_dm111.repository.DesiredProductsRepository;
import br.com.rodrigo.trabalho_dm111.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping(path="/api/desiredproducts")
public class DesiredProductsController {

    private static final Logger log = Logger.getLogger("UserController");

    @Autowired
    private DesiredProductsRepository desiredProductsRepository;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DesiredProducts> saveDesiredProducts(@RequestBody DesiredProducts desiredProducts) {
        try {
            return new ResponseEntity<DesiredProducts>(desiredProductsRepository.save(desiredProducts), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
    }
}
