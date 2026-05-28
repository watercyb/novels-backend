package com.blueberryjoy.novel.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<TagDTO> getAll() {
        return tagRepository.findAllDTO();
    }
}