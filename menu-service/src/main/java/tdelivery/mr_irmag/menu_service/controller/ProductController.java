package tdelivery.mr_irmag.menu_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tdelivery.mr_irmag.menu_service.domain.DTO.ProductResponse;
import tdelivery.mr_irmag.menu_service.domain.Entity.Product;
import tdelivery.mr_irmag.menu_service.service.CSVService;
import tdelivery.mr_irmag.menu_service.service.ProductService;

import java.util.List;

@RestController
@RequestMapping(value = "/menu")
public class ProductController {
    private final ProductService productService;
    private final CSVService csvService;

    @Autowired
    public ProductController(ProductService productService, CSVService csvService) {
        this.productService = productService;
        this.csvService = csvService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getMenuItems() {
        var list = productService.getAllProducts();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.CREATED);
    }

    @PostMapping("/upload-csv-file")
    public ResponseEntity<Boolean> uploadCSVFile(@RequestParam("file") MultipartFile file) {
        var result = csvService.parseCSV(file);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}