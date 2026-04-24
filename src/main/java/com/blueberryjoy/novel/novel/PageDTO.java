package com.blueberryjoy.novel.novel;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageDTO<T> {
    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalItems;
    private final int totalPages;

    public PageDTO(Page<T> pageData) {
        this.items = pageData.getContent();
        this.page = pageData.getNumber();
        this.size = pageData.getSize();
        this.totalItems = pageData.getTotalElements();
        this.totalPages = pageData.getTotalPages();
    }
}
