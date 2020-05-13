package br.com.rodrigo.trabalho_dm111.controller;

import br.com.rodrigo.trabalho_dm111.exception.UserAlreadyExistsException;
import br.com.rodrigo.trabalho_dm111.exception.UserNotFoundException;
import br.com.rodrigo.trabalho_dm111.model.DesiredProducts;
import br.com.rodrigo.trabalho_dm111.model.User;
import br.com.rodrigo.trabalho_dm111.repository.DesiredProductsRepository;
import br.com.rodrigo.trabalho_dm111.repository.UserRepository;
import br.com.rodrigo.trabalho_dm111.util.CheckRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/api/desiredproducts")
public class DesiredProductsController {

    private static final Logger log = Logger.getLogger("UserController");

    @Autowired
    private DesiredProductsRepository desiredProductsRepository;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DesiredProducts> saveDesiredProducts(@RequestBody DesiredProducts desiredProducts, Authentication authentication) {
        boolean hasRoleAdmin = CheckRole.hasRoleAdmin(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> requester = userRepository.getByCPF(desiredProducts.getCpf());
        if (requester.isPresent()) {
            if (hasRoleAdmin || userDetails.getUsername().equals(requester.get().getEmail())) {
                try {
                    return new ResponseEntity<DesiredProducts>(desiredProductsRepository.save(desiredProducts), HttpStatus.OK);
                } catch (UserNotFoundException e) {
                    return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(path = "/byCpf")
    public ResponseEntity<List<DesiredProducts>> getDesiredProductsByCpf(Authentication authentication, @RequestParam("cpf") String cpf) {
        boolean hasRoleAdmin = CheckRole.hasRoleAdmin(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> requester = userRepository.getByCPF(cpf);
        if (requester.isPresent()) {
            if (hasRoleAdmin || userDetails.getUsername().equals(requester.get().getEmail())) {
                try {
                    return new ResponseEntity<List<DesiredProducts>>(desiredProductsRepository.getDesiredProducts(cpf), HttpStatus.OK);
                } catch (UserNotFoundException e) {
                    return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping
    public ResponseEntity<DesiredProducts> deleteUser(@RequestParam("cpf") String cpf, @RequestParam("produtoId") Long productId, Authentication authentication) {
        try {
            boolean hasRoleAdmin = CheckRole.hasRoleAdmin(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<User> user = userRepository.getByCPF(cpf);
            if (hasRoleAdmin || userDetails.getUsername().equals(user.get().getEmail())) {
                return new ResponseEntity<DesiredProducts>(desiredProductsRepository.deleteDesiredProducts(cpf, productId), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
