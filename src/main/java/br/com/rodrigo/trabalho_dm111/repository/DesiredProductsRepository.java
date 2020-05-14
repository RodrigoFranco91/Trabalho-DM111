package br.com.rodrigo.trabalho_dm111.repository;

import br.com.rodrigo.trabalho_dm111.exception.UserNotFoundException;
import br.com.rodrigo.trabalho_dm111.model.DesiredProducts;
import br.com.rodrigo.trabalho_dm111.model.User;
import com.google.appengine.api.datastore.*;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
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

    private Entity getByProductIdAndCpf(String cpf, Long productId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter filterByCpf = new Query.FilterPredicate(PROPERTY_CPF, Query.FilterOperator.EQUAL, cpf);
        Query.Filter filterByProductId = new Query.FilterPredicate(PROPERTY_PRODUCT_ID, Query.FilterOperator.EQUAL, productId);
        Query.Filter filterUnion = Query.CompositeFilterOperator.and(filterByCpf, filterByProductId);
        Query query = new Query(DESIREDPRODUCTS_KIND).setFilter(filterUnion);
        Entity desariedProduct = datastore.prepare(query).asSingleEntity();
        return desariedProduct;
    }

    public DesiredProducts save(DesiredProducts dp) throws UserNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        UserRepository rp = new UserRepository();
        Optional<User> user = rp.getByCPF(dp.getCpf());
        if (user.isPresent()) {
            Entity desiredProductEntity = getByProductIdAndCpf(dp.getCpf(), dp.getProductId());
            if (desiredProductEntity == null) {
                Key desiredProductsKey = KeyFactory.createKey(DESIREDPRODUCTS_KIND, DESIREDPRODUCTS_KEY);
                Entity desiredProductsEntity = new Entity(DESIREDPRODUCTS_KIND, desiredProductsKey);
                desiredProductsToEntity(dp, desiredProductsEntity);
                datastore.put(desiredProductsEntity);
                dp.setId(desiredProductsEntity.getKey().getId());
                return dp;
            } else {
                desiredProductEntity.setProperty(PROPERTY_DESIRED_PRICE, dp.getDesiredPrice());
                datastore.put(desiredProductEntity);
                return entityToDesiredProducts(desiredProductEntity);
            }
        } else {
            throw new UserNotFoundException("Usuário não existe");
        }
    }

    public List<DesiredProducts> getDesiredProducts(String cpf) throws UserNotFoundException {
        List<DesiredProducts> desiredProductsList = new ArrayList<>();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter filterByCpf = new Query.FilterPredicate(PROPERTY_CPF, Query.FilterOperator.EQUAL, cpf);
        Query query = new Query(DESIREDPRODUCTS_KIND).setFilter(filterByCpf);
        List<Entity> allDesiredProduct = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for (Entity desiredProduct : allDesiredProduct) {
            DesiredProducts item = entityToDesiredProducts(desiredProduct);
            desiredProductsList.add(item);
        }
        return desiredProductsList;
    }

    public DesiredProducts deleteDesiredProducts(String cpf, Long productId) throws UserNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        UserRepository rp = new UserRepository();
        Optional<User> user = rp.getByCPF(cpf);
        if (user.isPresent()) {
            Entity desiredProductEntity = getByProductIdAndCpf(cpf, productId);
            if (desiredProductEntity == null) {
                throw new UserNotFoundException("Produto de Interesse não existe");
            } else {
                datastore.delete(desiredProductEntity.getKey());
                return entityToDesiredProducts(desiredProductEntity);
            }
        } else {
            throw new UserNotFoundException("Usuário não existe");
        }
    }
}
