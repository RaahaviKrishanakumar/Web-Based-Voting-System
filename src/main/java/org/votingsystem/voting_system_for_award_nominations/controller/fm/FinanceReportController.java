package org.votingsystem.voting_system_for_award_nominations.controller.fm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;
import org.votingsystem.voting_system_for_award_nominations.service.report.FinancialSummaryReportStrategy;
import org.votingsystem.voting_system_for_award_nominations.service.report.ReportService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/fm/reports") // Base path for all finance reports
public class FinanceReportController {

    //  use ReportService directly
    private final ReportService reportService;
    private final FinancialSummaryReportStrategy financialSummaryReportStrategy;
    private final EventService eventService;

    @Autowired
    public FinanceReportController(ReportService reportService,
                                   FinancialSummaryReportStrategy financialSummaryReportStrategy,
                                   EventService eventService) {
        this.reportService = reportService;
        this.financialSummaryReportStrategy = financialSummaryReportStrategy;
        this.eventService = eventService;
    }

    @PostMapping("/generate-financial-summary")
    public ResponseEntity<?> generateFinancialSummary(RedirectAttributes redirectAttributes) {
        try {
            Optional<Event> activeEventOpt = eventService.getActiveEvent();

            if (activeEventOpt.isEmpty()) {
                throw new RuntimeException("No active event found to generate a report for.");
            }

            Long activeEventId = activeEventOpt.get().getId();

            //  CALL ReportService DIRECTLY
            // 1. Set the strategy
            reportService.setStrategy(financialSummaryReportStrategy);
            // 2. Execute the report generation
            String filePathString = reportService.executeReportGeneration(activeEventId);

            Path path = Paths.get(filePathString);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the generated file: " + filePathString);
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("reportMessage", "❌ Error generating report: " + e.getMessage());
            // Make sure this path is correct for your finance dashboard
            return ResponseEntity.status(302).header("Location", "/fm/finance").build();
        }
    }
}