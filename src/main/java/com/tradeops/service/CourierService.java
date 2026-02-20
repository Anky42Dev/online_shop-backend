package com.tradeops.service;

import com.tradeops.models.dto.DeliveryTaskDTO;
import com.tradeops.models.model.OrderStatus;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface CourierService {
    List<DeliveryTaskDTO> deliveryTasks(Long id) throws AccessDeniedException;
    void acceptTask(Long deliveryTaskId);
    void setStatus(Long deliveryTaskId, OrderStatus orderStatus);
    void setEvidence(Long taskId, String evidenceUrl);
}
