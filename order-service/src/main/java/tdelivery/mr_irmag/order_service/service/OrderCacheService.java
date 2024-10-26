package tdelivery.mr_irmag.order_service.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderCacheService {
    private final Map<UUID, Double> totalAmountCache = new ConcurrentHashMap<>();
    public void cacheTotalAmount(UUID userId, Double totalAmount) {
        totalAmountCache.put(userId, totalAmount);
    }

    public Double getTotalAmount(UUID userId) {
        return totalAmountCache.get(userId);
    }

    public void removeTotalAmount(UUID userId) {
        totalAmountCache.remove(userId);
    }
}
