package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void clearRepositories() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByItem_Id() {
        User firstUser = userRepository.save(new User(1, "Mark", "kostrykinmark@gmail.com"));
        User secondUser = userRepository.save(new User(2, "John", "johndoe@gmail.com"));
        User thirdUser = userRepository.save(new User(3, "Jane", "janedoe@gmail.com"));
        Item item = itemRepository.save(Item.builder().name("Пылесос").description("Пылесос").owner(firstUser).available(true).build());

        Comment firstComment = commentRepository.save(Comment.builder().text("Отличный пылесос").item(item).author(secondUser).created(LocalDateTime.now()).build());
        Comment secondComment = commentRepository.save(Comment.builder().text("Ужасный пылесос").item(item).author(thirdUser).created(LocalDateTime.now()).build());
        List<Comment> comments = commentRepository.findAllByItem_Id(item.getId()).stream().collect(Collectors.toList());
        assertEquals(2, comments.size());
        assertEquals(firstComment.getText(), comments.get(0).getText());
        assertEquals(secondComment.getText(), comments.get(1).getText());
        assertEquals(firstComment.getItem(), comments.get(0).getItem());
        assertEquals(secondComment.getItem(), comments.get(1).getItem());
        assertEquals(firstComment.getAuthor(), comments.get(0).getAuthor());
        assertEquals(secondComment.getAuthor(), comments.get(1).getAuthor());
        assertEquals(firstComment.getCreated(), comments.get(0).getCreated());
        assertEquals(secondComment.getCreated(), comments.get(1).getCreated());
    }

}
