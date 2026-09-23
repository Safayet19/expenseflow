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
        Friend friend = isBlank(friendDTO.getId())
                ? new Friend()
                : getRequiredFriend(friendDTO.getId());

        friend.setName(friendDTO.getName().trim());
        friend.setPhone(friendDTO.getPhone().trim());
        friend.setEmail(cleanNullable(friendDTO.getEmail()));

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
