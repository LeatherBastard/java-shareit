package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PaginationBoundariesException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getAllByOwner() {
        UserDto userDto = new UserDto(1, "Mark", "kostrykinmark@gmail.com");
        ItemRequestDto firstItem = ItemRequestDto
                .builder().id(1).name("Пылесос").description("Пылесос").available(true).build();
        ItemRequestDto secondItem = ItemRequestDto
                .builder().id(2).name("Кофеварка").description("Кофеварка").available(false).build();
        userService.add(userDto);
        itemService.add(userDto.getId(), firstItem);
        itemService.add(userDto.getId(), secondItem);

        List<ItemResponseDto> items = itemService.getAllByOwner(userDto.getId(), 0, 3);
        assertEquals(2, items.size());
        assertEquals(firstItem.getId(), items.get(0).getId());
        assertEquals(firstItem.getName(), items.get(0).getName());
        assertEquals(firstItem.getDescription(), items.get(0).getDescription());
        assertEquals(firstItem.getAvailable(), items.get(0).getAvailable());

        assertEquals(secondItem.getId(), items.get(1).getId());
        assertEquals(secondItem.getName(), items.get(1).getName());
        assertEquals(secondItem.getDescription(), items.get(1).getDescription());
        assertEquals(secondItem.getAvailable(), items.get(1).getAvailable());
    }

    @Test
    void getAllByOwner_whenFromOrSizeWrong_thenPaginationBoundariesException() {
        assertThrows(PaginationBoundariesException.class, () -> itemService.getAllByOwner(1, -1, 3));
    }

    @Test
    void getAllByOwner_whenUserNotFound_thenEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> itemService.getAllByOwner(1, 0, 3));
    }
}
