package com.safayet.friendloantracker.services;

import com.safayet.friendloantracker.dto.ReminderDTO;
import com.safayet.friendloantracker.exception.InvalidOperationException;
import com.safayet.friendloantracker.exception.ResourceNotFoundException;
import com.safayet.friendloantracker.model.Friend;
import com.safayet.friendloantracker.model.Loan;
import com.safayet.friendloantracker.model.Reminder;
import com.safayet.friendloantracker.repository.FriendRepository;
import com.safayet.friendloantracker.repository.LoanRepository;
import com.safayet.friendloantracker.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Dhaka");

    private final ReminderRepository reminderRepository;
    private final FriendRepository friendRepository;
    private final LoanRepository loanRepository;

    public void saveReminder(ReminderDTO reminderDTO) {
        validateReminderDateTime(reminderDTO);

        Friend friend = friendRepository.findById(reminderDTO.getFriendId())
                .orElseThrow(() -> new InvalidOperationException("Selected friend does not exist."));

        Loan loan = null;
        if (!isBlank(reminderDTO.getLoanId())) {
            loan = loanRepository.findById(reminderDTO.getLoanId())
                    .orElseThrow(() -> new InvalidOperationException("Selected loan does not exist."));

            if (!Objects.equals(loan.getFriendId(), friend.getId())) {
                throw new InvalidOperationException("The selected loan does not belong to the selected friend.");
            }
        }

        Reminder reminder = isBlank(reminderDTO.getId())
                ? new Reminder()
                : reminderRepository.findById(reminderDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found."));

        reminder.setFriendId(friend.getId());
        reminder.setLoanId(loan == null ? null : loan.getId());
        reminder.setFriend(friend);
        reminder.setLoan(loan);
        reminder.setReminderDate(reminderDTO.getReminderDate());
        reminder.setReminderTime(reminderDTO.getReminderTime());
        reminder.setMessage(reminderDTO.getMessage().trim());
        reminder.setCompleted(reminderDTO.isCompleted());
        reminderRepository.save(reminder);
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = reminderRepository.findAll();
        hydrateRelations(reminders);
        return reminders;
    }

    public Reminder getReminderById(String id) {
        if (isBlank(id)) {
            return null;
        }
        Reminder reminder = reminderRepository.findById(id).orElse(null);
        if (reminder != null) {
            hydrateRelations(List.of(reminder));
        }
        return reminder;
    }

    public void markCompleted(String id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found."));
        reminder.setCompleted(true);
        reminderRepository.save(reminder);
    }

    public void deleteReminder(String id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found."));
        reminderRepository.delete(reminder);
    }

    private void validateReminderDateTime(ReminderDTO reminderDTO) {
        if (reminderDTO.isCompleted()
                || reminderDTO.getReminderDate() == null
                || reminderDTO.getReminderTime() == null) {
            return;
        }

        LocalDate today = LocalDate.now(APP_ZONE);
        if (reminderDTO.getReminderDate().isBefore(today)) {
            throw new InvalidOperationException("Pending reminder date cannot be in the past.");
        }

        if (reminderDTO.getReminderDate().isEqual(today)) {
            LocalTime now = LocalTime.now(APP_ZONE).withSecond(0).withNano(0);
            if (reminderDTO.getReminderTime().isBefore(now)) {
                throw new InvalidOperationException("Pending reminder time cannot already be in the past.");
            }
        }
    }

    private void hydrateRelations(List<Reminder> reminders) {
        Set<String> friendIds = reminders.stream()
                .map(Reminder::getFriendId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> loanIds = reminders.stream()
                .map(Reminder::getLoanId)
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

        reminders.forEach(reminder -> {
            reminder.setFriend(friendMap.get(reminder.getFriendId()));
            reminder.setLoan(loanMap.get(reminder.getLoanId()));
        });
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
