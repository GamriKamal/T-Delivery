package tdelivery.mr_irmag.order_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.geo.Point;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import tdelivery.mr_irmag.order_service.TestContainerBase;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = "eureka.client.enabled=false")
class OrderCacheServiceTest extends TestContainerBase {

    @Autowired
    private OrderCacheService orderCacheService;

    @Autowired
    private CacheManager cacheManager;

    private final String testAddress = "123 Test St";
    private final Double testTotalAmount = 150.0;
    private final Point testCoordinates = new Point(40.7128, 74.0060);
    private final String testRestaurantAddress = "Test Restaurant Address";
    private final Integer testDeliveryTime = 30;

    @BeforeEach
    public void setup() {
        Cache cacheTotalAmount = cacheManager.getCache("totalAmountCache");
        Cache cacheCoordinates = cacheManager.getCache("coordinatesCache");
        Cache cacheRestaurantAddress = cacheManager.getCache("restaurantAddress");
        Cache cacheDeliveryTime = cacheManager.getCache("deliveryTime");

        if (cacheTotalAmount != null) {
            cacheTotalAmount.clear();
        }
        if (cacheCoordinates != null) {
            cacheCoordinates.clear();
        }
        if (cacheRestaurantAddress != null) {
            cacheRestaurantAddress.clear();
        }
        if (cacheDeliveryTime != null) {
            cacheDeliveryTime.clear();
        }
    }

    @Test
    void cacheTotalAmount_ValidAddress_ShouldStoreTotalAmountInCache() {
        // Act
        orderCacheService.cacheTotalAmount(testAddress, testTotalAmount);

        // Assert
        Cache totalAmountCache = cacheManager.getCache("totalAmountCache");
        assertThat(totalAmountCache).isNotNull();
        assertThat(totalAmountCache.get(testAddress, Double.class)).isEqualTo(testTotalAmount);
    }

    @Test
    void cacheCoordinates_ValidAddress_ShouldStoreCoordinatesInCacheRestaurant() {
        // Act
        orderCacheService.cacheRestaurantCoordinates(testAddress, testCoordinates);

        // Assert
        Cache coordinatesCache = cacheManager.getCache("restaurantCoordinatesCache");
        assertThat(coordinatesCache).isNotNull();
        assertThat(coordinatesCache.get(testAddress, Point.class)).isEqualTo(testCoordinates);
    }

    @Test
    public void cacheRestaurantAddress_ValidAddress_ShouldStoreRestaurantAddressInCache() {
        // Act
        orderCacheService.cacheRestaurantAddress(testAddress, testRestaurantAddress);

        // Assert
        Cache restaurantAddressCache = cacheManager.getCache("restaurantAddress");
        assertThat(restaurantAddressCache).isNotNull();
        assertThat(restaurantAddressCache.get(testAddress, String.class)).isEqualTo(testRestaurantAddress);
    }

    @Test
    void cacheEviction_ValidAddress_ShouldEvictCachedValues() {
        // Arrange
        orderCacheService.cacheTotalAmount(testAddress, testTotalAmount);
        orderCacheService.cacheRestaurantCoordinates(testAddress, testCoordinates);
        orderCacheService.cacheRestaurantAddress(testAddress, testRestaurantAddress);

        // Act
        orderCacheService.removeTotalAmount(testAddress);
        orderCacheService.removeRestaurantCoordinates(testAddress);
        orderCacheService.removeRestaurantAddress(testAddress);

        // Assert
        Cache totalAmountCache = cacheManager.getCache("totalAmountCache");
        Cache coordinatesCache = cacheManager.getCache("restaurantCoordinatesCache");
        Cache restaurantAddressCache = cacheManager.getCache("restaurantAddress");

        assertThat(totalAmountCache.get(testAddress, Double.class)).isNull();
        assertThat(coordinatesCache.get(testAddress, Point.class)).isNull();
        assertThat(restaurantAddressCache.get(testAddress, String.class)).isNull();
    }

    @Test
    void cacheTotalAmount_RetrieveFromCache_ShouldReturnTotalAmount() {
        // Arrange
        orderCacheService.cacheTotalAmount(testAddress, testTotalAmount);

        // Act
        Double cachedAmount = orderCacheService.getTotalAmount(testAddress);

        // Assert
        assertThat(cachedAmount).isEqualTo(testTotalAmount);
    }

    @Test
    void cacheRestaurantCoordinates_RetrieveFromCache_ShouldReturnCoordinates() {
        // Arrange
        orderCacheService.cacheRestaurantCoordinates(testAddress, testCoordinates);

        // Act
        Point cachedCoordinates = orderCacheService.getRestaurantCoordinates(testAddress);

        // Assert
        assertThat(cachedCoordinates).isEqualTo(testCoordinates);
    }

    @Test
    public void cacheRestaurantAddress_RetrieveFromCache_ShouldReturnRestaurantAddress() {
        // Arrange
        orderCacheService.cacheRestaurantAddress(testAddress, testRestaurantAddress);

        // Act
        String cachedAddress = orderCacheService.getRestaurantAddress(testAddress);

        // Assert
        assertThat(cachedAddress).isEqualTo(testRestaurantAddress);
    }

    @Test
    void cacheDeliveryTime_ValidAddress_ShouldStoreDeliveryTimeInCache() {
        // Act
        orderCacheService.cacheDeliveryTime(testAddress, testDeliveryTime);

        // Assert
        Cache deliveryTimeCache = cacheManager.getCache("deliveryTime");
        assertThat(deliveryTimeCache).isNotNull();
        assertThat(deliveryTimeCache.get(testAddress, Integer.class)).isEqualTo(testDeliveryTime);
    }

    @Test
    void cacheDeliveryTime_RetrieveFromCache_ShouldReturnDeliveryTime() {
        // Arrange
        orderCacheService.cacheDeliveryTime(testAddress, testDeliveryTime);

        // Act
        Integer cachedDeliveryTime = orderCacheService.getDeliveryTime(testAddress);

        // Assert
        assertThat(cachedDeliveryTime).isEqualTo(testDeliveryTime);
    }

    @Test
    void cacheTotalAmount_RetrieveFromCache_ShouldReturnTotalAmount_WhenDataIsCached() {
        // Arrange
        orderCacheService.cacheTotalAmount(testAddress, testTotalAmount);

        // Act
        Double cachedAmount = orderCacheService.getTotalAmount(testAddress);

        // Assert
        assertThat(cachedAmount).isEqualTo(testTotalAmount);
    }

    @Test
    void cacheUserCoordinates_RetrieveFromCache_ShouldReturnCoordinates_WhenDataIsCached() {
        // Arrange
        orderCacheService.cacheUserCoordinates(testAddress, testCoordinates);

        // Act
        Point cachedCoordinates = orderCacheService.getUserCoordinates(testAddress);

        // Assert
        assertThat(cachedCoordinates).isEqualTo(testCoordinates);
    }

    @Test
    void cacheRestaurantCoordinates_RetrieveFromCache_ShouldReturnCoordinates_WhenDataIsCached() {
        // Arrange
        orderCacheService.cacheRestaurantCoordinates(testAddress, testCoordinates);

        // Act
        Point cachedCoordinates = orderCacheService.getRestaurantCoordinates(testAddress);

        // Assert
        assertThat(cachedCoordinates).isEqualTo(testCoordinates);
    }

    @Test
    void cacheRestaurantAddress_RetrieveFromCache_ShouldReturnRestaurantAddress_WhenDataIsCached() {
        // Arrange
        orderCacheService.cacheRestaurantAddress(testAddress, testRestaurantAddress);

        // Act
        String cachedAddress = orderCacheService.getRestaurantAddress(testAddress);

        // Assert
        assertThat(cachedAddress).isEqualTo(testRestaurantAddress);
    }

    @Test
    void cacheDeliveryTime_RetrieveFromCache_ShouldReturnDeliveryTime_WhenDataIsCached() {
        // Arrange
        orderCacheService.cacheDeliveryTime(testAddress, testDeliveryTime);

        // Act
        Integer cachedDeliveryTime = orderCacheService.getDeliveryTime(testAddress);

        // Assert
        assertThat(cachedDeliveryTime).isEqualTo(testDeliveryTime);
    }

}
