package com.example.sep492_be.dto.response;

import com.example.sep492_be.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private UUID id;
    private UserResponse user;
    private String content;
    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.user = new UserResponse(comment.getUser());
        this.content = comment.getContent();
    }
}
