package org.votingsystem.voting_system_for_award_nominations.service.em;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Nominee;
import org.votingsystem.voting_system_for_award_nominations.repository.NomineeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class NomineeService {

    private final NomineeRepository nomineeRepository;

    public NomineeService(NomineeRepository nomineeRepository) {
        this.nomineeRepository = nomineeRepository;
    }

    //  Create nominee + upload image
    public Nominee createNominee(Nominee nominee, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            // Generate a unique filename to avoid conflicts
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Save the file inside static/images folder
            Path uploadPath = Paths.get("src/main/resources/static/images/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // Store only the relative path in DB
            nominee.setImagePath("/images/" + fileName);
        }

        // Default status
        if (nominee.getStatus() == null || nominee.getStatus().isBlank()) {
            nominee.setStatus("PENDING"); // waiting for HR approval
        }

        return nomineeRepository.save(nominee);
    }

    //  Get all nominees
    public List<Nominee> getAllNominees() {
        return nomineeRepository.findAll();
    }

    // Get nominees only for a given event
    public List<Nominee> getNomineesForEvent(Long eventId) {
        return nomineeRepository.findByCategoryEventId(eventId);
    }


    //  Delete nominee (with safe check)
    public void deleteNominee(Long id) {
        nomineeRepository.findById(id).ifPresent(nominee -> {
            nomineeRepository.delete(nominee);
        });
    }
}
