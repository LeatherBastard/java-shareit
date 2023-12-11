package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Test
    void getAllUsersItemRequest() {
        UserDto user = new UserDto(1, "Mark", "kostrykinmark@gmail.com");
        UserDto secondUser = new UserDto(2, "John", "johndoe@gmail.com");
        ItemRequestRequestDto firstRequest = new ItemRequestRequestDto("Нужен пылесос");
        ItemRequestRequestDto secondRequest = new ItemRequestRequestDto("Нужна кофемашина");
        UserDto savedFirstUser = userService.add(user);
        UserDto savedSecondUser = userService.add(secondUser);
        ItemRequestResponseDto savedFirstItemRequest = itemRequestService.addItemRequest(savedFirstUser.getId(), firstRequest);
        ItemRequestResponseDto savedSecondItemRequest = itemRequestService.addItemRequest(savedFirstUser.getId(), secondRequest);

        List<ItemRequestResponseDto> itemRequests = itemRequestService.getAllUsersItemRequest(savedSecondUser.getId(), 0, 2);

        assertEquals(2, itemRequests.size());
        assertEquals(savedSecondItemRequest.getDescription(), itemRequests.get(0).getDescription());
        assertEquals(savedFirstItemRequest.getDescription(), itemRequests.get(1).getDescription());
    }

}
