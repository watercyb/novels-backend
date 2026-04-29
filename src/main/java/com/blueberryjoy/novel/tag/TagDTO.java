package com.blueberryjoy.novel.tag;

import lombok.Getter;

@Getter
public class TagDTO {
    private final long id;
    private final String name;
    private final String nameCN;
    private final int tagUsage;

    public TagDTO(long id, String name, String nameCN, int tagUsage) {
        this.id = id;
        this.name = name;
        this.nameCN = nameCN;
        this.tagUsage = tagUsage;
    }

    public TagDTO(Tag tag) {
        id = tag.getId();
        name = tag.getName();
        nameCN = tag.getNameCN();
        tagUsage = tag.getTagUsage();
    }
}
