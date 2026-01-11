package com.shop.onlineshop.service;

import com.shop.onlineshop.models.dto.DeliveryTaskDTO;
import com.shop.onlineshop.models.model.OrderStatus;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface CourierService {
    List<DeliveryTaskDTO> deliveryTasks(Long id) throws AccessDeniedException;
    void acceptTask(Long deliveryTaskId);
    void setStatus(Long deliveryTaskId, OrderStatus orderStatus);
    void setEvidence(Long taskId, String evidenceUrl);
}
