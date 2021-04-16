package apicross.demo.myspace;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MySpaceMicroserviceTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void request_to_not_existing_resource() throws Exception {
        mockMvc.perform(get("/unknown")
                .with(httpBasic("user1", "user1Pass")))
                .andExpect(status().is(404));
    }

    @Test
    public void request_without_authentication() throws Exception {
        mockMvc.perform(post("/my/competitions")
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":\"" + loremIpsum() + "\"," +
                        "\"participantReqs\":{\"minAge\":10}" +
                        "}"))
                .andExpect(status().is(401));
    }

    @Test
    public void creating_competition_invalid_request_body() throws Exception {
        RequestPostProcessor auth = httpBasic("user1", "user1Pass");

        mockMvc.perform(post("/my/competitions")
                .with(auth)
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" + // not all required properties supplied
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"" +
                        "}"))
                .andExpect(status().is(422))
                .andExpect(jsonPath("validationErrors.requestBody[0].code").value("MissingRequiredFields"))
                .andExpect(jsonPath("validationErrors.requestBody[0].fieldPath").value("$"));

        mockMvc.perform(post("/my/competitions")
                .with(auth)
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":\"" + loremIpsum() + "\"," +
                        "\"participantReqs\":{}" + // minProperties == 1
                        "}"))
                .andExpect(status().is(422))
                .andExpect(jsonPath("validationErrors.requestBody[0].code").value("TooFewFields"))
                .andExpect(jsonPath("validationErrors.requestBody[0].fieldPath").value("$.participantReqs"));
    }

    @Test
    public void creating_competition_success() throws Exception {
        mockMvc.perform(post("/my/competitions")
                .with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":\"" + loremIpsum() + "\"," +
                        "\"participantReqs\":{\"minAge\":10}" +
                        "}"))
                .andExpect(status().is(201));
    }

    @Test
    public void updating_competition() throws Exception {
        RequestPostProcessor auth = httpBasic("user1", "user1Pass");

        MvcResult registerCompetitionResult = mockMvc.perform(post("/my/competitions")
                .with(auth)
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":\"" + loremIpsum() + "\"," +
                        "\"participantReqs\":{\"minAge\":10}" +
                        "}"))
                .andReturn();

        String newCompetitionResourceURI = registerCompetitionResult.getResponse().getHeader("Location");
        assertNotNull(newCompetitionResourceURI);

        String eTag = registerCompetitionResult.getResponse().getHeader("ETag");
        assertNotNull(eTag);

        MvcResult patchCompetitionResult = mockMvc.perform(patch(newCompetitionResourceURI)
                .with(auth)
                .contentType("application/vnd.demoapp.v1+json")
                .header("If-Match", eTag)
                .content("{" +
                        "\"participantReqs\":{\"minAge\":5}" +
                        "}"))
                .andExpect(status().is(204))
                .andReturn();

        String eTagAfterPatch = patchCompetitionResult.getResponse().getHeader("ETag");
        assertNotNull(eTag);

        mockMvc.perform(patch(newCompetitionResourceURI)
                .with(auth)
                .contentType("application/vnd.demoapp.v1+json")
                .header("If-Match", eTag)
                .content("{" +
                        "\"participantReqs\":{\"minAge\":15}" +
                        "}"))
                .andExpect(status().is(412));

        mockMvc.perform(patch(newCompetitionResourceURI)
                .with(auth)
                .contentType("application/vnd.demoapp.v1+json")
                .header("If-Match", eTagAfterPatch)
                .content("{" +
                        "\"participantReqs\":{\"minAge\":12}" +
                        "}"))
                .andExpect(status().is(204));
    }

    @Test
    public void deleting_competition() throws Exception {
        RequestPostProcessor auth = httpBasic("user1", "user1Pass");

        MvcResult registerCompetitionResult = mockMvc.perform(post("/my/competitions")
                .with(auth)
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":\"" + loremIpsum() + "\"," +
                        "\"participantReqs\":{\"minAge\":10}" +
                        "}"))
                .andReturn();

        String newCompetitionResourceURI = registerCompetitionResult.getResponse().getHeader("Location");
        assertNotNull(newCompetitionResourceURI);

        mockMvc.perform(get(newCompetitionResourceURI)
                .with(auth)
                .accept("application/vnd.demoapp.v1+json"))
                .andExpect(status().is(200));

        mockMvc.perform(delete(newCompetitionResourceURI)
                .with(auth))
                .andExpect(status().is(204));
    }

    @Test
    public void registering_work() throws Exception {
        RequestPostProcessor auth = httpBasic("user1", "user1Pass");

        MvcResult registerCompetitionResult = mockMvc.perform(post("/my/competitions")
                .with(auth)
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":\"" + loremIpsum() + "\"," +
                        "\"participantReqs\":{\"minAge\":10}" +
                        "}"))
                .andReturn();

        String newCompetitionResourceURI = registerCompetitionResult.getResponse().getHeader("Location");
        assertNotNull(newCompetitionResourceURI);

        MvcResult mvcResult = mockMvc.perform(get(newCompetitionResourceURI)
                .with(auth)
                .accept("application/vnd.demoapp.v1+json"))
                .andExpect(status().is(200))
                .andReturn();

        String competitionId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(put("/my/competitions/{competitionId}/open", competitionId)
                .with(auth)
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"acceptWorksTillDate\": \"" + LocalDate.now().plus(10, ChronoUnit.DAYS) + "\"," +
                        "\"acceptVotesTillDate\":\"" + LocalDate.now().plus(20, ChronoUnit.DAYS) + "\"" +
                        "}"))
                .andExpect(status().is(204));

        mockMvc.perform(post("/my/competitions/{competitionId}/works", competitionId)
                .with(auth)
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\": \"Bla-Bla song\"," +
                        "\"description\":\"" + loremIpsum() + "\"," +
                        "\"author\": \"Victor\"," +
                        "\"author_age\": 12" +
                        "}"))
                .andExpect(status().is(201));
    }

    private String loremIpsum() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip " +
                "ex ea commodo consequat. " +
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore " +
                "eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
                "sunt in culpa qui officia deserunt mollit anim id est laborum.";
    }
}
