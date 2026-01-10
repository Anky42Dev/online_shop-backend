package com.shop.onlineshop.service.impl;

import com.shop.onlineshop.exceptions.ResourceNotFoundException;
import com.shop.onlineshop.exceptions.UserNotFoundException;
import com.shop.onlineshop.mapper.DeliveryTaskMapper;
import com.shop.onlineshop.models.dto.DeliveryTaskDTO;
import com.shop.onlineshop.models.entity.Courier;
import com.shop.onlineshop.models.entity.DeliveryTaskEntity;
import com.shop.onlineshop.models.entity.OrderEntity;
import com.shop.onlineshop.models.entity.UserEntity;
import com.shop.onlineshop.models.model.OrderStatus;
import com.shop.onlineshop.repo.CourierRepo;
import com.shop.onlineshop.repo.DeliveryTaskRepo;
import com.shop.onlineshop.repo.OrderRepo;
import com.shop.onlineshop.service.CourierService;
import com.shop.onlineshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourierServiceImpl implements CourierService {
    private final UserService userService;
    private final CourierRepo courierRepo;
    private final DeliveryTaskRepo deliveryTaskRepo;
    private final OrderRepo orderRepo;
    private final DeliveryTaskMapper deliveryTaskMapper;


    @Override
    @Transactional(readOnly=true)
    public List<DeliveryTaskDTO> deliveryTasks() {
        Long userId = userService.getCurrentUser().getId();
        Courier courier = courierRepo.findById(userId).orElseThrow(()->new UserNotFoundException("User with id: " + userId + " not found"));
        return deliveryTaskMapper.toDTO(deliveryTaskRepo.findDeliveryTaskEntitiesByCourier_Id(courier.getId()));

    }

    @Override
    @Transactional
    public void acceptTask(Long deliveryTaskId) {
        UserEntity currentUser = userService.getCurrentUser();
        Courier courier = courierRepo.findCourierByUserEntity(currentUser);
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
    @Transactional()
    public void setStatus(Long deliveryTaskId, OrderStatus orderStatus) {
        DeliveryTaskEntity deliveryTaskEntity = deliveryTaskRepo.findById(deliveryTaskId).orElseThrow(()->new ResourceNotFoundException("Delivery task with id: " + deliveryTaskId + " not found"));

        deliveryTaskEntity.setOrderStatus(orderStatus);

        if(orderStatus == OrderStatus.DELIVERED){
            OrderEntity orderEntity = deliveryTaskEntity.getOrderEntity();
            if(orderEntity!=null){
                orderEntity.setStatus(OrderStatus.DELIVERED);
                orderRepo.save(orderEntity);
                log.info("Order {} marked as DELIVERED by courier", orderEntity.getId());            }
        }
        deliveryTaskRepo.save(deliveryTaskEntity);
    }

    @Override
    @Transactional
    public void setEvidence(Long taskId, String evidenceUrl) {
        DeliveryTaskEntity task = deliveryTaskRepo.findById(taskId)
                .orElseThrow(()->new ResourceNotFoundException("Task not found"));

        task.setEvidenceUrl(evidenceUrl);
        deliveryTaskRepo.save(task);
    }
}
