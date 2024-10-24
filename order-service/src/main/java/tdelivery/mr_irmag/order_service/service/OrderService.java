package tdelivery.mr_irmag.order_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.order_service.domain.dto.DeliveryDTO;
import tdelivery.mr_irmag.order_service.domain.dto.DistanceMatrixDTO;
import tdelivery.mr_irmag.order_service.domain.dto.OrderItemRequest;
import tdelivery.mr_irmag.order_service.domain.dto.OrderRequest;
import tdelivery.mr_irmag.order_service.domain.entity.Order;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.order_service.repository.OrderRepository;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final DistanceService distanceService;

    public DeliveryDTO processOrder(OrderRequest orderRequest) {
        var response = distanceService.calculateDelivery(orderRequest.getAddress());
        response.setProductPrice(orderRequest.getItems().stream()
                .mapToDouble(OrderItemRequest::getPrice).sum());
        response.setTotalPrice(response.getDeliveryPrice() + response.getProductPrice());
        return response;
    }

}
