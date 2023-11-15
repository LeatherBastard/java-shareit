package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingForCommentNotFoundException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerOrBookerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentView;
import ru.practicum.shareit.item.dto.ItemBookingDatesView;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    public static final String ITEM_NOT_FOUND_MESSAGE = "Item with id %d not found";
    private static final String WRONG_OWNER_MESSAGE = "You are not an owner ot this item!";

    private static final String BOOKING_FOR_COMMENT_NOT_FOUND_EXCEPTION_MESSAGE = " You have not booked item %d to comment";

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;


    @Override
    public List<ItemDto> getAll() {
        return itemRepository.findAll().stream().map(itemMapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemBookingDatesView> getAllByOwner(Integer ownerId) {
        Optional<User> optionalUser = userRepository.findById(ownerId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, ownerId);
        List<Item> ownerItems = itemRepository.findAllByOwner(optionalUser.get());
        List<ItemBookingDatesView> items = ownerItems.stream()
                .map(this::setBookingDatesToItem).collect(Collectors.toList());
        items.forEach(item ->
                item.setComments(commentRepository
                        .findAllByItem_Id(item.getId())
                        .stream()
                        .map(itemMapper::mapToCommentView)
                        .collect(Collectors.toSet())
                )
        );
        return items;
    }

    @Override
    public List<ItemDto> getAllByText(String text) {
        if (text.isEmpty())
            return new ArrayList<>();
        return itemRepository.findAllByText(text).stream().map(itemMapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemBookingDatesView getById(Integer userId, Integer itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new EntityNotFoundException(ITEM_NOT_FOUND_MESSAGE, itemId);
        Item itemById = optionalItem.get();

        ItemBookingDatesView result;
        if (itemById.getOwner().getId() != userId) {
            result = itemMapper.mapToItemBookingDatesView(itemById);
        } else {
            result = setBookingDatesToItem(itemById);
        }
        result.setComments(commentRepository
                .findAllByItem_Id(itemId)
                .stream()
                .map(itemMapper::mapToCommentView).collect(Collectors.toSet()));

        return result;

    }

    @Override
    public ItemDto add(Integer ownerId, ItemDto itemDto) {
        Optional<User> optionalUser = userRepository.findById(ownerId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, ownerId);
        Item item = itemMapper.mapToItem(itemDto);
        item.setOwner(optionalUser.get());
        return itemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public CommentView addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new EntityNotFoundException(ITEM_NOT_FOUND_MESSAGE, itemId);

        List<Booking> bookings = bookingRepository.findAllByBooker_IdAndItem_IdAndStatusAndEndIsBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {

            throw new BookingForCommentNotFoundException(BOOKING_FOR_COMMENT_NOT_FOUND_EXCEPTION_MESSAGE, itemId);
        }
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(optionalItem.get())
                .author(optionalUser.get())
                .created(LocalDateTime.now())
                .build();

        return itemMapper.mapToCommentView(commentRepository.save(comment));
    }

    @Override
    public ItemDto update(Integer ownerId, Integer itemId, ItemDto item) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new EntityNotFoundException(ITEM_NOT_FOUND_MESSAGE, itemId);
        Item oldItem = optionalItem.get();
        if (oldItem.getOwner().getId() != ownerId) {
            throw new WrongOwnerOrBookerException(WRONG_OWNER_MESSAGE);
        }
        if (item.getName() != null)
            oldItem.setName(item.getName());
        if (item.getDescription() != null)
            oldItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            oldItem.setAvailable(item.getAvailable());
        return itemMapper.mapToItemDto(itemRepository.save(oldItem));
    }

    private ItemBookingDatesView setBookingDatesToItem(Item item) {

        Optional<Booking> lastBooking = bookingRepository.findLastBookingDateForItem(item.getId());
        Optional<Booking> nextBooking = bookingRepository.findNextBookingDateForItem(item.getId());

        ItemBookingDatesView itemView = itemMapper.mapToItemBookingDatesView(item);
        if (lastBooking.isPresent())
            itemView.setLastBooking(bookingMapper.mapToBookingItemView(lastBooking.get()));
        if (nextBooking.isPresent())
            itemView.setNextBooking(bookingMapper.mapToBookingItemView(nextBooking.get()));
        return itemView;
    }
}
