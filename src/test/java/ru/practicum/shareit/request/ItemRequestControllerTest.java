package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithOfferDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private final LocalDateTime dateTime = LocalDateTime.of(2023, 11, 18, 12, 0, 0, 0);

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequest_StandardBehavior() {
        ReflectionTestUtils.setField(itemRequestController, "itemRequestService", itemRequestService);

        ItemRequestDto requestDto = createRequestDto();
        ItemRequestWithOfferDto requestWithProposalsDto = createRequestWithOffersDto();
        when(itemRequestService.getTargetRequest(anyLong(), anyLong())).thenReturn(requestWithProposalsDto);

        ItemRequestWithOfferDto expected = itemRequestController.getTargetRequest(1L, 1L);
        assertThat((expected.getId()), equalTo(requestDto.getId()));
        assertThat((expected.getRequester()), equalTo(requestWithProposalsDto.getRequester()));
        assertThat(expected.getDescription(), equalTo(requestWithProposalsDto.getDescription()));
        assertThat((expected.getCreated()), equalTo(requestWithProposalsDto.getCreated()));
        assertThat((expected.getItems().get(0)), equalTo(requestWithProposalsDto.getItems().get(0)));
    }

    @Test
    void getRequests_ShouldReturnList() throws Exception {
        ReflectionTestUtils.setField(itemRequestController, "itemRequestService", itemRequestService);
        ItemRequestWithOfferDto requestWithProposalsDto = createRequestWithOffersDto();
        List<ItemRequestWithOfferDto> requests = List.of(requestWithProposalsDto);

        when(itemRequestService.getRequests(anyLong())).thenReturn(requests);
        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].name",
                        equalTo(requestWithProposalsDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].id").value(equalTo(requestWithProposalsDto.getId()), Long.class));
    }

    @Test
    void getRequestsAll_ShouldReturnList() throws Exception {
        ReflectionTestUtils.setField(itemRequestController, "itemRequestService", itemRequestService);
        ItemRequestWithOfferDto requestWithProposalsDto = createRequestWithOffersDto();
        List<ItemRequestWithOfferDto> requests = List.of(requestWithProposalsDto);

        when(itemRequestService.getPageableRequests(anyLong(), any())).thenReturn(requests);
        mockMvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .param("from", "3")
                        .param("size", "5")
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].name",
                        equalTo(requestWithProposalsDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].id").value(equalTo(requestWithProposalsDto.getId()), Long.class));
    }

    @Test
    void create_StandardBehavior() throws Exception {
        ItemRequestDto requestDto = createRequestDto();
        when(itemRequestService.createRequest(anyLong(), any())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", equalTo(requestDto.getDescription())))
                .andExpect(jsonPath("$.id").value(equalTo(requestDto.getId()), Long.class));
    }

    private ItemRequestDto createRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .created(dateTime.minusDays(3))
                .requester(createUserDto())
                .description("нужна кофемашина")
                .build();
    }

    private ItemRequestWithOfferDto createRequestWithOffersDto() {
        ItemRequestDto requestDto = createRequestDto();
        List<ItemDto> requestsDto = List.of(createItemDto());
        return new ItemRequestWithOfferDto(requestDto.getId(),
                requestDto.getDescription(),
                requestDto.getRequester(),
                requestDto.getCreated(),
                requestsDto);
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
                .build();
    }

    private UserDto createOwnerDto() {
        return UserDto.builder()
                .id(2L)
                .name("Mariya")
                .email("Mariya@nmicrk.ru")
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("ноутбук")
                .description("ультра тонкий")
                .available(true)
                .owner(createOwnerDto())
                .requestId(100L)
                .build();
    }
}
