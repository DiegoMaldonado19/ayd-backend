package com.ayd.sie.courier.application.usecases;

import com.ayd.sie.courier.application.dto.CourierDeliveryDto;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import com.ayd.sie.shared.domain.exceptions.BusinessConstraintViolationException;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCourierDeliveriesUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;

    @Transactional(readOnly = true)
    public Page<CourierDeliveryDto> execute(Integer courierId, int page, int size, String sortBy, String sortDir) {
        log.info("Getting deliveries for courier {} - page: {}, size: {}", courierId, page, size);

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can access delivery information");
        }

        // 2. Setup pagination and sorting
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Get deliveries assigned to the courier
        Page<TrackingGuide> guidePage = trackingGuideRepository.findByCourierUserId(courierId, pageable);

        // 4. Map to DTOs
        return guidePage.map(this::mapToCourierDeliveryDto);
    }

    @Transactional(readOnly = true)
    public List<CourierDeliveryDto> getActiveDeliveries(Integer courierId) {
        log.info("Getting active deliveries for courier {}", courierId);

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can access delivery information");
        }

        // 2. Get active deliveries (not in final states)
        List<String> activeStates = List.of("Asignada", "Recogida", "En Ruta", "Incidencia");
        List<TrackingGuide> activeGuides = trackingGuideRepository
                .findByCourierUserIdAndCurrentStateStateNameInOrderByCreatedAtAsc(courierId, activeStates);

        // 3. Map to DTOs
        return activeGuides.stream()
                .map(this::mapToCourierDeliveryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourierDeliveryDto> getDeliveriesByState(Integer courierId, String stateName) {
        log.info("Getting deliveries in state {} for courier {}", stateName, courierId);

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can access delivery information");
        }

        // 2. Validate state name
        List<String> validStates = List.of("Asignada", "Recogida", "En Ruta", "Entregada", "Incidencia");
        if (!validStates.contains(stateName)) {
            throw new BusinessConstraintViolationException("Invalid state name: " + stateName);
        }

        // 3. Get deliveries in the specified state
        List<TrackingGuide> guides = trackingGuideRepository
                .findByCourierUserIdAndCurrentStateStateNameOrderByCreatedAtAsc(courierId, stateName);

        // 4. Map to DTOs
        return guides.stream()
                .map(this::mapToCourierDeliveryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourierDeliveryDto getDeliveryDetail(Integer guideId, Integer courierId) {
        log.info("Getting delivery detail for guide {} by courier {}", guideId, courierId);

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can access delivery information");
        }

        // 2. Get tracking guide
        TrackingGuide guide = trackingGuideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking guide not found"));

        // 3. Verify the guide is assigned to the requesting courier
        if (guide.getCourier() == null || !guide.getCourier().getUserId().equals(courierId)) {
            throw new BusinessConstraintViolationException("Guide is not assigned to this courier");
        }

        // 4. Map to DTO
        return mapToCourierDeliveryDto(guide);
    }

    private CourierDeliveryDto mapToCourierDeliveryDto(TrackingGuide guide) {
        return CourierDeliveryDto.builder()
                .guideId(guide.getGuideId())
                .guideNumber(guide.getGuideNumber())
                .businessName(guide.getBusiness().getBusinessName())
                .currentState(guide.getCurrentState().getStateName())
                .basePrice(guide.getBasePrice())
                .courierCommission(guide.getCourierCommission())
                .recipientName(guide.getRecipientName())
                .recipientPhone(guide.getRecipientPhone())
                .recipientAddress(guide.getRecipientAddress())
                .recipientCity(guide.getRecipientCity())
                .recipientState(guide.getRecipientState())
                .observations(guide.getObservations())
                .assignmentAccepted(guide.getAssignmentAccepted())
                .assignmentDate(guide.getAssignmentDate())
                .assignmentAcceptedAt(guide.getAssignmentAcceptedAt())
                .pickupDate(guide.getPickupDate())
                .deliveryDate(guide.getDeliveryDate())
                .createdAt(guide.getCreatedAt())
                .priority("NORMAL") // Campo no disponible en la entidad actual, valor por defecto
                .hasIncidents(false) // Este campo se podría calcular consultando incidencias
                .build();
    }
}