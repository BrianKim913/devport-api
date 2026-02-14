package kr.devport.api.domain.port.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.devport.api.domain.port.dto.response.PortDetailResponse;
import kr.devport.api.domain.port.dto.response.PortResponse;
import kr.devport.api.domain.port.service.PortService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Ports", description = "Port communities endpoints")
@RestController
@RequestMapping("/api/ports")
@RequiredArgsConstructor
public class PortController {

    private final PortService portService;

    @Operation(
        summary = "Get all ports",
        description = "Retrieve all port communities with project counts"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ports retrieved successfully",
            content = @Content(schema = @Schema(implementation = PortResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<PortResponse>> getAllPorts() {
        return ResponseEntity.ok(portService.getAllPorts());
    }

    @Operation(
        summary = "Get port by slug",
        description = "Retrieve port detail with projects and hot releases"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Port retrieved successfully",
            content = @Content(schema = @Schema(implementation = PortDetailResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Port not found", content = @Content)
    })
    @GetMapping("/{slug}")
    public ResponseEntity<PortDetailResponse> getPortBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(portService.getPortBySlug(slug));
    }
}
