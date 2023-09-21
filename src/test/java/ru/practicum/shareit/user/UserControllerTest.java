package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUser_StandardBehavior() {
        ReflectionTestUtils.setField(userController, "userService", userService);

        UserDto userDto = createUserDto();
        when(userService.getUserDto(any())).thenReturn(userDto);

        UserDto response = userController.getTargetUser(1L);
        assertThat((response.getId()), equalTo(userDto.getId()));
        assertThat((response.getName()), equalTo(userDto.getName()));
        assertThat((response.getEmail()), equalTo(userDto.getEmail()));
    }

    @Test
    void getUsers_ShouldReturnList() throws Exception {

        UserDto userDto = createUserDto();
        List<UserDto> expectedItems = List.of(userDto);
        when(userService.getAllUsers()).thenReturn(expectedItems);
        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "1L")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", equalTo(userDto.getName())))
                .andExpect(jsonPath("$[0].email", equalTo(userDto.getEmail())))
                .andExpect(jsonPath("$[0].id").value(equalTo(userDto.getId()), Long.class));
    }

    @Test
    void create_StandardBehavior() throws Exception {
        UserDto userDto = createUserDto();
        when(userService.createUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1L"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(userDto.getName())))
                .andExpect(jsonPath("$.email", equalTo(userDto.getEmail())))
                .andExpect(jsonPath("$.id").value(equalTo(userDto.getId()), Long.class));
    }

    @Test
    void delete_StandardBehavior() throws Exception {
        UserDto userDto = createUserDto();
        when(userService.deleteUser(anyLong())).thenReturn(userDto);

        mockMvc.perform(delete("/users/{id}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1L"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(userDto.getName())))
                .andExpect(jsonPath("$.email", equalTo(userDto.getEmail())))
                .andExpect(jsonPath("$.id").value(equalTo(userDto.getId()), Long.class));
    }

    @Test
    void update_StandardBehavior() throws Exception {
        Map<String, Object> updates = Map.of("name", "Kirill", "email", "Kirill@nmicrk.ru");
        UserDto userDtoWithUpdates = createUserDto();
        userDtoWithUpdates.setName((String) updates.get("name"));
        userDtoWithUpdates.setEmail((String) updates.get("email"));
        when(userService.updateUser(anyLong(), any())).thenReturn(userDtoWithUpdates);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .content(mapper.writeValueAsString(updates))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1L"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(userDtoWithUpdates.getName())))
                .andExpect(jsonPath("$.email", equalTo(userDtoWithUpdates.getEmail())))
                .andExpect(jsonPath("$.id").value(equalTo(userDtoWithUpdates.getId()), Long.class));
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
                .build();
    }
}
