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
    public TagDTO createTag(String name, String nameCN) {
        if (tagRepository.findByName(name).orElse(null) == null && tagRepository.findByNameCN(nameCN).orElse(null) == null) {
            Tag newTag = new Tag();
            newTag.setName(name);
            newTag.setNameCN(nameCN);
            tagRepository.save(newTag);
            return new TagDTO(newTag);
        } else {
            throw new ApiException("Tag already exists", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public TagDTO update(long id, String name, String nameCN, Integer count) {
        if (tagRepository.findByName(name).orElse(null) != null || tagRepository.findByNameCN(nameCN).orElse(null) != null)
            throw new ApiException("Tag name exists", HttpStatus.BAD_REQUEST);
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag == null)
            throw new ApiException("Tag not found", HttpStatus.BAD_REQUEST);
        if (name == null) name = tag.getName();
        if (nameCN == null) nameCN = tag.getNameCN();
        if (count == null) count = tag.getTagUsage();
        tag.setName(name);
        tag.setNameCN(nameCN);
        tag.setTagUsage(count);
        tagRepository.save(tag);
        return new TagDTO(tag);
    }

    public List<TagDTO> getAll() {
        return tagRepository.findAllDTO();
    }
}