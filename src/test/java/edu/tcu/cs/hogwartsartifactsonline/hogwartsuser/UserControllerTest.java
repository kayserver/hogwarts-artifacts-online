package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tcu.cs.hogwartsartifactsonline.system.StatusCode;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.dto.UserDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @Value("${api.endpoint.base-url:http://localhost:8080/api/v1}")
    private String baseUrl;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private List<HogwartsUser> users;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        this.users = new ArrayList<>();

        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("john");
        u1.setPassword("123456");
        u1.setEnabled(true);
        u1.setRoles("admin user");
        this.users.add(u1);

        HogwartsUser u2 = new HogwartsUser();
        u2.setId(2);
        u2.setUsername("eric");
        u2.setPassword("654321");
        u2.setEnabled(true);
        u2.setRoles("user");
        this.users.add(u2);

        HogwartsUser u3 = new HogwartsUser();
        u3.setId(3);
        u3.setUsername("tom");
        u3.setPassword("qwerty");
        u3.setEnabled(false);
        u3.setRoles("user");
        this.users.add(u3);
    }

    @Test
    void testFindAllUsersSuccess() throws Exception {
        // Given
        given(this.userService.findAll()).willReturn(this.users);

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.users.size())))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].username").value("john"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].username").value("eric"))
                .andExpect(jsonPath("$.data[2].id").value(3))
                .andExpect(jsonPath("$.data[2].username").value("tom"));
    }

    @Test
    void testFindUserByIdSuccess() throws Exception {
        // Given
        given(this.userService.findById(2)).willReturn(this.users.get(1));

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/users/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.username").value("eric"));
    }

    @Test
    void testFindUserByIdNotFound() throws Exception {
        // Given
        given(this.userService.findById(5)).willThrow(new ObjectNotFoundException("user", 5));

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/users/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAddUserSuccess() throws Exception {
        // Given
        UserDto userDto = new UserDto(null, "lily", true, "user");
        String json = this.objectMapper.writeValueAsString(userDto);

        HogwartsUser savedUser = new HogwartsUser();
        savedUser.setId(4);
        savedUser.setUsername("lily");
        savedUser.setPassword("123456");
        savedUser.setEnabled(true);
        savedUser.setRoles("user");

        given(this.userService.save(org.mockito.Mockito.any(HogwartsUser.class))).willReturn(savedUser);

        // When and then
        this.mockMvc.perform(post(this.baseUrl + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("lily"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.roles").value("user"));
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        // Given
        UserDto userDto = new UserDto(3, "tom123", false, "user");
        String json = this.objectMapper.writeValueAsString(userDto);

        HogwartsUser updatedUser = new HogwartsUser();
        updatedUser.setId(3);
        updatedUser.setUsername("tom123");
        updatedUser.setEnabled(false);
        updatedUser.setRoles("user");

        given(this.userService.update(eq(3), org.mockito.Mockito.any(HogwartsUser.class))).willReturn(updatedUser);

        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(3))
                .andExpect(jsonPath("$.data.username").value("tom123"))
                .andExpect(jsonPath("$.data.enabled").value(false))
                .andExpect(jsonPath("$.data.roles").value("user"));
    }

    @Test
    void testUpdateUserErrorWithNonExistentId() throws Exception {
        // Given
        UserDto userDto = new UserDto(5, "tom123", false, "user");
        String json = this.objectMapper.writeValueAsString(userDto);

        given(this.userService.update(eq(5), org.mockito.Mockito.any(HogwartsUser.class)))
                .willThrow(new ObjectNotFoundException("user", 5));

        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        // Given
        doNothing().when(this.userService).delete(2);

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/users/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteUserErrorWithNonExistentId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("user", 5)).when(this.userService).delete(5);

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/users/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}