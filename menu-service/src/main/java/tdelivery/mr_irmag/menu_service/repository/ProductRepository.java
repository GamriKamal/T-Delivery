package tdelivery.mr_irmag.menu_service.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tdelivery.mr_irmag.menu_service.domain.Entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findById(String id);

    Product findByName(String name);

    List<Product> findByPrice(double price);

    void deleteById(Long id);
}
