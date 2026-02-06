package org.votingsystem.voting_system_for_award_nominations.service.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Vote;
import org.votingsystem.voting_system_for_award_nominations.repository.VoteRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VoterDemographicsReportStrategy implements ReportGenerationStrategy {

    private final VoteRepository voteRepository;

    @Autowired
    public VoterDemographicsReportStrategy(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    public String generateReport(Long eventId) throws IOException {
        //  Get ALL votes for the event
        List<Vote> allVotes = voteRepository.findByEvent_Id(eventId);

        String filePath = "reports/voter_summary_report_" + eventId + ".csv";
        Path reportDir = Paths.get("reports");
        if (!Files.exists(reportDir)) {
            Files.createDirectories(reportDir);
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Category,Value\n");

            //   Filter for unique voters before counting
            Map<String, Vote> uniqueVoterMap = new HashMap<>();
            for (Vote vote : allVotes) {
                uniqueVoterMap.put(vote.getVoterId(), vote); // This automatically keeps only the last vote from each user
            }
            List<Vote> uniqueVotes = new ArrayList<>(uniqueVoterMap.values());
            // From now on, we will use the 'uniqueVotes' list for all calculations

            if (uniqueVotes.isEmpty()) {
                writer.append("Total Unique Voters,0\n");
                return filePath;
            }

            // Calculations now based on UNIQUE voters
            Map<String, Long> countryCounts = uniqueVotes.stream()
                    .filter(vote -> vote.getCountry() != null && !vote.getCountry().isBlank())
                    .collect(Collectors.groupingBy(Vote::getCountry, Collectors.counting()));

            Map<String, Integer> ageRangeCounts = new HashMap<>();
            ageRangeCounts.put("18-25", 0);
            ageRangeCounts.put("26-35", 0);
            ageRangeCounts.put("36-50", 0);
            ageRangeCounts.put("51+", 0);
            for (Vote vote : uniqueVotes) { // Use the unique list here too

                int age = vote.getAge();
                if (age >= 18 && age <= 25) ageRangeCounts.merge("18-25", 1, Integer::sum);
                else if (age >= 26 && age <= 35) ageRangeCounts.merge("26-35", 1, Integer::sum);
                else if (age >= 36 && age <= 50) ageRangeCounts.merge("36-50", 1, Integer::sum);
                else ageRangeCounts.merge("51+", 1, Integer::sum);
            }

            //  Write data to CSV
            writer.append("Total Unique Voters,").append(String.valueOf(uniqueVotes.size())).append("\n");
            writer.append("\n");

            writer.append("Voter Count by Country,\n");
            for (Map.Entry<String, Long> entry : countryCounts.entrySet()) {
                writer.append(entry.getKey()).append(",").append(String.valueOf(entry.getValue())).append("\n");
            }
            writer.append("\n");

            writer.append("Voter Count by Age Range,\n");
            for (Map.Entry<String, Integer> entry : ageRangeCounts.entrySet()) {
                writer.append(entry.getKey()).append(",").append(String.valueOf(entry.getValue())).append("\n");
            }
        }

        return filePath;
    }
}