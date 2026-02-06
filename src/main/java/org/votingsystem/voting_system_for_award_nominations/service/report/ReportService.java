package org.votingsystem.voting_system_for_award_nominations.service.report;

import org.springframework.stereotype.Service;
import java.io.IOException;

// The Context class that uses a report generation strategy.
@Service
public class ReportService {

    // Holds a reference to the current strategy object.
    private ReportGenerationStrategy strategy;

    // Allows changing the strategy at runtime.
    public void setStrategy(ReportGenerationStrategy strategy) {
        this.strategy = strategy;
    }

    // Executes the generateReport method of the currently set strategy.
    public String executeReportGeneration(Long eventId) throws IOException {
        if (strategy == null) {
            throw new IllegalStateException("A report generation strategy must be set before execution.");
        }
        return strategy.generateReport(eventId);
    }
}