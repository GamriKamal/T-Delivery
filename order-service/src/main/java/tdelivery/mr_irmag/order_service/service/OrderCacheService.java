package tdelivery.mr_irmag.order_service.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

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

    @Cacheable(value = "userCoordinatesCache", key = "#address")
    public Point getUserCoordinates(String address) {
        return null;
    }

    @CachePut(value = "userCoordinatesCache", key = "#address")
    public Point cacheUserCoordinates(String address, Point coordinates) {
        return coordinates;
    }

    @CacheEvict(value = "userCoordinatesCache", key = "#address")
    public void removeUserCoordinates(String address) {
    }

    @Cacheable(value = "restaurantCoordinatesCache", key = "#address")
    public Point getRestaurantCoordinates(String address) {
        return null;
    }

    @CachePut(value = "restaurantCoordinatesCache", key = "#address")
    public Point cacheRestaurantCoordinates(String address, Point coordinates) {
        return coordinates;
    }

    @CacheEvict(value = "restaurantCoordinatesCache", key = "#address")
    public void removeRestaurantCoordinates(String address) {
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

    @Cacheable(value = "deliveryTime", key = "#address")
    public Integer getDeliveryTime(String address) {
        return null;
    }

    @CachePut(value = "deliveryTime", key = "#address")
    public Integer cacheDeliveryTime(String address, Integer deliveryTime) {
        return deliveryTime;
    }

    @CacheEvict(value = "deliveryTime", key = "#address")
    public void removeDeliveryTime(String address) {
    }
}
