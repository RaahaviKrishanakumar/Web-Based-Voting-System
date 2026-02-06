package org.votingsystem.voting_system_for_award_nominations.service.coo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.votingsystem.voting_system_for_award_nominations.modelentity.*;
import org.votingsystem.voting_system_for_award_nominations.repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CooService {

    private final VotingSessionRepository votingSessionRepository;
    private final CategoryRepository categoryRepository;
    private final NomineeRepository nomineeRepository;
    private final VoteRepository voteRepository;
    private final WinnerRepository winnerRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CooService(VotingSessionRepository votingSessionRepository,
                      CategoryRepository categoryRepository,
                      NomineeRepository nomineeRepository,
                      VoteRepository voteRepository,
                      WinnerRepository winnerRepository,
                      EventRepository eventRepository) {
        this.votingSessionRepository = votingSessionRepository;
        this.categoryRepository = categoryRepository;
        this.nomineeRepository = nomineeRepository;
        this.voteRepository = voteRepository;
        this.winnerRepository = winnerRepository;
        this.eventRepository = eventRepository;
    }

    //  Start a new session
    @Transactional
    public void startSession(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("❌ Event not found"));

        boolean hasPending = nomineeRepository.findByCategoryEventId(eventId).stream()
                .anyMatch(n -> "PENDING".equalsIgnoreCase(n.getStatus()));
        if (hasPending) {
            throw new RuntimeException("❌ Cannot start session: Some nominees are still pending approval.");
        }

        boolean exists = votingSessionRepository.findTopByEvent_IdOrderByIdDesc(eventId).isPresent();
        if (exists) {
            throw new RuntimeException("❌ A session already exists for this event!");
        }

        VotingSession session = new VotingSession();
        session.setEvent(event);
        session.setStatus("ONGOING");
        votingSessionRepository.save(session);
    }

    //  Restart session
    @Transactional
    public void restartSession(Long eventId, boolean clearVotes) {
        VotingSession session = votingSessionRepository.findTopByEvent_IdOrderByIdDesc(eventId)
                .orElseThrow(() -> new RuntimeException("❌ Session not found"));

        if ("ENDED".equalsIgnoreCase(session.getStatus())) {
            throw new RuntimeException("❌ Cannot restart: Session already ended.");
        }

        if (clearVotes) {
            voteRepository.deleteByEvent_Id(eventId);
        }

        session.setStatus("ONGOING");
        votingSessionRepository.save(session);
    }

    //  End session
    @Transactional
    public void endSession(Long eventId) {
        VotingSession session = votingSessionRepository.findTopByEvent_IdOrderByIdDesc(eventId)
                .orElseThrow(() -> new RuntimeException("❌ Session not found"));

        if ("ENDED".equalsIgnoreCase(session.getStatus())) {
            throw new RuntimeException("❌ Session already ended.");
        }

        session.setStatus("ENDED");
        votingSessionRepository.save(session);
    }

    //  Live results
    public List<Map<String, Object>> getLiveResultsWithNames(Long categoryId) {
        List<Nominee> approved = nomineeRepository.findByCategoryIdAndStatus(categoryId, "APPROVED");

        Map<Long, Long> counts = approved.stream()
                .collect(Collectors.toMap(Nominee::getId, n -> 0L));

        List<Object[]> raw = voteRepository.countVotesByNomineeIncludingZero(categoryId);
        for (Object[] row : raw) {
            Long nomineeId = (Long) row[0];
            Long count = (Long) row[1];
            if (counts.containsKey(nomineeId)) {
                counts.put(nomineeId, count);
            }
        }

        List<Map<String, Object>> response = new ArrayList<>();
        for (Nominee n : approved) {
            Map<String, Object> map = new HashMap<>();
            map.put("nominee", n.getName());
            map.put("votes", counts.getOrDefault(n.getId(), 0L));
            response.add(map);
        }
        return response;
    }

    //  Save winner (Updated Method)
    @Transactional
    public void saveWinner(Long categoryId, Long nomineeId, MultipartFile imageFile) throws IOException {
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        Nominee nominee = nomineeRepository.findById(nomineeId).orElseThrow();
        Event event = category.getEvent();

        var session = votingSessionRepository.findTopByEvent_IdOrderByIdDesc(event.getId())
                .orElseThrow(() -> new RuntimeException("❌ No session found for this event."));

        if (!"ENDED".equalsIgnoreCase(session.getStatus())) {
            throw new RuntimeException("⚠️ Cannot declare winner until the voting session ends.");
        }

        if (winnerRepository.existsByEvent_IdAndCategory_Id(event.getId(), categoryId)) {
            throw new RuntimeException("❌ A winner is already declared for this category.");
        }

        Winner winner = new Winner();
        winner.setCategory(category);
        winner.setNominee(nominee);
        winner.setEvent(event);
        winner.setStatus("DECLARED"); // Set the initial status

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            String uploadDir = "uploads/winners/";
            java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.write(path, imageFile.getBytes());
            winner.setImagePath("/" + uploadDir + fileName);
        }
        winnerRepository.save(winner);
    }

    // NEW METHOD START
    /**
     * Finds all winners for an event with a "DECLARED" status and updates them to "PENDING_REVIEW".
     * This effectively submits them to the PRO.
     * @param eventId The ID of the event for which to submit winners.
     */
    @Transactional
    public void submitWinnersForReview(Long eventId) {
        List<Winner> declaredWinners = winnerRepository.findByEvent_IdAndStatus(eventId, "DECLARED");

        if (declaredWinners.isEmpty()) {
            throw new RuntimeException("There are no new winners to submit for review.");
        }

        for (Winner winner : declaredWinners) {
            winner.setStatus("PENDING_REVIEW");
        }
        winnerRepository.saveAll(declaredWinners);
    }
    // NEW METHOD END


    //  Get winners for an event
    public List<Winner> getWinnersForEvent(Long eventId) {
        return winnerRepository.findByEvent_Id(eventId);
    }

    //  Delete a winner
    @Transactional
    public void deleteWinner(Long winnerId) {
        winnerRepository.deleteById(winnerId);
    }

    //  Update a winner
    @Transactional
    public void updateWinner(Long id, Long categoryId, Long nomineeId, MultipartFile imageFile) throws IOException {
        Winner winner = winnerRepository.findById(id).orElseThrow();

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElseThrow();
            winner.setCategory(category);
            winner.setEvent(category.getEvent());
        }
        if (nomineeId != null) {
            Nominee nominee = nomineeRepository.findById(nomineeId).orElseThrow();
            winner.setNominee(nominee);
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            String uploadDir = "uploads/winners/";
            java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.write(path, imageFile.getBytes());
            winner.setImagePath("/" + uploadDir + fileName);
        }
        winnerRepository.save(winner);
    }

    //  Check session status
    public String getSessionStatus(Long eventId) {
        return votingSessionRepository.findTopByEvent_IdOrderByIdDesc(eventId)
                .map(VotingSession::getStatus)
                .orElse("NOT_STARTED");
    }
}
