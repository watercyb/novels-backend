package com.blueberryjoy.novel.comment;

import com.blueberryjoy.novel.novel.Novel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Novel, Long> {
}