package ifmo.unit.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ifmo.controller.ProfileController;
import ifmo.dto.ProfileEntityDto;
import ifmo.repository.UserRepository;
import ifmo.security.JwtService;
import ifmo.service.ProfileService;
import ifmo.service.UserService;
import ifmo.utils.JsonDeserializer;

import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ProfileService profileService;
    @MockBean
    private JwtService jwtService;

    @DisplayName("Get profile by id")
    @Test
    @WithMockUser
    public void getProfileByIdTest() throws Exception {
        ProfileEntityDto profileDTO = new ProfileEntityDto(1L, "Dasxunya", "Dasxunya", Date.valueOf("2021-05-01").toLocalDate(), "+79809345671", "my.mail@mail.com", "icon.jpg");

        Mockito.when(profileService.showUserProfile(1L)).thenReturn(profileDTO);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/profile/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String expectedJson = JsonDeserializer.objectToJson(profileDTO);
        String actualJson = mvcResult.getResponse().getContentAsString();

        Assertions.assertEquals(expectedJson, actualJson);
    }

}