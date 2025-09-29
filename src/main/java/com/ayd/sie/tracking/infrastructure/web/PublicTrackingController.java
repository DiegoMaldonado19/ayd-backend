package com.ayd.sie.tracking.infrastructure.web;

import com.ayd.sie.tracking.application.dto.RejectDeliveryDto;
import com.ayd.sie.tracking.application.dto.RejectDeliveryResponseDto;
import com.ayd.sie.tracking.application.dto.TrackingResponseDto;
import com.ayd.sie.tracking.application.usecases.PublicTrackingUseCase;
import com.ayd.sie.tracking.application.usecases.RejectDeliveryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracking/public")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public Tracking", description = "Public tracking endpoints for customers")
public class PublicTrackingController {

    private final PublicTrackingUseCase publicTrackingUseCase;
    private final RejectDeliveryUseCase rejectDeliveryUseCase;

    @GetMapping("/{guideNumber}")
    @Operation(summary = "Get tracking information", description = "Retrieve detailed tracking information for a delivery guide by guide number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tracking information retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Tracking guide not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TrackingResponseDto> getTrackingInfo(
            @Parameter(description = "Guide number to track", example = "202500000001", required = true) @PathVariable String guideNumber) {

        log.info("Received request to track guide: {}", guideNumber);

        try {
            TrackingResponseDto trackingInfo = publicTrackingUseCase.getTrackingInfo(guideNumber);
            return ResponseEntity.ok(trackingInfo);
        } catch (RuntimeException e) {
            log.error("Error tracking guide {}: {}", guideNumber, e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/reject")
    @Operation(summary = "Reject delivery", description = "Reject a delivery by providing user email, rejection reason and optionally initiate return process")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or delivery cannot be rejected"),
            @ApiResponse(responseCode = "404", description = "Tracking guide or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> rejectDelivery(
            @Parameter(description = "Rejection details including user email and reason", required = true) @Valid @RequestBody RejectDeliveryDto request) {

        log.info("Received request to reject delivery for guide: {}", request.getGuideNumber());

        try {
            RejectDeliveryResponseDto response = rejectDeliveryUseCase.rejectDelivery(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error rejecting delivery for guide {}: {}", request.getGuideNumber(), e.getMessage());

            if (e.getMessage().contains("Tracking guide not found")) {
                return ResponseEntity.status(404).body("Tracking guide not found");
            }

            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(404).body("User not found with the provided email");
            }

            if (e.getMessage().contains("Cannot reject")) {
                return ResponseEntity.status(400).body(e.getMessage());
            }

            return ResponseEntity.status(500).body("Error processing rejection");
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search tracking guide", description = "Search for tracking information by guide number (alternative endpoint)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tracking information retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Guide number is required"),
            @ApiResponse(responseCode = "404", description = "Tracking guide not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TrackingResponseDto> searchGuide(
            @Parameter(description = "Guide number to search", example = "202500000001", required = true) @RequestParam String guideNumber) {

        if (guideNumber == null || guideNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return getTrackingInfo(guideNumber.trim());
    }
}