package tdelivery.mr_irmag.menu_service.Service;

import com.netflix.discovery.converters.Auto;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tdelivery.mr_irmag.menu_service.Domain.Entity.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
public class CSVService {
    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public boolean parseCSV(MultipartFile file){
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;

            csvReader.readNext();

            while ((nextLine = csvReader.readNext()) != null) {
                var product = Product.builder()
                        .name(nextLine[0])
                        .price(Double.parseDouble(nextLine[1]))
                        .description(nextLine[2])
                        .imageUrl(nextLine[3])
                        .build();

                productService.saveProduct(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getLocalizedMessage());
        }
        return true;
    }
}