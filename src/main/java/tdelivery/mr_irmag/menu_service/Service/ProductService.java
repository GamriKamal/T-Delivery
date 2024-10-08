package tdelivery.mr_irmag.menu_service.Service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import tdelivery.mr_irmag.menu_service.Domain.DTO.ProductResponse;
import tdelivery.mr_irmag.menu_service.Domain.Entity.Product;
import tdelivery.mr_irmag.menu_service.Exception.ProductAlreadyExistsException;
import tdelivery.mr_irmag.menu_service.Exception.ProductNotFoundException;
import tdelivery.mr_irmag.menu_service.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ProductService {
    private ProductRepository productRepository;
    private AutoIncrementService autoIncrementService;

    @Autowired
    public ProductService(ProductRepository productRepository, AutoIncrementService autoIncrementService) {
        this.productRepository = productRepository;
        this.autoIncrementService = autoIncrementService;
    }

    @Schema(description = "Получает все продукты из хранилища.")
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()){
            return products.stream()
                    .map(product -> new ProductResponse(product.getName(), product.getPrice(), product.getDescription()))
                    .collect(Collectors.toList());
        } else {
            log.error("List of product is null!");
            throw new NullPointerException();
        }
    }


    @Schema(description = "Получает продукт по его id.")
    public Product getProductById(long id) {
        Optional<Product> temp = productRepository.findById(id);
        if (temp.isPresent()) {
            log.info("Product found: {}", temp.get());
            return temp.get();
        } else {
            log.error("Error! The product was not found with ID: {}", id);
            throw new ProductNotFoundException("Error! The product was not found with ID: " + id);
        }
    }

    @Schema(description = "Сохраняет новый продукт в хранилище.")
    public Product saveProduct(Product product) {
        if (product.getId() == null) {
            product.setId(autoIncrementService.getNextId());
        }

        if (productRepository.existsById(product.getId())) {
            throw new ProductAlreadyExistsException("There is already an product with this ID: " + product.getId());
        }

        try {
            Product savedProduct = productRepository.save(product);
            log.info("The product has been added successfully: {}", savedProduct);
            return savedProduct;
        } catch (DataAccessException e) {
            log.error("Error saving product: {}", e.getMessage());
            throw new RuntimeException("Could not save the product. Please try again.", e);
        }
    }

    @Schema(description = "Удаляет продукт по его id.")
    public boolean deleteProductById(Long id) {
        try {
            if (!productRepository.existsById(id)) {
                throw new ProductNotFoundException("Error! The product with this ID was not found: " + id);
            }

            productRepository.deleteById(id);
            log.info("The product was successfully deleted: {}", id);
            return true;

        } catch (ProductNotFoundException e) {
            log.error("Error! The product with this ID was not found: {}", id);
            throw e;
        } catch (EmptyResultDataAccessException e) {
            log.error("Error! Unable to delete. The product with ID {} does not exist.", id);
            throw new ProductNotFoundException("Error! The product with this ID was not found: " + id);
        } catch (Exception e) {
            log.error("An unexpected error occurred while deleting the category with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("An unexpected error occurred. Please try again later.");
        }
    }


}
