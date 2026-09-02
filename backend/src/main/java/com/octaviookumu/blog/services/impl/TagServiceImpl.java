package com.octaviookumu.blog.services.impl;

import com.octaviookumu.blog.domain.entities.Tag;
import com.octaviookumu.blog.repositories.TagRepository;
import com.octaviookumu.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAllWithPostCount();
    }
}
