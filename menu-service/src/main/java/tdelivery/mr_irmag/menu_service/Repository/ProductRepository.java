package tdelivery.mr_irmag.menu_service.Repository;


import tdelivery.mr_irmag.menu_service.Domain.Entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, Long> {
    public Optional<Product> findById(Long id);
    public Product findByName(String name);
    public List<Product> findByPrice(double price);
    public void deleteById(Long id);
}
