package ru.practicum.shareit.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;


    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, mapper);
    }


    @Nested
    class UserServiceAddTests {
        @Test
        void add_whenUserIsValid_thenReturnUser() {
            User expectedUser = User.builder().id(1).name("Mark").email("kostrykinmark@gmail.com").build();
            when(userRepository.save(any(User.class))).thenReturn(expectedUser);
            assertEquals(mapper.mapToUserDto(expectedUser), userService.add(mapper.mapToUserDto(expectedUser)));
        }
    }

    @Nested
    class UserServiceGetAllTests {
        @Test
        void getAll_whenThereAreUsers_thenReturnUsers() {
            User firstUser = User.builder().id(1).name("Mark").email("kostrykinmark@gmail.com").build();
            User secondUser = User.builder().id(2).name("John").email("johndoe@gmail.com").build();
            List<User> expectedUsers = List.of(firstUser, secondUser);
            when(userRepository.findAll()).thenReturn(expectedUsers);
            List<UserDto> expectedDtoUsers = expectedUsers.stream().map(mapper::mapToUserDto).collect(Collectors.toList());
            assertEquals(expectedDtoUsers, userService.getAll());
        }
    }

    @Nested
    class UserServiceGetByIdTests {
        @Test
        void getById_whenThereIsUser_thenReturnUser() {
            User expectedUser = User.builder().id(1).name("Mark").email("kostrykinmark@gmail.com").build();
            when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));
            User actualUser = mapper.mapToUser(userService.getById(expectedUser.getId()));
            assertEquals(expectedUser, actualUser);
        }

        @Test
        void getById_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            when(userRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> userService.getById(1));
        }
    }


    @Nested
    class UserServiceUpdateTests {
        @Test
        void update_whenUserIsFound_thenUpdateOnlyAvailableFields() {
            User oldUser = User.builder().id(1).name("Mark").email("kostrykinmark@gmail.com").build();
            User updatedUser = User.builder().id(1).name("John").email("johndoe@gmail.com").build();
            when(userRepository.findById(oldUser.getId())).thenReturn(Optional.of(oldUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            userService.update(oldUser.getId(), mapper.mapToUserDto(updatedUser));
            verify(userRepository).save(userArgumentCaptor.capture());
            User savedUser = userArgumentCaptor.getValue();
            assertEquals(updatedUser.getName(), savedUser.getName());
            assertEquals(updatedUser.getEmail(), savedUser.getEmail());
        }

        @Test
        void update_whenUserIsNotFound_thenEntityNotFoundExceptionThrown() {
            User oldUser = User.builder().id(1).name("Mark").email("kostrykinmark@gmail.com").build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> userService.update(1, mapper.mapToUserDto(oldUser)));
            verify(userRepository, Mockito.times(1)).findById(oldUser.getId());
            verify(userRepository, Mockito.never()).save(oldUser);
        }

    }

    @Nested
    class UserServiceRemoveTests {
        @Test
        void remove_whenUserIsFound_thenRemoveUser() {
            User expectedUser = User.builder().id(1).name("Mark").email("kostrykinmark@gmail.com").build();
            when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));
            userService.remove(expectedUser.getId());
            verify(userRepository, Mockito.times(1)).findById(expectedUser.getId());
            verify(userRepository, Mockito.times(1)).delete(expectedUser);
        }

        @Test
        void remove_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            User expectedUser = User.builder().id(1).name("Mark").email("kostrykinmark@gmail.com").build();
            assertThrows(EntityNotFoundException.class, () -> userService.remove(expectedUser.getId()));
            verify(userRepository, Mockito.times(1)).findById(expectedUser.getId());
            verify(userRepository, Mockito.never()).delete(expectedUser);
        }
    }

}
