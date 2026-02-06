package org.votingsystem.voting_system_for_award_nominations.service.report;

import java.io.IOException;


// Defines the contract for all report generation strategies.
public interface ReportGenerationStrategy {

    String generateReport(Long eventId) throws IOException;
}