package nl.first8.hu.ticketsale.sales;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.first8.hu.ticketsale.registration.Account;
import nl.first8.hu.ticketsale.util.TestRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Rollback(false)
public class SalesIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Sql(statements = {
            "DELETE FROM `ticket`;",
            "DELETE FROM `account`;"})
    @Test
    public void testInsertTicket() throws Exception {

        Account account = testRepository.createDefaultAccount("f.dejong@first8.nl");

        final Ticket newTicket = new Ticket("Chinese Man", "Trip Hop", "Tovilo");

        String ticketJson = objectMapper.writeValueAsString(newTicket);

        final MvcResult result = mvc.perform(
                post("/sales/ticket").param("account_id", account.getId().toString())
                        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_UTF8).content(ticketJson)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();

        final Long createdID = readLongResponse(result);

        final Ticket actualTicket = testRepository.find(createdID);
        assertThat(actualTicket.getId(), is(createdID));
        assertThat(actualTicket.getArtist(), is(newTicket.getArtist()));
    }

    @Sql(statements = {
            "DELETE FROM `ticket`;",
            "DELETE FROM `account`;"})
    @Test
    public void testGetTickets() throws Exception {

        Account account = testRepository.createDefaultAccount("f.dejong@first8.nl");
        Ticket ticketGorillaz = testRepository.createDefaultTicket(account, "Gorillaz");
        Ticket ticketThieveryCo = testRepository.createDefaultTicket(account, "Thievery Cooperation");


        MvcResult result = mvc.perform(
                get("/sales/ticket").param("account_id", account.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        List<TicketDto> actualTickets = readTicketsResponse(result);
        assertEquals(2, actualTickets.size());
        assertEquals(ticketGorillaz.getArtist(), actualTickets.get(0).getArtist());
        assertEquals(ticketThieveryCo.getArtist(), actualTickets.get(1).getArtist());


    }

    private Long readLongResponse(MvcResult result) throws java.io.IOException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), Long.class);
    }

    private List<TicketDto> readTicketsResponse(MvcResult result) throws IOException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TicketDto>>() {
        });
    }


}
