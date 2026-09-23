package com.logic.project;

import com.logic.project.config.JwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class DashboardAuthenticationTest {
    @Autowired WebApplicationContext context;
    @Value("${jwt.secret}") String secret;

    @Test
    void missingInvalidAndExpiredJwtReturnJson401InsteadOfLoginHtml() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        String expired = new JwtConfig(secret, -1000).generateToken("user", "USER");
        for (String token : new String[]{"", "invalid-token", expired}) {
            var request = get("/api/dashboard").accept("application/json");
            if (!token.isEmpty()) request.header("Authorization", "Bearer " + token);
            mvc.perform(request).andExpect(status().isUnauthorized())
                    .andExpect(content().contentTypeCompatibleWith("application/json"))
                    .andExpect(header().doesNotExist("Location"))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").isNotEmpty());
        }
    }

    @Test
    void nonApiBrowserRequestsStillUseFormLoginRedirect() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        mvc.perform(get("/protected-page").accept("text/html"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
    }
}
