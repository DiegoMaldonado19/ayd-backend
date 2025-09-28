package com.ayd.sie.courier.application.usecases;

import com.ayd.sie.courier.application.dto.CommissionDto;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.entities.User;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.UserJpaRepository;
import com.ayd.sie.shared.domain.exceptions.BusinessConstraintViolationException;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCommissionHistoryUseCase {

    private final TrackingGuideJpaRepository trackingGuideRepository;
    private final UserJpaRepository userRepository;

    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.15"); // 15% commission

    @Transactional(readOnly = true)
    public CommissionDto execute(Integer courierId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting commission history for courier {} from {} to {}", courierId, startDate, endDate);

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can access commission information");
        }

        // 2. Convert dates to LocalDateTime for database query
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        // 3. Get all delivered guides in the date range
        List<TrackingGuide> deliveredGuides;
        if (startDateTime != null && endDateTime != null) {
            deliveredGuides = trackingGuideRepository
                    .findByCourierUserIdAndCurrentStateStateNameAndDeliveryDateBetween(
                            courierId, "Entregada", startDateTime, endDateTime);
        } else if (startDateTime != null) {
            deliveredGuides = trackingGuideRepository
                    .findByCourierUserIdAndCurrentStateStateNameAndDeliveryDateGreaterThanEqual(
                            courierId, "Entregada", startDateTime);
        } else if (endDateTime != null) {
            deliveredGuides = trackingGuideRepository
                    .findByCourierUserIdAndCurrentStateStateNameAndDeliveryDateLessThanEqual(
                            courierId, "Entregada", endDateTime);
        } else {
            deliveredGuides = trackingGuideRepository
                    .findByCourierUserIdAndCurrentStateStateName(courierId, "Entregada");
        }

        // 4. Calculate totals and build response
        return buildCommissionDto(deliveredGuides, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCommissions(Integer courierId, LocalDate startDate, LocalDate endDate) {
        log.info("Calculating total commissions for courier {} from {} to {}", courierId, startDate, endDate);

        // 1. Validate courier
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found"));

        if (!courier.getRole().getRoleName().equals("Repartidor")) {
            throw new BusinessConstraintViolationException("Only couriers can access commission information");
        }

        // 2. Get delivered guides using the main method
        CommissionDto commissionData = execute(courierId, startDate, endDate);

        return commissionData.getTotalCommissions();
    }

    @Transactional(readOnly = true)
    public BigDecimal getMonthlyCommissions(Integer courierId, int year, int month) {
        log.info("Getting monthly commissions for courier {} - {}/{}", courierId, month, year);

        // 1. Calculate date range for the month
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 2. Use the existing method to calculate total commissions
        return getTotalCommissions(courierId, startDate, endDate);
    }

    private CommissionDto buildCommissionDto(List<TrackingGuide> deliveredGuides, LocalDate startDate,
            LocalDate endDate) {
        List<CommissionDto.DeliveryCommissionDetail> deliveryDetails = deliveredGuides.stream()
                .map(this::mapToDeliveryCommissionDetail)
                .collect(Collectors.toList());

        BigDecimal totalCommissions = deliveryDetails.stream()
                .map(CommissionDto.DeliveryCommissionDetail::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // For now, no penalties are applied
        BigDecimal totalPenalties = BigDecimal.ZERO;
        BigDecimal netTotal = totalCommissions.subtract(totalPenalties);

        return CommissionDto.builder()
                .periodStart(startDate)
                .periodEnd(endDate)
                .totalDeliveries(deliveredGuides.size())
                .totalCommissions(totalCommissions)
                .totalPenalties(totalPenalties)
                .netTotal(netTotal)
                .settlementStatus("Pendiente") // Default status
                .paymentDate(null) // Not paid yet
                .deliveryDetails(deliveryDetails)
                .build();
    }

    private CommissionDto.DeliveryCommissionDetail mapToDeliveryCommissionDetail(TrackingGuide guide) {
        BigDecimal commission = calculateCommission(guide);

        return CommissionDto.DeliveryCommissionDetail.builder()
                .guideId(guide.getGuideId())
                .guideNumber(guide.getGuideNumber())
                .businessName(guide.getBusiness().getBusinessName())
                .deliveryDate(guide.getDeliveryDate())
                .basePrice(guide.getBasePrice())
                .commissionRate(COMMISSION_RATE)
                .commissionAmount(commission)
                .deliveryStatus("Entregada")
                .recipientName(guide.getRecipientName())
                .build();
    }

    private BigDecimal calculateCommission(TrackingGuide guide) {
        if (guide.getBasePrice() == null) {
            return BigDecimal.ZERO;
        }
        return guide.getBasePrice().multiply(COMMISSION_RATE);
    }
}