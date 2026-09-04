package com.octaviookumu.blog.services.impl;

import com.octaviookumu.blog.domain.entities.Tag;
import com.octaviookumu.blog.repositories.TagRepository;
import com.octaviookumu.blog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAllWithPostCount();
    }

    @Override
    @Transactional
    public List<Tag> createTags(Set<String> tagNames) {
        List<Tag> existingTags = tagRepository.findByNameIn(tagNames);

        Set<String> existingTagNames = existingTags.stream().map(Tag::getName)
                .collect(Collectors.toSet());

        List<Tag> newTags = tagNames.stream()
                .filter(name -> !existingTagNames.contains(name)) // stream of tag names not already in  database
                .map(tagName -> Tag.builder()
                        .name(tagName)
                        .posts(new HashSet<>())
                        .build())
                .toList();

        List<Tag> savedTags = new ArrayList<>();

        if (!newTags.isEmpty()) {
            savedTags = tagRepository.saveAll(newTags);
        }

        // return all the tags. Combine existing with saved tags
        savedTags.addAll(existingTags);

        return savedTags;
    }

    @Transactional
    @Override
    public void deleteTag(UUID id) {
        tagRepository.findById(id).ifPresent(tag -> {
            if (!tag.getPosts().isEmpty()) {
                throw new IllegalStateException("Cannot delete tag with posts");
            }
            tagRepository.deleteById(id);
        });
    }

    @Override
    public Tag getTagById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID " + id));
    }

    @Override
    public List<Tag> getTagsByIds(Set<UUID> ids) {
        List<Tag> foundTags = tagRepository.findAllById(ids);

        // confirm the provided tags exist
        if (foundTags.size() != ids.size()) {
            throw new EntityNotFoundException("Not all specified tag IDs exist");
        }
        return foundTags;
    }


}
