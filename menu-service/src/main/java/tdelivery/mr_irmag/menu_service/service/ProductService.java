package tdelivery.mr_irmag.menu_service.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.menu_service.domain.DTO.ProductResponse;
import tdelivery.mr_irmag.menu_service.domain.Entity.Product;
import tdelivery.mr_irmag.menu_service.exception.ProductAlreadyExistsException;
import tdelivery.mr_irmag.menu_service.exception.ProductNotFoundException;
import tdelivery.mr_irmag.menu_service.repository.ProductRepository;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Schema(description = "Получает все продукты из хранилища.")
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            return products.stream()
                    .map(product -> new ProductResponse(product.getName(), product.getPrice(), product.getDescription()))
                    .collect(Collectors.toList());
        } else {
            log.error("The list of products is empty!");
            throw new ProductNotFoundException("No products were found.");
        }
    }



    @Schema(description = "Получает продукт по его id.")
    public Product getProductById(String id) {
        Optional<Product> temp = productRepository.findById(id);
        if (temp.isPresent()) {
            log.info("The product has been found: {}", temp.get());
            return temp.get();
        } else {
            log.error("Error! The product was not found with an ID: {}", id);
            throw new ProductNotFoundException("Error! The product was not found with an ID: " + id);
        }
    }

    @Schema(description = "Сохраняет новый продукт в хранилище.")
    public Product saveProduct(Product product) {
        if (product.getId() != null && productRepository.existsById(product.getId())) {
            throw new ProductAlreadyExistsException("A product with this ID already exists: " + product.getId());
        }

        try {
            Product savedProduct = productRepository.save(product);
            log.info("The product has been successfully added: {}", savedProduct);
            return savedProduct;
        } catch (DataAccessException e) {
            log.error("Error saving the product: {}", e.getMessage());
            throw new RuntimeException("The product could not be saved. Try again.", e);
        }
    }

    @Schema(description = "Удаляет продукт по его id.")
    public boolean deleteProductById(String id) {
        try {
            if (!productRepository.existsById(id)) {
                throw new ProductNotFoundException("Error! The product with this ID was not found: " + id);
            }

            productRepository.deleteById(id);
            log.info("The product has been successfully deleted: {}", id);
            return true;

        } catch (EmptyResultDataAccessException | ProductNotFoundException e) {
            log.error("Error! Could not be deleted. The product with ID {} does not exist.", id);
            throw new ProductNotFoundException("Error! The product with this ID was not found: " + id);
        } catch (Exception e) {
            log.error("Unexpected error when deleting a product with an ID {}: {}", id, e.getMessage());
            throw new RuntimeException("An unexpected error has occurred. Try again later.");
        }
    }
}

