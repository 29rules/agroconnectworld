package com.agroconnectworld.order.service;

import com.agroconnectworld.order.dto.CreateOrderRequest;
import com.agroconnectworld.order.dto.OrderItemRequest;
import com.agroconnectworld.order.entity.Order;
import com.agroconnectworld.order.entity.OrderItem;
import com.agroconnectworld.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<Order> listAll() {
        return repository.findAll();
    }

    public Order findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Transactional
    public Order create(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(request.getStatus());
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());
        request.getItems().forEach(itemRequest -> order.getItems().add(mapItem(itemRequest, order)));
        order.setTotalAmount(calculateTotal(order.getItems()));
        return repository.save(order);
    }

    private OrderItem mapItem(OrderItemRequest itemRequest, Order order) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(itemRequest.getProductId());
        item.setQuantity(itemRequest.getQuantity());
        item.setPrice(itemRequest.getPrice());
        return item;
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}




