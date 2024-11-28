package tdelivery.mr_irmag.menu_service.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tdelivery.mr_irmag.menu_service.domain.Entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    public Optional<Product> findById(String id);
    public Product findByName(String name);
    public List<Product> findByPrice(double price);
    public void deleteById(Long id);
}
