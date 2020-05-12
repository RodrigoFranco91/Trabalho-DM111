package br.com.rodrigo.trabalho_dm111.repository;

import br.com.rodrigo.trabalho_dm111.exception.UserAlreadyExistsException;
import br.com.rodrigo.trabalho_dm111.exception.UserNotFoundException;
import br.com.rodrigo.trabalho_dm111.model.DesiredProducts;
import br.com.rodrigo.trabalho_dm111.model.User;
import com.google.appengine.api.datastore.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class DesiredProductsRepository {

    private static final Logger log = Logger.getLogger("DesiredProductsRepository");

    private static final String DESIREDPRODUCTS_KIND = "DesiredProducts";
    private static final String DESIREDPRODUCTS_KEY = "desiredProductsKeys";
    private static final String PROPERTY_ID = "id";
    private static final String PROPERTY_CPF = "cpf";
    private static final String PROPERTY_SALE_ID = "saleId";
    private static final String PROPERTY_PRODUCT_ID = "productId";
    private static final String PROPERTY_DESIRED_PRICE = "desiredPrice";

    private void desiredProductsToEntity(DesiredProducts desiredProducts, Entity desiredProductsEntity) {
        desiredProductsEntity.setProperty(PROPERTY_ID, desiredProducts.getId());
        desiredProductsEntity.setProperty(PROPERTY_CPF, desiredProducts.getCpf());
        desiredProductsEntity.setProperty(PROPERTY_SALE_ID, desiredProducts.getSaleId());
        desiredProductsEntity.setProperty(PROPERTY_PRODUCT_ID, desiredProducts.getProductId());
        desiredProductsEntity.setProperty(PROPERTY_DESIRED_PRICE, desiredProducts.getDesiredPrice());

    }

    private DesiredProducts entityToDesiredProducts(Entity desiredProductsEntity) {
        DesiredProducts dp = new DesiredProducts();
        dp.setId(desiredProductsEntity.getKey().getId());
        dp.setCpf((String) desiredProductsEntity.getProperty(PROPERTY_CPF));
        dp.setSaleId((Long) desiredProductsEntity.getProperty(PROPERTY_SALE_ID));
        dp.setProductId((Long) desiredProductsEntity.getProperty(PROPERTY_PRODUCT_ID));
        dp.setDesiredPrice((Double) desiredProductsEntity.getProperty(PROPERTY_DESIRED_PRICE));
        return dp;
    }

    public DesiredProducts save(DesiredProducts dp) throws UserNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        UserRepository rp = new UserRepository();
        Optional<User> user = rp.getByCPF(dp.getCpf());
        if (user.isPresent()) {
            Key desiredProductsKey = KeyFactory.createKey(DESIREDPRODUCTS_KIND, DESIREDPRODUCTS_KEY);
            Entity desiredProductsEntity = new Entity(DESIREDPRODUCTS_KIND, desiredProductsKey);
            desiredProductsToEntity(dp, desiredProductsEntity);
            datastore.put(desiredProductsEntity);
            dp.setId(desiredProductsEntity.getKey().getId());
            return dp;
        } else {
            throw new UserNotFoundException("Usuário não existe");
        }
    }
}
