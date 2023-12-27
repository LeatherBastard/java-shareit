package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
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
        UserDto savedUser = userService.add(userDto);
        ItemRequestDto savedFirstItem = itemService.add(savedUser.getId(), firstItem);
        ItemRequestDto savedSecondItem = itemService.add(savedUser.getId(), secondItem);

        List<ItemResponseDto> items = itemService.getAllByOwner(savedUser.getId(), 0, 3);
        assertEquals(2, items.size());
        assertEquals(savedFirstItem.getId(), items.get(0).getId());
        assertEquals(savedFirstItem.getName(), items.get(0).getName());
        assertEquals(savedFirstItem.getDescription(), items.get(0).getDescription());
        assertEquals(savedFirstItem.getAvailable(), items.get(0).getAvailable());

        assertEquals(savedSecondItem.getId(), items.get(1).getId());
        assertEquals(savedSecondItem.getName(), items.get(1).getName());
        assertEquals(savedSecondItem.getDescription(), items.get(1).getDescription());
        assertEquals(savedSecondItem.getAvailable(), items.get(1).getAvailable());
    }


    @Test
    void getAllByOwner_whenUserNotFound_thenEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> itemService.getAllByOwner(1, 0, 3));
    }
}
