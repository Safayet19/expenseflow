package com.safayet.friendloantracker.services;

import com.safayet.friendloantracker.model.Friend;
import com.safayet.friendloantracker.model.Loan;
import com.safayet.friendloantracker.model.LoanActivity;
import com.safayet.friendloantracker.repository.FriendRepository;
import com.safayet.friendloantracker.repository.LoanActivityRepository;
import com.safayet.friendloantracker.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanActivityService {

    private final LoanActivityRepository loanActivityRepository;
    private final LoanRepository loanRepository;
    private final FriendRepository friendRepository;

    public void recordActivity(Loan loan, String message) {
        LoanActivity activity = new LoanActivity();
        activity.setLoanId(loan.getId());
        activity.setLoan(loan);
        activity.setActivityMessage(message);
        activity.setActivityDateTime(LocalDateTime.now(ZoneId.of("Asia/Dhaka")));
        loanActivityRepository.save(activity);
    }

    public List<LoanActivity> getAllActivities() {
        List<LoanActivity> activities = loanActivityRepository.findAllByOrderByActivityDateTimeDesc();
        hydrateLoans(activities);
        return activities;
    }

    private void hydrateLoans(List<LoanActivity> activities) {
        Set<String> loanIds = activities.stream()
                .map(LoanActivity::getLoanId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Loan> loanMap = new HashMap<>();
        loanRepository.findAllById(loanIds).forEach(loan -> loanMap.put(loan.getId(), loan));

        Set<String> friendIds = loanMap.values().stream()
                .map(Loan::getFriendId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Friend> friendMap = new HashMap<>();
        friendRepository.findAllById(friendIds).forEach(friend -> friendMap.put(friend.getId(), friend));

        loanMap.values().forEach(loan -> loan.setFriend(friendMap.get(loan.getFriendId())));
        activities.forEach(activity -> activity.setLoan(loanMap.get(activity.getLoanId())));
    }
}
