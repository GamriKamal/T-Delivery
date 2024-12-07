package tdelivery.mr_irmag.menu_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Operation(summary = "Получить доступ ко всем пунктам меню", description = "Получить список всех продуктов из меню")
    @ApiResponse(responseCode = "200", description = "Успешно получен список продуктов",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class)))
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getMenuItems() {
        List<ProductResponse> productList = productService.getAllProducts();
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @Operation(summary = "Создайте новый продукт", description = "Добавьте новый продукт в меню")
    @ApiResponse(responseCode = "201", description = "Успешно созданный продукт",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Product.class)))
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Parameter(description = "Product details") Product product) {
        return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.CREATED);
    }

    @Operation(summary = "Загрузить CSV-файл", description = "Загрузите CSV-файл для массового добавления продуктов. Нужна роль админа.")
    @ApiResponse(responseCode = "201", description = "CSV-файл загружен, а продукты добавлены успешно",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    @PostMapping("/upload-csv-file")
    public ResponseEntity<Boolean> uploadCSVFile(@RequestParam("file") @Parameter(description = "CSV-файл для загрузки") MultipartFile file) {
        var result = csvService.parseCSV(file);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
