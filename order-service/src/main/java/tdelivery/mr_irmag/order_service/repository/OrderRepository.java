package tdelivery.mr_irmag.order_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tdelivery.mr_irmag.order_service.domain.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByName(String name);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId")
    Page<Order> findAllOrderByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query(value = "SELECT * FROM orders o " +
            "WHERE ST_Distance(o.restaurant_coordinates::geography, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography) <= :radius " +
            "AND o.status = 'PREPARED' " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Order> findNearestPreparedOrders(@Param("radius") int radius,
                                          @Param("longitude") double longitude,
                                          @Param("latitude") double latitude,
                                          @Param("limit") int limit);


}



