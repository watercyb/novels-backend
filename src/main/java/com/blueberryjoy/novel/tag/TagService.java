package com.blueberryjoy.novel.tag;

import com.blueberryjoy.novel.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Transactional
    public Tag createTag(String name) {
        Tag existTag = tagRepository.findByName(name).orElse(null);
        if (existTag == null) {
            Tag newTag = new Tag();
            newTag.setName(name);
            return tagRepository.save(newTag);
        } else {
            throw new ApiException("Tag already exists", HttpStatus.BAD_REQUEST);
        }
    }

    public List<TagDTO> getAll() {
        return tagRepository.findAllDTO();
    }
}