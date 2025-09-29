package com.ayd.sie.reports.application.services;

import com.ayd.sie.reports.application.dto.*;
import com.ayd.sie.shared.domain.entities.*;
import com.ayd.sie.shared.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportGeneratorService {

        private final TrackingGuideJpaRepository trackingGuideRepository;
        private final UserJpaRepository userRepository;
        private final BusinessJpaRepository businessRepository;
        private final MonthlyDiscountJpaRepository monthlyDiscountRepository;

        public DeliveryReportDto generateDeliveryReport(LocalDate startDate, LocalDate endDate) {
                log.info("Generating delivery report for period: {} to {}", startDate, endDate);

                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

                // Get delivery statistics using repository queries
                long completedDeliveries = trackingGuideRepository.countByCurrentStateStateNameAndDeliveryDateBetween(
                                "Entregada", startDateTime, endDateTime);

                long cancelledDeliveries = trackingGuideRepository
                                .countByCurrentStateStateNameAndCancellationDateBetween(
                                                "Cancelada", startDateTime, endDateTime);

                long rejectedDeliveries = trackingGuideRepository
                                .countByCurrentStateStateNameAndCancellationDateBetween(
                                                "Rechazada", startDateTime, endDateTime);

                long totalDeliveries = completedDeliveries + cancelledDeliveries + rejectedDeliveries;

                DeliveryReportDto report = DeliveryReportDto.builder()
                                .reportDate(LocalDate.now())
                                .periodStart(startDate)
                                .periodEnd(endDate)
                                .completedDeliveries(completedDeliveries)
                                .cancelledDeliveries(cancelledDeliveries)
                                .rejectedDeliveries(rejectedDeliveries)
                                .totalDeliveries(totalDeliveries)
                                .build();

                report.calculateRates();

                log.info("Delivery report generated: {} total deliveries", totalDeliveries);
                return report;
        }

        public List<CommissionReportDto> generateCommissionReport(LocalDate startDate, LocalDate endDate) {
                log.info("Generating commission report for period: {} to {}", startDate, endDate);

                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

                // Get all couriers who have deliveries in the period
                List<User> couriers = userRepository.findCouriersWithDeliveriesInPeriod(startDateTime, endDateTime);

                List<CommissionReportDto> reports = couriers.stream()
                                .map(courier -> generateCourierCommissionReport(courier, startDate, endDate,
                                                startDateTime,
                                                endDateTime))
                                .collect(Collectors.toList());

                log.info("Commission report generated for {} couriers", reports.size());
                return reports;
        }

        public List<DiscountReportDto> generateDiscountReport(LocalDate startDate, LocalDate endDate) {
                log.info("Generating discount report for period: {} to {}", startDate, endDate);

                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

                List<MonthlyDiscount> discounts = monthlyDiscountRepository.findByCalculatedAtBetween(
                                startDateTime, endDateTime);

                List<DiscountReportDto> reports = discounts.stream()
                                .map(this::mapToDiscountReportDto)
                                .collect(Collectors.toList());

                log.info("Discount report generated for {} businesses", reports.size());
                return reports;
        }

        public List<RankingReportDto> generateBusinessRankingReport(LocalDate startDate, LocalDate endDate) {
                log.info("Generating business ranking report for period: {} to {}", startDate, endDate);

                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

                // Get business statistics for the period
                List<Object[]> businessStats = businessRepository.findBusinessStatisticsForPeriod(
                                startDateTime, endDateTime);

                List<RankingReportDto> reports = IntStream.range(0, businessStats.size())
                                .mapToObj(index -> {
                                        Object[] stats = businessStats.get(index);
                                        RankingReportDto report = mapToRankingReportDto(stats, startDate, endDate);
                                        report.setRankPosition(index + 1); // Set ranking position
                                        report.calculateDerivedFields();
                                        return report;
                                })
                                .collect(Collectors.toList());

                log.info("Business ranking report generated for {} businesses", reports.size());
                return reports;
        }

        public List<RankingReportDto> generateCancellationsByBusinessReport(LocalDate startDate, LocalDate endDate) {
                log.info("Generating cancellations by business report for period: {} to {}", startDate, endDate);

                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

                List<Object[]> cancellationStats = businessRepository.findCancellationStatisticsByBusiness(
                                startDateTime, endDateTime);

                List<RankingReportDto> reports = cancellationStats.stream()
                                .map(stats -> mapToCancellationReportDto(stats, startDate, endDate))
                                .collect(Collectors.toList());

                log.info("Cancellation report generated for {} businesses", reports.size());
                return reports;
        }

        private CommissionReportDto generateCourierCommissionReport(User courier, LocalDate startDate,
                        LocalDate endDate,
                        LocalDateTime startDateTime, LocalDateTime endDateTime) {

                long totalDeliveries = trackingGuideRepository.countByCourierUserIdAndCreatedAtBetween(
                                courier.getUserId(), startDateTime, endDateTime);

                long completedDeliveries = trackingGuideRepository
                                .countByCourierUserIdAndCurrentStateStateNameAndDeliveryDateBetween(
                                                courier.getUserId(), "Entregada", startDateTime, endDateTime);

                long cancelledDeliveries = trackingGuideRepository
                                .countByCourierUserIdAndCurrentStateStateNameAndCancellationDateBetween(
                                                courier.getUserId(), "Cancelada", startDateTime, endDateTime);

                BigDecimal totalCommission = trackingGuideRepository.sumCommissionByCourierIdAndPeriod(
                                courier.getUserId(), startDateTime, endDateTime);

                if (totalCommission == null) {
                        totalCommission = BigDecimal.ZERO;
                }

                CommissionReportDto report = CommissionReportDto.builder()
                                .courierId(courier.getUserId())
                                .courierName(courier.getFirstName() + " " + courier.getLastName())
                                .courierEmail(courier.getEmail())
                                .periodStart(startDate)
                                .periodEnd(endDate)
                                .totalDeliveries(totalDeliveries)
                                .completedDeliveries(completedDeliveries)
                                .cancelledDeliveries(cancelledDeliveries)
                                .totalCommission(totalCommission)
                                .build();

                report.calculateDerivedFields();
                return report;
        }

        private DiscountReportDto mapToDiscountReportDto(MonthlyDiscount discount) {
                return DiscountReportDto.builder()
                                .businessId(discount.getBusiness().getBusinessId())
                                .businessName(discount.getBusiness().getBusinessName())
                                .loyaltyLevel(discount.getAppliedLevel() != null
                                                ? discount.getAppliedLevel().getLevelName()
                                                : "None")
                                .periodStart(LocalDate.of(discount.getYear(), discount.getMonth(), 1))
                                .periodEnd(LocalDate.of(discount.getYear(), discount.getMonth(), 1).withDayOfMonth(
                                                LocalDate.of(discount.getYear(), discount.getMonth(), 1)
                                                                .lengthOfMonth()))
                                .totalDeliveries(discount.getTotalDeliveries())
                                .completedDeliveries(0) // Would need to calculate from tracking data
                                .cancelledDeliveries(0) // Would need to calculate from tracking data
                                .totalAmount(discount.getTotalBeforeDiscount())
                                .discountPercentage(discount.getDiscountPercentage())
                                .discountAmount(discount.getDiscountAmount())
                                .finalAmount(discount.getTotalAfterDiscount())
                                .freeCancellationsUsed(0) // Would need additional logic to calculate
                                .penaltyAmount(BigDecimal.ZERO) // Would need additional logic to calculate
                                .build();
        }

        private RankingReportDto mapToRankingReportDto(Object[] stats, LocalDate startDate, LocalDate endDate) {
                return RankingReportDto.builder()
                                .businessId((Integer) stats[0])
                                .businessName((String) stats[1])
                                .businessEmail((String) stats[2])
                                .loyaltyLevel((String) stats[3])
                                .periodStart(startDate)
                                .periodEnd(endDate)
                                .totalDeliveries(((Number) stats[4]).longValue())
                                .completedDeliveries(((Number) stats[5]).longValue())
                                .cancelledDeliveries(((Number) stats[6]).longValue())
                                .totalRevenue((BigDecimal) stats[7])
                                .build();
        }

        private RankingReportDto mapToCancellationReportDto(Object[] stats, LocalDate startDate, LocalDate endDate) {
                return RankingReportDto.builder()
                                .businessId((Integer) stats[0])
                                .businessName((String) stats[1])
                                .businessEmail((String) stats[2])
                                .loyaltyLevel((String) stats[3])
                                .periodStart(startDate)
                                .periodEnd(endDate)
                                .totalDeliveries(((Number) stats[4]).longValue())
                                .completedDeliveries(((Number) stats[5]).longValue())
                                .cancelledDeliveries(((Number) stats[6]).longValue())
                                .totalRevenue(BigDecimal.ZERO) // Not relevant for cancellation report
                                .build();
        }
}