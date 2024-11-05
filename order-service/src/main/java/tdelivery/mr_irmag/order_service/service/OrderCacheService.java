package tdelivery.mr_irmag.order_service.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderCacheService {

    @Cacheable(value = "totalAmountCache", key = "#address")
    public Double getTotalAmount(String address) {
        return null;
    }

    @CachePut(value = "totalAmountCache", key = "#address")
    public Double cacheTotalAmount(String address, Double totalAmount) {
        return totalAmount;
    }

    @CacheEvict(value = "totalAmountCache", key = "#address")
    public void removeTotalAmount(String address) {
    }

    @Cacheable(value = "coordinatesCache", key = "#address")
    public Point getCoordinates(String address) {
        return null;
    }

    @CachePut(value = "coordinatesCache", key = "#address")
    public Point cacheCoordinates(String address, Point coordinates) {
        return coordinates;
    }

    @CacheEvict(value = "coordinatesCache", key = "#address")
    public void removeCoordinates(String address) {
    }

    @Cacheable(value = "restaurantAddress", key = "#address")
    public String getRestaurantAddress(String address) {
        return null;
    }

    @CachePut(value = "restaurantAddress", key = "#address")
    public String cacheRestaurantAddress(String address, String restaurantAddress) {
        return restaurantAddress;
    }

    @CacheEvict(value = "restaurantAddress", key = "#address")
    public void removeRestaurantAddress(String address) {
    }

}
