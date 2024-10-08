package tdelivery.mr_irmag.menu_service.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.menu_service.Domain.Entity.Product;


@Service
@Log4j2
public class AutoIncrementService {

    private final AtomicLong currentId = new AtomicLong(0);
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AutoIncrementService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initialize() {
        log.info(mongoTemplate.findAll(Product.class, "product"));

        Long maxId = mongoTemplate.findAll(Product.class, "product").stream()
                .mapToLong(Product::getId)
                .max()
                .orElse(0L);

        currentId.set(maxId);
    }

    public Long getNextId() {
        return currentId.incrementAndGet();
    }
}

