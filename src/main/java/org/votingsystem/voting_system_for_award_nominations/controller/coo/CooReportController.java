package org.votingsystem.voting_system_for_award_nominations.controller.coo;

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
import org.votingsystem.voting_system_for_award_nominations.service.report.ReportService;
import org.votingsystem.voting_system_for_award_nominations.service.report.VoterDemographicsReportStrategy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/coo/reports")
public class CooReportController {

    // use ReportService directly
    private final ReportService reportService;
    private final VoterDemographicsReportStrategy voterDemographicsReportStrategy;
    private final EventService eventService;

    @Autowired
    public CooReportController(ReportService reportService,
                               VoterDemographicsReportStrategy voterDemographicsReportStrategy,
                               EventService eventService) {
        this.reportService = reportService;
        this.voterDemographicsReportStrategy = voterDemographicsReportStrategy;
        this.eventService = eventService;
    }

    @PostMapping("/generate-voter-demographics")
    public ResponseEntity<?> generateVoterReport(RedirectAttributes redirectAttributes) {
        try {
            Optional<Event> activeEventOpt = eventService.getActiveEvent();
            if (activeEventOpt.isEmpty()) {
                throw new RuntimeException("No active event found to generate a report for.");
            }
            Event activeEvent = activeEventOpt.get(); // We decided to get the event name, so pass the whole object

            // CALL ReportService DIRECTLY
            //  Set the strategy
            reportService.setStrategy(voterDemographicsReportStrategy);
            //  Execute the report generation
            String filePathString = reportService.executeReportGeneration(activeEvent.getId()); // Pass the ID here as per our final decision

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
            return ResponseEntity.status(302).header("Location", "/coo/voting_sessions").build();
        }
    }
}