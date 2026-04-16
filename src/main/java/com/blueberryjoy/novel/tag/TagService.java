package com.blueberryjoy.novel.tag;

import com.blueberryjoy.novel.novel.NovelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public Tag createTag(String name) {
        Tag existTag = tagRepository.findByName(name).orElse(null);
        if (existTag == null) {
            Tag newTag = new Tag();
            newTag.setName(name);
            return tagRepository.save(newTag);
        } else {
            return null;
        }
    }
}