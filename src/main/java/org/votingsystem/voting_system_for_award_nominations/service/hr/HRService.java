package org.votingsystem.voting_system_for_award_nominations.service.hr;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Category;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Nominee;
import org.votingsystem.voting_system_for_award_nominations.repository.CategoryRepository;
import org.votingsystem.voting_system_for_award_nominations.repository.NomineeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class HRService {

    private final NomineeRepository nomineeRepository;
    private final CategoryRepository categoryRepository;

    public HRService(NomineeRepository nomineeRepository, CategoryRepository categoryRepository) {
        this.nomineeRepository = nomineeRepository;
        this.categoryRepository = categoryRepository;
    }

    //  Approve or Reject nominee
    public void updateNomineeStatus(Long id, String status) {
        nomineeRepository.findById(id).ifPresent(nominee -> {
            nominee.setStatus(status);
            nomineeRepository.save(nominee);
        });
    }

    //  Edit nominee (name, description, category + image)
    public void updateNomineeDetails(Nominee updatedNominee, Long categoryId, MultipartFile imageFile) {
        nomineeRepository.findById(updatedNominee.getId()).ifPresent(nominee -> {
            nominee.setName(updatedNominee.getName());

            //  Validate description length
            String desc = updatedNominee.getDescription() != null ? updatedNominee.getDescription().trim() : "";
            if (desc.length() < 10 || desc.length() > 300) {
                throw new IllegalArgumentException("Description must be between 10 and 300 characters.");
            }
            nominee.setDescription(desc);

            //  Update category
            categoryRepository.findById(categoryId).ifPresent(nominee::setCategory);

            //  Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                    String uploadDir = "uploads/nominee-images/";
                    Path path = Paths.get(uploadDir + fileName);
                    Files.createDirectories(path.getParent());
                    Files.write(path, imageFile.getBytes());

                    nominee.setImagePath("/" + uploadDir + fileName);
                } catch (IOException e) {
                    e.printStackTrace(); // log properly in production
                }
            }

            nomineeRepository.save(nominee);
        });
    }

    //  Delete nominee
    public void deleteNominee(Long id) {
        nomineeRepository.deleteById(id);
    }

    //  Get nominees by event & status (for HR dashboard)
    public List<Nominee> getNomineesByStatusAndEvent(Long eventId, String status) {
        if (eventId == null) {
            return List.of(); // No active event
        }
        return nomineeRepository.findByCategoryEventIdAndStatus(eventId, status);
    }

    //  Get categories for a specific event
    public List<Category> getCategoriesByEvent(Long eventId) {
        if (eventId == null) {
            return List.of(); // No active event
        }
        return categoryRepository.findByEventId(eventId);
    }
}
