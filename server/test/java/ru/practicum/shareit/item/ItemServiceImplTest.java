package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingForCommentNotFoundException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerOrBookerException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {


    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;

    private User user;
    private User anotherUser;
    private Item item;

    private ItemMapper itemMapper;
    private BookingMapper bookingMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    ArgumentCaptor<Item> itemArgumentCaptor;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapper();
        bookingMapper = new BookingMapper();
        itemService = new ItemServiceImpl(itemRepository, userRepository, commentRepository, bookingRepository, itemRequestRepository, itemMapper, bookingMapper);
        user = new User(1, "Mark", "kostrykinmark@gmail.com");
        anotherUser = new User(1, "John", "johndoe@gmail.com");
        item = new Item(1, "Пылесос", "Пылесос", true, user, null);
    }


    @Nested
    class ItemServiceGetAllTests {
        @Test
        void getAll() {
            when(itemRepository.findAll()).thenReturn(List.of(item));
            List<ItemRequestDto> items = itemService.getAll();
            assertEquals(1, items.size());
            assertEquals(item.getId(), items.get(0).getId());
            assertEquals(item.getName(), items.get(0).getName());
            assertEquals(item.getDescription(), items.get(0).getDescription());
            assertEquals(item.getAvailable(), items.get(0).getAvailable());
        }

    }

    @Nested
    class ItemServiceGetAllByOwnerTests {


        @Test
        void getAllByOwner_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            assertThrows(EntityNotFoundException.class,
                    () -> itemService.getAllByOwner(1, 1, 1));
        }

        @Test
        void getAllByOwner_WhenUserFound_thenReturnItems() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(itemRepository.findAllByOwnerFromAndLimit(1, 1, 1)).thenReturn(List.of(item));
            List<ItemResponseDto> ownerItems = itemService.getAllByOwner(1, 1, 1);
            verify(itemRepository, Mockito.times(1)).findAllByOwnerFromAndLimit(1, 1, 1);
            verify(bookingRepository, Mockito.times(1)).findLastBookingDateForItem(1);
            verify(bookingRepository, Mockito.times(1)).findNextBookingDateForItem(1);
            verify(commentRepository, Mockito.times(1)).findAllByItem_Id(1);
            assertEquals(item.getId(), ownerItems.get(0).getId());
            assertEquals(item.getName(), ownerItems.get(0).getName());
            assertEquals(item.getDescription(), ownerItems.get(0).getDescription());
            assertEquals(item.getAvailable(), ownerItems.get(0).getAvailable());
        }
    }


    @Nested
    class ItemServiceGetAllByTextTests {

        @Test
        void getAllByText_WhenUserFound_thenReturnItems() {
            when(itemRepository.findAllByText("пЫлеСос", 1, 1)).thenReturn(List.of(item));
            List<ItemRequestDto> itemsByText = itemService.getAllByText(1, "пЫлеСос", 1, 1);
            verify(itemRepository, Mockito.times(1)).findAllByText("пЫлеСос", 1, 1);
            assertEquals(item.getId(), itemsByText.get(0).getId());
            assertEquals(item.getName(), itemsByText.get(0).getName());
            assertEquals(item.getDescription(), itemsByText.get(0).getDescription());
        }

    }

    @Nested
    class ItemServiceGetByIdTests {
        @Test
        void getById_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
            when(itemRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> itemService.getById(1, 1));
            verify(bookingRepository, Mockito.never()).findLastBookingDateForItem(1);
            verify(bookingRepository, Mockito.never()).findNextBookingDateForItem(1);
            verify(commentRepository, Mockito.never()).findAllByItem_Id(1);
        }

        @Test
        void getAllById_WhenItemFound_thenReturnItem() {
            when(itemRepository.findById(1)).thenReturn(Optional.of(item));
            Booking firstBooking = Booking
                    .builder()
                    .start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(2)).item(item).booker(anotherUser).status(BookingStatus.APPROVED)
                    .build();
            Booking secondBooking = Booking.builder()
                    .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().minusHours(2)).item(item).booker(anotherUser).status(BookingStatus.APPROVED)
                    .build();
            when(bookingRepository.findLastBookingDateForItem(1)).thenReturn(Optional.of(secondBooking));
            when(bookingRepository.findNextBookingDateForItem(1)).thenReturn(Optional.of(firstBooking));
            ItemResponseDto foundItem = itemService.getById(1, 1);
            verify(itemRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.times(1)).findLastBookingDateForItem(1);
            verify(bookingRepository, Mockito.times(1)).findNextBookingDateForItem(1);
            verify(commentRepository, Mockito.times(1)).findAllByItem_Id(1);
            assertEquals(item.getId(), foundItem.getId());
            assertEquals(item.getName(), foundItem.getName());
            assertEquals(item.getDescription(), foundItem.getDescription());
            assertEquals(item.getAvailable(), foundItem.getAvailable());
            assertEquals(bookingMapper.mapToBookingItemView(secondBooking), foundItem.getLastBooking());
            assertEquals(bookingMapper.mapToBookingItemView(firstBooking), foundItem.getNextBooking());
        }

    }

    @Nested
    class ItemServiceAddTests {

        @Test
        void add_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            ItemRequest itemRequest = new ItemRequest(1, "Нужен пылесос", anotherUser, LocalDateTime.now());
            ItemRequestDto itemRequestDto = ItemRequestDto
                    .builder()
                    .id(1).name("Пылесос").description("Пылесос").available(true).requestId(itemRequest.getId())
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> itemService.add(1, itemRequestDto));
            verify(itemRepository, Mockito.never()).save(any(Item.class));
        }

        @Test
        void add_whenItemRequestNotFound_thenEntityNotFoundExceptionThrown() {
            ItemRequest itemRequest = new ItemRequest(1, "Нужен пылесос", anotherUser, LocalDateTime.now());
            ItemRequestDto itemRequestDto = ItemRequestDto
                    .builder()
                    .id(1).name("Пылесос").description("Пылесос").available(true).requestId(itemRequest.getId())
                    .build();
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(itemRequestRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> itemService.add(1, itemRequestDto));
            verify(itemRepository, Mockito.never()).save(any(Item.class));
        }

        @Test
        void add_whenAllIsValid_thenAddAndReturnItem() {
            ItemRequest itemRequest = new ItemRequest(1, "Нужен пылесос", anotherUser, LocalDateTime.now());
            ItemRequestDto itemRequestDto = ItemRequestDto
                    .builder()
                    .id(1).name("Пылесос").description("Пылесос").available(true).requestId(itemRequest.getId())
                    .build();
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequest));
            item.setRequest(itemRequest);
            when(itemRepository.save(any(Item.class))).thenReturn(item);
            ItemRequestDto addedItem = itemService.add(1, itemRequestDto);
            verify(itemRepository, Mockito.times(1)).save(any(Item.class));
            assertEquals(itemRequestDto, addedItem);
        }

    }

    @Nested
    class ItemServiceAddCommentTests {
        @Test
        void addComment_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("Пылесос оказался как раз вовремя, спасибо").build();

            when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> itemService.addComment(1, 1, commentRequestDto));
            verify(commentRepository, Mockito.never()).save(any(Comment.class));
        }

        @Test
        void addComment_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("Пылесос оказался как раз вовремя, спасибо").build();
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(itemRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> itemService.addComment(1, 1, commentRequestDto));
            verify(commentRepository, Mockito.never()).save(any(Comment.class));
        }

        @Test
        void addComment_whenBookingNotFound_thenBookingForCommentNotFoundExceptionThrown() {
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("Пылесос оказался как раз вовремя, спасибо").build();
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(itemRepository.findById(1)).thenReturn(Optional.of(item));
            when(bookingRepository
                    .findAllByBooker_IdAndItem_IdAndStatusAndEndIsBefore(any(Integer.class), any(Integer.class), any(BookingStatus.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            assertThrows(BookingForCommentNotFoundException.class, () -> itemService.addComment(1, 1, commentRequestDto));
            verify(commentRepository, Mockito.never()).save(any(Comment.class));
        }

        @Test
        void addComment_whenAllIsValid_thenAddAndReturnComment() {
            CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                    .text("Пылесос оказался как раз вовремя, спасибо").build();
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(itemRepository.findById(1)).thenReturn(Optional.of(item));
            Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, BookingStatus.WAITING);
            when(bookingRepository
                    .findAllByBooker_IdAndItem_IdAndStatusAndEndIsBefore(any(Integer.class), any(Integer.class), any(BookingStatus.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(booking));
            when(commentRepository.save(any(Comment.class))).thenReturn(
                    new Comment(1, commentRequestDto.getText(), item, user, LocalDateTime.now()));
            CommentResponseDto savedComment = itemService.addComment(1, 1, commentRequestDto);
            verify(commentRepository, Mockito.times(1)).save(any(Comment.class));
            assertEquals(1, savedComment.getId());
            assertEquals(commentRequestDto.getText(), savedComment.getText());
            assertEquals(user.getName(), savedComment.getAuthorName());
        }

    }

    @Nested
    class ItemServiceUpdateTests {
        @Test
        void update_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            ItemRequest itemRequest = new ItemRequest(1, "Нужен пылесос", anotherUser, LocalDateTime.now());
            ItemRequestDto itemRequestDto = ItemRequestDto
                    .builder()
                    .id(1).name("Пылесос").description("Пылесос").available(true).requestId(itemRequest.getId())
                    .build();

            when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> itemService.update(1, 1, itemRequestDto));
            verify(itemRepository, Mockito.never()).save(any(Item.class));
        }

        @Test
        void update_whenUserIdNotEqualsItemOwnerId_thenWrongOwnerOrBookerExceptionThrown() {
            ItemRequest itemRequest = new ItemRequest(1, "Нужен пылесос", user, LocalDateTime.now());
            ItemRequestDto itemRequestDto = ItemRequestDto
                    .builder()
                    .id(1).name("Пылесос").description("Пылесос").available(true).requestId(itemRequest.getId())
                    .build();

            when(itemRepository.findById(1)).thenReturn(Optional.of(item));
            assertThrows(WrongOwnerOrBookerException.class, () -> itemService.update(2, 1, itemRequestDto));
        }

        @Test
        void update_whenItemIsFound_thenUpdateOnlyAvailableFields() {

            Item updateItem = new Item(3, "Кофеварка", "Кофеварка", false, null, null);
            when(itemRepository.findById(1)).thenReturn(Optional.of(item));
            when(itemRepository.save(any(Item.class))).thenReturn(updateItem);
            itemService.update(1, 1, itemMapper.mapToItemDto(updateItem));
            verify(itemRepository).save(itemArgumentCaptor.capture());
            verify(itemRepository, Mockito.times(1)).save(any(Item.class));
            Item savedItem = itemArgumentCaptor.getValue();
            assertNotEquals(updateItem.getId(), savedItem.getId());
            assertEquals(updateItem.getName(), savedItem.getName());
            assertEquals(updateItem.getDescription(), savedItem.getDescription());
            assertEquals(updateItem.getAvailable(), savedItem.getAvailable());
        }

    }

}
