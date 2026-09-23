package com.safayet.friendloantracker.services;

import com.safayet.friendloantracker.dto.ContactLogDTO;
import com.safayet.friendloantracker.exception.InvalidOperationException;
import com.safayet.friendloantracker.exception.ResourceNotFoundException;
import com.safayet.friendloantracker.model.ContactLog;
import com.safayet.friendloantracker.model.Friend;
import com.safayet.friendloantracker.model.Loan;
import com.safayet.friendloantracker.repository.ContactLogRepository;
import com.safayet.friendloantracker.repository.FriendRepository;
import com.safayet.friendloantracker.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactLogService {

    private static final Set<String> ALLOWED_METHODS = Set.of(
            "Phone Call", "WhatsApp", "Messenger", "SMS", "In Person", "Other"
    );

    private final ContactLogRepository contactLogRepository;
    private final FriendRepository friendRepository;
    private final LoanRepository loanRepository;

    public void saveContactLog(ContactLogDTO contactLogDTO) {
        if (!ALLOWED_METHODS.contains(contactLogDTO.getContactMethod())) {
            throw new InvalidOperationException("Invalid contact method selected.");
        }

        if (contactLogDTO.getNextContactDate() != null
                && contactLogDTO.getContactDate() != null
                && contactLogDTO.getNextContactDate().isBefore(contactLogDTO.getContactDate())) {
            throw new InvalidOperationException("Next contact date cannot be before the contact date.");
        }

        Friend friend = friendRepository.findById(contactLogDTO.getFriendId())
                .orElseThrow(() -> new InvalidOperationException("Selected friend does not exist."));

        Loan loan = null;
        if (!isBlank(contactLogDTO.getLoanId())) {
            loan = loanRepository.findById(contactLogDTO.getLoanId())
                    .orElseThrow(() -> new InvalidOperationException("Selected loan does not exist."));

            if (!Objects.equals(loan.getFriendId(), friend.getId())) {
                throw new InvalidOperationException("The selected loan does not belong to the selected friend.");
            }
        }

        ContactLog contactLog = isBlank(contactLogDTO.getId())
                ? new ContactLog()
                : contactLogRepository.findById(contactLogDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact log not found."));

        contactLog.setFriendId(friend.getId());
        contactLog.setLoanId(loan == null ? null : loan.getId());
        contactLog.setFriend(friend);
        contactLog.setLoan(loan);
        contactLog.setContactDate(contactLogDTO.getContactDate());
        contactLog.setContactMethod(contactLogDTO.getContactMethod().trim());
        contactLog.setDiscussion(contactLogDTO.getDiscussion().trim());
        contactLog.setFriendResponse(cleanNullable(contactLogDTO.getFriendResponse()));
        contactLog.setNextContactDate(contactLogDTO.getNextContactDate());
        contactLogRepository.save(contactLog);
    }

    public List<ContactLog> getAllContactLogs() {
        List<ContactLog> contactLogs = contactLogRepository.findAll();
        hydrateRelations(contactLogs);
        return contactLogs;
    }

    public ContactLog getContactLogById(String id) {
        if (isBlank(id)) {
            return null;
        }
        ContactLog contactLog = contactLogRepository.findById(id).orElse(null);
        if (contactLog != null) {
            hydrateRelations(List.of(contactLog));
        }
        return contactLog;
    }

    public void deleteContactLog(String id) {
        ContactLog contactLog = contactLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact log not found."));
        contactLogRepository.delete(contactLog);
    }

    private void hydrateRelations(List<ContactLog> contactLogs) {
        Set<String> friendIds = contactLogs.stream()
                .map(ContactLog::getFriendId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> loanIds = contactLogs.stream()
                .map(ContactLog::getLoanId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Friend> friendMap = new HashMap<>();
        friendRepository.findAllById(friendIds).forEach(friend -> friendMap.put(friend.getId(), friend));

        Map<String, Loan> loanMap = new HashMap<>();
        loanRepository.findAllById(loanIds).forEach(loan -> loanMap.put(loan.getId(), loan));

        Set<String> loanFriendIds = loanMap.values().stream()
                .map(Loan::getFriendId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        friendRepository.findAllById(loanFriendIds).forEach(friend -> friendMap.putIfAbsent(friend.getId(), friend));
        loanMap.values().forEach(loan -> loan.setFriend(friendMap.get(loan.getFriendId())));

        contactLogs.forEach(contactLog -> {
            contactLog.setFriend(friendMap.get(contactLog.getFriendId()));
            contactLog.setLoan(loanMap.get(contactLog.getLoanId()));
        });
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
