package com.safayet.friendloantracker.services;

import com.safayet.friendloantracker.dto.FriendDTO;
import com.safayet.friendloantracker.exception.InvalidOperationException;
import com.safayet.friendloantracker.exception.ResourceNotFoundException;
import com.safayet.friendloantracker.model.Address;
import com.safayet.friendloantracker.model.Friend;
import com.safayet.friendloantracker.model.Loan;
import com.safayet.friendloantracker.repository.ContactLogRepository;
import com.safayet.friendloantracker.repository.FriendRepository;
import com.safayet.friendloantracker.repository.LoanRepository;
import com.safayet.friendloantracker.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final LoanRepository loanRepository;
    private final ReminderRepository reminderRepository;
    private final ContactLogRepository contactLogRepository;

    public void saveFriend(FriendDTO friendDTO) {
        String id = friendDTO.getId();
        String phone = friendDTO.getPhone().trim();
        String email = cleanNullable(friendDTO.getEmail());
        if (email != null) {
            email = email.toLowerCase(Locale.ROOT);
        }

        boolean duplicatePhone = isBlank(id)
                ? friendRepository.existsByPhone(phone)
                : friendRepository.existsByPhoneAndIdNot(phone, id);
        if (duplicatePhone) {
            throw new InvalidOperationException("Another friend already uses this phone number.");
        }

        if (email != null) {
            boolean duplicateEmail = isBlank(id)
                    ? friendRepository.existsByEmailIgnoreCase(email)
                    : friendRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
            if (duplicateEmail) {
                throw new InvalidOperationException("Another friend already uses this email address.");
            }
        }

        Friend friend = isBlank(id) ? new Friend() : getRequiredFriend(id);
        friend.setName(friendDTO.getName().trim());
        friend.setPhone(phone);
        friend.setEmail(email);

        Address address = friendDTO.getAddress() == null ? new Address() : friendDTO.getAddress();
        address.setFullAddress(cleanNullable(address.getFullAddress()));
        friend.setAddress(address);
        friend.setNotes(cleanNullable(friendDTO.getNotes()));

        friendRepository.save(friend);
    }

    public List<Friend> getAllFriends() {
        List<Friend> friends = friendRepository.findAll();
        List<Loan> loans = loanRepository.findAll();

        Map<String, List<Loan>> loansByFriend = loans.stream()
                .filter(loan -> loan.getFriendId() != null)
                .collect(Collectors.groupingBy(Loan::getFriendId));

        friends.forEach(friend -> friend.setLoans(
                new ArrayList<>(loansByFriend.getOrDefault(friend.getId(), List.of()))
        ));
        return friends;
    }

    public Friend getFriendById(String id) {
        if (isBlank(id)) {
            return null;
        }
        return friendRepository.findById(id).orElse(null);
    }

    public Friend getRequiredFriend(String id) {
        if (isBlank(id)) {
            throw new ResourceNotFoundException("Friend not found.");
        }
        return friendRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found."));
    }

    public void deleteFriend(String id) {
        Friend friend = getRequiredFriend(id);

        if (loanRepository.existsByFriendId(id)
                || reminderRepository.existsByFriendId(id)
                || contactLogRepository.existsByFriendId(id)) {
            throw new InvalidOperationException(
                    "This friend has related loans, reminders, or contact logs. Remove those records first."
            );
        }

        friendRepository.delete(friend);
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
