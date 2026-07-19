package test.takehome.transinnovate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import test.takehome.transinnovate.constants.ApplicationConstants;
import test.takehome.transinnovate.dto.OrderRequest;
import test.takehome.transinnovate.service.IntegrationService;

@RestController
@RequestMapping("/api/v1/integration")
@Tag(name = "Integration", description = "Integration API")
public class IntegrationController {

    final IntegrationService integrationService;

    @Autowired
    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping("/forward")
    @Operation(summary = "Forward order to Xdock", description = "Routes order via chosen channel")
    public ResponseEntity<Object> forwardOrder(
        @RequestParam ApplicationConstants.ChannelType channel,
        @RequestBody OrderRequest request
    ) throws Exception {
        Object response = this.integrationService.processOrder(channel, request);
        return ResponseEntity.ok(response);
    }
}
