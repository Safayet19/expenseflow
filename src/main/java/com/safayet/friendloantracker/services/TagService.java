package com.safayet.friendloantracker.services;

import com.safayet.friendloantracker.dto.TagDTO;
import com.safayet.friendloantracker.exception.InvalidOperationException;
import com.safayet.friendloantracker.exception.ResourceNotFoundException;
import com.safayet.friendloantracker.model.Loan;
import com.safayet.friendloantracker.model.Tag;
import com.safayet.friendloantracker.repository.LoanRepository;
import com.safayet.friendloantracker.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final LoanRepository loanRepository;

    public void saveTag(TagDTO tagDTO) {
        Tag tag = isBlank(tagDTO.getId())
                ? new Tag()
                : getRequiredTag(tagDTO.getId());

        tag.setName(tagDTO.getName().trim());
        tag.setDescription(cleanNullable(tagDTO.getDescription()));
        tagRepository.save(tag);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag getTagById(String id) {
        if (isBlank(id)) {
            return null;
        }
        return tagRepository.findById(id).orElse(null);
    }

    public Tag getRequiredTag(String id) {
        if (isBlank(id)) {
            throw new ResourceNotFoundException("Tag not found.");
        }
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found."));
    }

    public Set<Tag> getTagsByIds(Set<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<Tag> tags = tagRepository.findAllById(ids);
        if (tags.size() != new LinkedHashSet<>(ids).size()) {
            throw new InvalidOperationException("One or more selected tags are invalid.");
        }
        return new LinkedHashSet<>(tags);
    }

    public void deleteTag(String id) {
        Tag tag = getRequiredTag(id);
        List<Loan> loans = loanRepository.findAllByTagIdsContaining(id);

        for (Loan loan : loans) {
            loan.getTagIds().remove(id);
        }
        if (!loans.isEmpty()) {
            loanRepository.saveAll(loans);
        }
        tagRepository.delete(tag);
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
