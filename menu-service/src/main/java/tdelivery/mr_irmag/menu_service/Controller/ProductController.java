package tdelivery.mr_irmag.menu_service.Controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tdelivery.mr_irmag.menu_service.Domain.DTO.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import tdelivery.mr_irmag.menu_service.Domain.Entity.Product;
import tdelivery.mr_irmag.menu_service.Service.CSVService;
import tdelivery.mr_irmag.menu_service.Service.ProductService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@RestController
@RequestMapping(value = "/menu")
public class ProductController {
    private ProductService productService;
    private CSVService csvService;

    @Autowired
    public ProductController(ProductService productService, CSVService csvService) {
        this.productService = productService;
        this.csvService = csvService;
    }

    @GetMapping("/products")
    public ResponseEntity<?> getMenuItems() {
        try {
            var list = productService.getAllProducts();
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (NullPointerException e){
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload-csv-file")
    public ResponseEntity<?> uploadCSVFile(@RequestParam("file") MultipartFile file) throws IOException {
        var result = csvService.parseCSV(file);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
