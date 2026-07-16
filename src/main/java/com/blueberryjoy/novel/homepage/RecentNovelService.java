package com.blueberryjoy.novel.homepage;

import com.blueberryjoy.novel.novel.NovelDTO;
import com.blueberryjoy.novel.novel.NovelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RecentNovelService {
    @Autowired
    private NovelRepository novelRepository;

    private volatile List<NovelDTO> novelDTOList;

    @Transactional(readOnly = true)
    public List<NovelDTO> getNovelDTOList() {
        return novelDTOList;
    }
}
