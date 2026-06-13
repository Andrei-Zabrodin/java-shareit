package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Collection<Comment> findByItemIdOrderByCreatedDesc(long itemId);

    Collection<Comment> findByItemIdInOrderByCreatedDesc(Collection<Long> itemIds);
}
