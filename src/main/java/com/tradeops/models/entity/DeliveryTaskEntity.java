package com.tradeops.models.entity;

import com.tradeops.models.model.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "delivery_tasks")
public class DeliveryTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;

    @OneToOne
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String evidenceUrl;
    private LocalDateTime updatedAt;



}
