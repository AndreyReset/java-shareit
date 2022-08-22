package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentAddingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment toComment(CommentAddingDto commentAdding, Item item, User user) {
        Comment comment = new Comment();
        comment.setText(commentAdding.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        return comment;
    }

    public static Set<CommentDto> toDto(Item item) {
        Set<CommentDto> commentDto = new HashSet<>();
        for (Comment comment : item.getComment()) {
            commentDto.add(toDto(comment));
        }
        return commentDto;
    }

    public static CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}
