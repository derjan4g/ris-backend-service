package de.bund.digitalservice.ris.caselaw.adapter;

import de.bund.digitalservice.ris.OpenApiConfiguration;
import de.bund.digitalservice.ris.caselaw.domain.FeatureToggleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/feature-toggles")
@Tag(name = OpenApiConfiguration.CASELAW_TAG)
public class FeatureToggleController {
  private final FeatureToggleService service;

  public FeatureToggleController(FeatureToggleService service) {
    this.service = service;
  }

  @GetMapping("/{toggleName}")
  @PreAuthorize("isAuthenticated()")
  public Mono<Boolean> isEnabled(@PathVariable String toggleName) {
    return service.isEnabled(toggleName);
  }
}
