//package tdelivery.mr_irmag.menu_service.Controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//import tdelivery.mr_irmag.menu_service.Service.CSVService;
//
//@RestController
////@RequestMapping("/api/v1/menu")
//@RequiredArgsConstructor
//public class AdminController {
//    private final CSVService csvService;
//
//    @PostMapping("/upload-csv-file")
//    public ResponseEntity<Boolean> uploadCSVFile(@RequestParam("file") MultipartFile file) {
//        var result = csvService.parseCSV(file);
//        return new ResponseEntity<>(result, HttpStatus.CREATED);
//    }
//}
