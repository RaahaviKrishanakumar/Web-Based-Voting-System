package org.votingsystem.voting_system_for_award_nominations.service.pro;

import org.springframework.stereotype.Service;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Feedback;
import org.votingsystem.voting_system_for_award_nominations.modelentity.User;
import org.votingsystem.voting_system_for_award_nominations.repository.FeedbackRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // List all feedback for an event
    public List<Feedback> getFeedbackForEvent(Long eventId) {
        return feedbackRepository.findByEventId(eventId);
    }

    // List feedback for a specific user in an event
    public List<Feedback> getUserFeedback(Long eventId, Long userId) {
        return feedbackRepository.findByEventIdAndUserId(eventId, userId);
    }

    // Add new feedback with validation
    public Feedback addFeedback(Event event, User user, String message, int rating) {
        String cleaned = (message == null ? "" : message.trim());
        if (cleaned.length() < 10 || cleaned.length() > 500) {
            throw new IllegalArgumentException("Feedback must be between 10 and 500 characters.");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Please select a star rating (1–5).");
        }

        Feedback fb = new Feedback();
        fb.setEvent(event);
        fb.setUser(user);
        fb.setMessage(cleaned);
        fb.setRating(rating);
        fb.setCreatedAt(LocalDateTime.now());
        fb.setUpdatedAt(LocalDateTime.now());
        return feedbackRepository.save(fb);
    }

    // Edit feedback (only by owner, with validation)
    public Feedback editFeedback(Long feedbackId, User user, String newMessage, int newRating) {
        Feedback fb = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        if (!fb.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only edit your own feedback");
        }

        String cleaned = (newMessage == null ? "" : newMessage.trim());
        if (cleaned.length() < 10 || cleaned.length() > 500) {
            throw new IllegalArgumentException("Feedback must be between 10 and 500 characters.");
        }
        if (newRating < 1 || newRating > 5) {
            throw new IllegalArgumentException("Please select a star rating (1–5).");
        }

        fb.setMessage(cleaned);
        fb.setRating(newRating);
        fb.setUpdatedAt(LocalDateTime.now());
        return feedbackRepository.save(fb);
    }

    // Delete feedback (only by owner)
    public void deleteFeedback(Long feedbackId, User user) {
        Feedback fb = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        if (!fb.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own feedback");
        }
        feedbackRepository.delete(fb);
    }

    public void deleteFeedbackByAdmin(Long feedbackId) {
        if (!feedbackRepository.existsById(feedbackId)) {
            throw new RuntimeException("Feedback not found with ID: " + feedbackId);
        }
        feedbackRepository.deleteById(feedbackId);
    }

    // Admin/PRO reply
    public Feedback replyToFeedback(Long feedbackId, String reply) {
        Feedback fb = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        fb.setReply(reply);
        fb.setUpdatedAt(LocalDateTime.now());
        return feedbackRepository.save(fb);
    }
}
