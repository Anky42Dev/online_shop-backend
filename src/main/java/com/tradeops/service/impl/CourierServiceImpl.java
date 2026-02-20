package com.tradeops.service.impl;

import com.tradeops.exceptions.ResourceNotFoundException;
import com.tradeops.mapper.DeliveryTaskMapper;
import com.tradeops.models.dto.DeliveryTaskDTO;
import com.tradeops.models.entity.Courier;
import com.tradeops.models.entity.DeliveryTaskEntity;
import com.tradeops.models.entity.OrderEntity;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.model.OrderStatus; // Или DeliveryStatus, если поменяли
import com.tradeops.repo.CourierRepo;
import com.tradeops.repo.DeliveryTaskRepo;
import com.tradeops.repo.OrderRepo;
import com.tradeops.service.CourierService;
import com.tradeops.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException; // <--- ИСПРАВЛЕНО
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourierServiceImpl implements CourierService {
    private final UserService userService;
    private final CourierRepo courierRepo;
    private final DeliveryTaskRepo deliveryTaskRepo;
    private final OrderRepo orderRepo;
    private final DeliveryTaskMapper deliveryTaskMapper;

    private Courier getCurrentCourier() {
        UserEntity userEntity = userService.getCurrentUser();
        return courierRepo.findCourierByUserEntity(userEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Courier profile not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryTaskDTO> deliveryTasks(Long id) {
        Courier courier = getCurrentCourier();

        if (!Objects.equals(courier.getId(), id)) {
            throw new AccessDeniedException("Access denied: You may only see your tasks!");
        }

        return deliveryTaskMapper.toDTO(deliveryTaskRepo.findDeliveryTaskEntitiesByCourier_Id(courier.getId()));
    }

    @Override
    @Transactional
    public void acceptTask(Long deliveryTaskId) {
        Courier courier = getCurrentCourier();

        DeliveryTaskEntity deliveryTaskEntity = deliveryTaskRepo.findById(deliveryTaskId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery task with id: " + deliveryTaskId + " not found"));

        if (deliveryTaskEntity.getCourier() != null) {
            throw new IllegalStateException("Sorry, this task is already taken by another courier.");
        }

        deliveryTaskEntity.setCourier(courier);

        deliveryTaskEntity.setOrderStatus(OrderStatus.ACCEPTED);

        OrderEntity orderEntity = deliveryTaskEntity.getOrderEntity();
        orderEntity.setStatus(OrderStatus.ACCEPTED);

        deliveryTaskRepo.save(deliveryTaskEntity);
        orderRepo.save(orderEntity);
    }

    @Override
    @Transactional
    public void setStatus(Long deliveryTaskId, OrderStatus newStatus) {
        Courier courier = getCurrentCourier();

        DeliveryTaskEntity task = deliveryTaskRepo.findById(deliveryTaskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!Objects.equals(task.getCourier().getId(), courier.getId())) {
            throw new AccessDeniedException("You cannot change status of a task that is not assigned to you.");
        }

        task.setOrderStatus(newStatus);

        if (task.getOrderEntity() != null) {
            task.getOrderEntity().setStatus(newStatus);
            orderRepo.save(task.getOrderEntity());
        }

        deliveryTaskRepo.save(task);
    }

    @Override
    @Transactional
    public void setEvidence(Long taskId, String evidenceUrl) {
        Courier courier = getCurrentCourier();

        DeliveryTaskEntity task = deliveryTaskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!Objects.equals(task.getCourier().getId(), courier.getId())) {
            throw new AccessDeniedException("You cannot upload evidence for a task that is not assigned to you.");
        }

        task.setEvidenceUrl(evidenceUrl);
        deliveryTaskRepo.save(task);

    }
}