package tdelivery.mr_irmag.order_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tdelivery.mr_irmag.order_service.domain.entity.Order;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByName(String name);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId")
    Page<Order> findAllOrderByUserId(@Param("userId") UUID userId, Pageable pageable);


}
