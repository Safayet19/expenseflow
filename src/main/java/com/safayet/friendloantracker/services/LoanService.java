package com.safayet.friendloantracker.services;

import com.safayet.friendloantracker.dto.LoanDTO;
import com.safayet.friendloantracker.exception.InvalidOperationException;
import com.safayet.friendloantracker.exception.ResourceNotFoundException;
import com.safayet.friendloantracker.model.Friend;
import com.safayet.friendloantracker.model.Loan;
import com.safayet.friendloantracker.model.Tag;
import com.safayet.friendloantracker.repository.ContactLogRepository;
import com.safayet.friendloantracker.repository.FriendRepository;
import com.safayet.friendloantracker.repository.LoanActivityRepository;
import com.safayet.friendloantracker.repository.LoanRepository;
import com.safayet.friendloantracker.repository.ReminderRepository;
import com.safayet.friendloantracker.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final Set<String> ALLOWED_LOAN_TYPES = Set.of(
            "Money", "Book", "Charger", "Laptop", "Umbrella", "Clothes", "Other"
    );
    private static final Set<String> ALLOWED_STATUSES = Set.of("BORROWED", "RETURNED", "OVERDUE");

    private final LoanRepository loanRepository;
    private final FriendRepository friendRepository;
    private final TagRepository tagRepository;
    private final ReminderRepository reminderRepository;
    private final ContactLogRepository contactLogRepository;
    private final LoanActivityRepository loanActivityRepository;
    private final LoanActivityService loanActivityService;

    public void saveLoan(LoanDTO loanDTO) {
        validateLoanRequest(loanDTO);

        Friend friend = friendRepository.findById(loanDTO.getFriendId())
                .orElseThrow(() -> new InvalidOperationException("Selected friend does not exist."));

        Set<String> tagIds = loanDTO.getTagIds() == null
                ? new LinkedHashSet<>()
                : new LinkedHashSet<>(loanDTO.getTagIds());

        List<Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            throw new InvalidOperationException("One or more selected tags are invalid.");
        }

        boolean newLoan = isBlank(loanDTO.getId());
        Loan loan = newLoan
                ? new Loan()
                : loanRepository.findById(loanDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found."));

        loan.setFriendId(friend.getId());
        loan.setFriend(friend);
        loan.setLoanType(loanDTO.getLoanType().trim());
        loan.setItemName(cleanNullable(loanDTO.getItemName()));
        loan.setAmount(loanDTO.getAmount());
        loan.setQuantity(loanDTO.getQuantity());
        loan.setBorrowDate(loanDTO.getBorrowDate());
        loan.setExpectedReturnDate(loanDTO.getExpectedReturnDate());
        loan.setStatus(loanDTO.getStatus().trim().toUpperCase());
        loan.setTagIds(tagIds);
        loan.setTags(new LinkedHashSet<>(tags));
        loan.setLegacyTag(null);
        loan.setNotes(cleanNullable(loanDTO.getNotes()));

        Loan savedLoan = loanRepository.save(loan);
        savedLoan.setFriend(friend);
        savedLoan.setTags(new LinkedHashSet<>(tags));
        loanActivityService.recordActivity(savedLoan, newLoan ? "Loan created" : "Loan updated");
    }

    public List<Loan> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        hydrateBasicRelations(loans);
        return loans;
    }

    public Loan getLoanById(String id) {
        if (isBlank(id)) {
            return null;
        }

        Loan loan = loanRepository.findById(id).orElse(null);
        if (loan == null) {
            return null;
        }

        hydrateBasicRelations(List.of(loan));
        loan.setReminders(reminderRepository.findAllByLoanId(id));
        loan.setContactLogs(contactLogRepository.findAllByLoanId(id));
        loan.setActivities(loanActivityRepository.findAllByLoanIdOrderByActivityDateTimeDesc(id));
        return loan;
    }

    public Loan getRequiredLoan(String id) {
        Loan loan = getLoanById(id);
        if (loan == null) {
            throw new ResourceNotFoundException("Loan not found.");
        }
        return loan;
    }

    public void deleteLoan(String id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found."));

        reminderRepository.deleteAllByLoanId(id);
        contactLogRepository.deleteAllByLoanId(id);
        loanActivityRepository.deleteAllByLoanId(id);
        loanRepository.delete(loan);
    }

    private void validateLoanRequest(LoanDTO loanDTO) {
        if (!ALLOWED_LOAN_TYPES.contains(loanDTO.getLoanType())) {
            throw new InvalidOperationException("Invalid loan type selected.");
        }

        String status = loanDTO.getStatus() == null ? "" : loanDTO.getStatus().trim().toUpperCase();
        if (!ALLOWED_STATUSES.contains(status)) {
            throw new InvalidOperationException("Invalid loan status selected.");
        }

        if (loanDTO.getBorrowDate() != null
                && loanDTO.getExpectedReturnDate() != null
                && loanDTO.getExpectedReturnDate().isBefore(loanDTO.getBorrowDate())) {
            throw new InvalidOperationException("Expected return date cannot be before the borrow date.");
        }
    }

    private void hydrateBasicRelations(List<Loan> loans) {
        Set<String> friendIds = loans.stream()
                .map(Loan::getFriendId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> tagIds = loans.stream()
                .flatMap(loan -> loan.getTagIds() == null ? Set.<String>of().stream() : loan.getTagIds().stream())
                .collect(Collectors.toSet());

        Map<String, Friend> friendMap = new HashMap<>();
        friendRepository.findAllById(friendIds).forEach(friend -> friendMap.put(friend.getId(), friend));

        Map<String, Tag> tagMap = new HashMap<>();
        tagRepository.findAllById(tagIds).forEach(tag -> tagMap.put(tag.getId(), tag));

        for (Loan loan : loans) {
            loan.setFriend(friendMap.get(loan.getFriendId()));

            LinkedHashSet<Tag> tags = new LinkedHashSet<>();
            if (loan.getTagIds() != null) {
                for (String tagId : loan.getTagIds()) {
                    Tag tag = tagMap.get(tagId);
                    if (tag != null) {
                        tags.add(tag);
                    }
                }
            }
            loan.setTags(tags);
        }
    }

    private String cleanNullable(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
