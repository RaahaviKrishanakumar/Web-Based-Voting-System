package org.votingsystem.voting_system_for_award_nominations.service.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.votingsystem.voting_system_for_award_nominations.repository.ExpenseRepository;
import org.votingsystem.voting_system_for_award_nominations.repository.SponsorRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.math.BigDecimal;

@Component
public class FinancialSummaryReportStrategy implements ReportGenerationStrategy {

    private final SponsorRepository sponsorRepository;
    private final ExpenseRepository expenseRepository;

    @Autowired
    public FinancialSummaryReportStrategy(SponsorRepository sponsorRepository, ExpenseRepository expenseRepository) {
        this.sponsorRepository = sponsorRepository;
        this.expenseRepository = expenseRepository;
    }

    @Override
    public String generateReport(Long eventId) throws IOException {
        //  Define the file path with a .csv extension
        String filePath = "reports/financial_summary_report_" + eventId + ".csv";

        // Create the 'reports' directory if it does not exist
        Path reportDir = Paths.get("reports");
        if (!Files.exists(reportDir)) {
            Files.createDirectories(reportDir);
        }

        // Calculate total sponsorship amount
        double totalSponsorship = sponsorRepository.findByEventIdOrderByIdDesc(eventId)
                .stream()
                .map(s -> s.getAmount().doubleValue())
                .reduce(0.0, Double::sum);

        // Calculate total expenses
        double totalExpenses = expenseRepository.findByBudgetEventId(eventId)
                .stream()
                .map(e -> e.getAmount().doubleValue())
                .reduce(0.0, Double::sum);

        double netResult = totalSponsorship - totalExpenses;

        // Write the content in CSV format
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV headers
            writer.append("Category,Amount\n");

            // Write data rows
            writer.append("Total Sponsorship,").append(String.format("%.2f", totalSponsorship)).append("\n");
            writer.append("Total Expenses,").append(String.format("%.2f", totalExpenses)).append("\n");
            writer.append("Net Result,").append(String.format("%.2f", netResult)).append("\n");
        }

        return filePath;
    }
}