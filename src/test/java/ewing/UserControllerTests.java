package ewing;

import ewing.application.common.GsonUtils;
import ewing.entity.User;
import ewing.user.UserService;
import ewing.user.vo.UserWithRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户测试，测试事务自动回滚。
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StartApp.class)
public class UserControllerTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    private MockMvc mvc;

    public static final String USERNAME = "Ewing";

    private User createUser(boolean save) {
        UserWithRole user = new UserWithRole();
        user.setUsername("123");
        user.setNickname("123");
        user.setPassword("123");
        user.setGender("MALE");
        if (save) {
            userService.addUserWithRole(user);
        }
        return user;
    }

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithUserDetails(USERNAME)
    public void saveTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/user/addUserWithRole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(GsonUtils.toJson(createUser(false)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andReturn();
    }

    @Test
    @WithUserDetails(USERNAME)
    public void getTest() throws Exception {
        User user = createUser(true);
        mvc.perform(MockMvcRequestBuilders
                .get("/user/getUser")
                .param("userId", user.getUserId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username")
                        .value("123"));
    }

    @Test
    @WithUserDetails(USERNAME)
    public void deleteTest() throws Exception {
        User user = createUser(true);
        mvc.perform(MockMvcRequestBuilders
                .post("/user/deleteUser")
                .param("userId", user.getUserId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}