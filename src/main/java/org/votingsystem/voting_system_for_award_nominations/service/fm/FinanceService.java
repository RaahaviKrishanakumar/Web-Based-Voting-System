package org.votingsystem.voting_system_for_award_nominations.service.fm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Budget;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Expense;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Sponsor;
import org.votingsystem.voting_system_for_award_nominations.repository.BudgetRepository;
import org.votingsystem.voting_system_for_award_nominations.repository.ExpenseRepository;
import org.votingsystem.voting_system_for_award_nominations.repository.SponsorRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinanceService {

    private final SponsorRepository sponsorRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    // ✅ The report-related dependencies have been removed.
    @Autowired
    public FinanceService(SponsorRepository sponsorRepository,
                          BudgetRepository budgetRepository,
                          ExpenseRepository expenseRepository) {
        this.sponsorRepository = sponsorRepository;
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    // ---------- Sponsors ----------
    public List<Sponsor> listSponsors(Long eventId) {
        return sponsorRepository.findByEventIdOrderByIdDesc(eventId);
    }

    public void addSponsor(Event event, String name, BigDecimal amount, MultipartFile agreementFile) {
        Sponsor s = new Sponsor();
        s.setEvent(event);
        s.setName(name);
        s.setAmount(amount);
        s.setCreatedAt(LocalDateTime.now());
        if (agreementFile != null && !agreementFile.isEmpty()) {
            String savedPath = saveFile(agreementFile, "uploads/sponsors/");
            s.setAgreementFilePath("/" + savedPath.replace("\\", "/"));
        }
        sponsorRepository.save(s);
    }

    public void updateSponsor(Long id, String name, BigDecimal amount) {
        Sponsor s = sponsorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        s.setName(name);
        s.setAmount(amount);
        sponsorRepository.save(s);
    }

    public void deleteSponsor(Long sponsorId) {
        sponsorRepository.deleteById(sponsorId);
    }

    // ---------- Budgets ----------
    public List<Budget> listBudgets(Long eventId) {
        return budgetRepository.findByEventId(eventId);
    }

    public void addBudget(Event event, String category, BigDecimal allocatedAmount) {
        Budget b = new Budget();
        b.setEvent(event);
        b.setCategory(category);
        b.setAllocatedAmount(allocatedAmount);
        b.setCreatedAt(LocalDateTime.now());
        budgetRepository.save(b);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    // ---------- Expenses ----------
    public List<Expense> listExpenses(Long eventId) {
        return expenseRepository.findByBudgetEventId(eventId);
    }

    public void addExpense(Long budgetId, String description, BigDecimal amount) {
        Budget b = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        Expense e = new Expense();
        e.setBudget(b);
        e.setDescription(description);
        e.setAmount(amount);
        e.setCreatedAt(LocalDateTime.now());
        expenseRepository.save(e);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    // ✅ The generateFinancialSummaryReport method has been removed.

    // ---------- File Helper ----------
    private String saveFile(MultipartFile file, String baseDir) {
        try {
            Path dir = Path.of(baseDir);
            Files.createDirectories(dir);
            String safeName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path out = dir.resolve(safeName);
            Files.write(out, file.getBytes());
            return baseDir + safeName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}