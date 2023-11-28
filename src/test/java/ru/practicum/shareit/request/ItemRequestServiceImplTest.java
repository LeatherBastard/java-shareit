package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PaginationBoundariesException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private ItemMapper itemMapper;
    private ItemRequestMapper itemRequestMapper;

    private User user;
    private ItemRequest itemRequest;

    @Captor
    ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapper();
        itemRequestMapper = new ItemRequestMapper();
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository, itemMapper, itemRequestMapper);
        user = new User(1, "Mark", "kostrykinmark@gmail.com");
        itemRequest = new ItemRequest(1, "Нужен пылесос", user, LocalDateTime.now());
    }

    @Nested
    class ItemRequestServiceAddItemRequestTests {
        @Test
        void add_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            ItemRequestRequestDto itemRequest = new ItemRequestRequestDto("Нужен пылесос");
            when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> itemRequestService.addItemRequest(1, itemRequest));
            verify(itemRequestRepository, Mockito.never()).save(any(ItemRequest.class));
        }

        @Test
        void add_whenAllIsValid_thenAddAndReturnItemRequest() {
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
            when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
            itemRequestService.addItemRequest(1, itemRequestMapper.mapToItemRequestRequestDto(itemRequest));
            verify(itemRequestRepository).save(itemRequestArgumentCaptor.capture());
            ItemRequest addedItemRequest = itemRequestArgumentCaptor.getValue();
            verify(itemRequestRepository, Mockito.times(1)).save(any(ItemRequest.class));
            assertEquals(itemRequest.getDescription(), addedItemRequest.getDescription());
            assertEquals(itemRequest.getRequestor(), addedItemRequest.getRequestor());
        }
    }

    @Nested
    class ItemRequestServiceGetUserItemRequestsTests {
        @Test
        void getUserItemRequests_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> itemRequestService.getUserItemRequests(1));
            verify(itemRequestRepository, Mockito.never()).findAllByRequestor_Id(1);
        }

        @Test
        void getUserItemRequests_whenAllValid_thenReturnAllItemRequests() {
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
            when(itemRequestRepository.findAllByRequestor_Id(1)).thenReturn(List.of(itemRequest));
            List<ItemRequestResponseDto> itemRequests = itemRequestService.getUserItemRequests(1);
            verify(itemRequestRepository, Mockito.times(1)).findAllByRequestor_Id(1);
            assertEquals(1, itemRequests.size());
        }
    }

    @Nested
    class ItemRequestServiceGetAllUsersItemRequestTests {
        @Test
        void getAllUsersItemRequest_whenInvalidFromOrSize_thenPaginationBoundariesExceptionThrown() {
            assertThrows(PaginationBoundariesException.class,
                    () -> itemRequestService.getAllUsersItemRequest(1, -1, -1));
            verify(itemRequestRepository, Mockito.never()).findAllUsersItemRequest(1, -1, -1);
        }

        @Test
        void getUsersItemRequest_whenAllValid_thenReturnAllUserItemRequests() {
            when(itemRequestRepository.findAllUsersItemRequest(1, 1, 1)).thenReturn(List.of(itemRequest));
            List<ItemRequestResponseDto> itemRequests = itemRequestService.getAllUsersItemRequest(1, 1, 1);
            verify(itemRequestRepository, Mockito.times(1)).findAllUsersItemRequest(1, 1, 1);
            assertEquals(1, itemRequests.size());
        }

    }

    @Nested
    class ItemRequestServiceGetItemRequestTests {
        @Test
        void getItemRequest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequest(1, 1));
            verify(itemRequestRepository, Mockito.never()).findById(1);
        }

        @Test
        void getItemRequest_whenItemRequestNotFound_thenEntityNotFoundExceptionThrown() {
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
            assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequest(1, 1));
            verify(userRepository, Mockito.times(1)).findById(1);
            verify(itemRequestRepository, Mockito.times(1)).findById(1);
        }

        @Test
        void getItemRequest_whenAllValid_thenReturnItemRequest() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequest));
            ItemRequestResponseDto foundItemRequest = itemRequestService.getItemRequest(1, 1);
            verify(userRepository, Mockito.times(1)).findById(1);
            verify(itemRequestRepository, Mockito.times(1)).findById(1);
            assertEquals(itemRequest.getId(), foundItemRequest.getId());
            assertEquals(itemRequest.getDescription(), foundItemRequest.getDescription());
            assertEquals(itemRequest.getCreated(), foundItemRequest.getCreated());
        }

    }


}
