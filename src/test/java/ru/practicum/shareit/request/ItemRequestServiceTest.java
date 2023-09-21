package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {
    private final LocalDateTime dateTime = LocalDateTime.of(2023, 11, 18, 12, 0, 0, 0);

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestService itemRequestService =
            new ItemRequestService(itemRequestRepository, userRepository, itemRequestMapper, userService, itemService);

    @Test
    void save_WrongUserId() {
        ReflectionTestUtils.setField(itemRequestService, "userRepository", userRepository);
        Long userId = 2L;
        ItemRequestDto itemRequestDto = createItemRequestDto();
        when(userRepository.existsById(anyLong())).thenReturn(false);
        NotFoundException notFoundException
                = assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(userId, itemRequestDto));
        assertThat(notFoundException.getMessage(), equalTo("Пользователь отсутствует в системе"));
    }

    @Test
    void save_StandardBehavior() {
        ReflectionTestUtils.setField(itemRequestService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemRequestService, "itemRequestMapper", itemRequestMapper);
        ReflectionTestUtils.setField(itemRequestService, "itemRequestRepository", itemRequestRepository);
        ReflectionTestUtils.setField(itemRequestService, "userService", userService);

        User user = createUser();
        UserDto userDto = createUserDto();
        ItemRequest request = createItemRequest();
        ItemRequestDto requestDto = createItemRequestDto();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestMapper.itemRequestDtoToRequest(any())).thenReturn(request);
        when(itemRequestMapper.itemRequestToRequestDto(any())).thenReturn(requestDto);
        when(itemRequestRepository.save(any())).thenReturn(request);

        ItemRequestDto expectedRequestDto = itemRequestService.createRequest(user.getId(), requestDto);

        assertThat(expectedRequestDto.getId(), equalTo(requestDto.getId()));
        assertThat(expectedRequestDto.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(expectedRequestDto.getCreated(), equalTo(requestDto.getCreated()));
        assertThat(expectedRequestDto.getRequester().getId(), equalTo(userDto.getId()));
        assertThat(expectedRequestDto.getRequester().getName(), equalTo(userDto.getName()));
        assertThat(expectedRequestDto.getRequester().getEmail(), equalTo(userDto.getEmail()));
    }

    private ItemRequest createItemRequest() {
        return ItemRequest.builder()
                .id(1L)
                .description("Нужен шуруповерт")
                .requester(createUser())
                .created(dateTime.minusDays(3))
                .build();
    }

    private ItemRequestDto createItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .description("Нужен шуруповерт")
                .requester(createUserDto())
                .created(dateTime.minusDays(3))
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
                .build();
    }
}
