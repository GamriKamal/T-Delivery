package tdelivery.mr_irmag.courier_service.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.courier_service.domain.dto.Point;
import tdelivery.mr_irmag.courier_service.domain.dto.RouteServiceResponse;
import tdelivery.mr_irmag.courier_service.domain.entity.Order;

import java.util.List;

@Service
public class CourierCacheService {

    @Cacheable(value = "orderCache", key = "#courierCoordinates")
    public List<Order> getOptimalOrder(Point courierCoordinates) {
        return null;
    }

    @CachePut(value = "orderCache", key = "#courierCoordinates")
    public List<Order> cacheOptimalOrders(Point courierCoordinates, List<Order> order) {
        return order;
    }

    @CacheEvict(value = "orderCache", key = "#courierCoordinates")
    public void removeOptimalOrder(Point courierCoordinates) {
    }

    @Cacheable(value = "timeDelivery", key = "#courierCoordinates")
    public RouteServiceResponse getTimeDelivery(Point courierCoordinates) {
        return null;
    }

    @CachePut(value = "timeDelivery", key = "#courierCoordinates")
    public RouteServiceResponse cacheTimeDelivery(Point courierCoordinates, RouteServiceResponse order) {
        return order;
    }

    @CacheEvict(value = "timeDelivery", key = "#courierCoordinates")
    public void removeTimeDelivery(Point courierCoordinates) {
    }

}

