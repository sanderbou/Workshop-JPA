package nl.first8.hu.ticketsale.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.first8.hu.ticketsale.registration.Account;
import nl.first8.hu.ticketsale.util.TestRepository;
import nl.first8.hu.ticketsale.venue.Concert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Rollback(false)
public class ReportingIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRepository helper;

    @Test
    public void testReport() throws Exception {

        Concert concertMetal1 = helper.createConcert("Five Finger Death Punch", "metal", "Utrecht");
        Concert concertMetal2 = helper.createConcert("Disturbed", "metal", "Apeldoorn");
        Concert concertElec= helper.createConcert("Pogo", "electronica", "Amsterdam");
        Account accountZeist = helper.createAccount("user@zeist.museum", "Zeist");
        Account accountNieuwegein = helper.createAccount("user@nieuwegein.museum", "Nieuwegein");
        Account accountHouten = helper.createAccount("user@houten.museum", "Houten");
        helper.createTicket(concertMetal1, accountZeist);
        helper.createTicket(concertMetal1, accountNieuwegein);
        helper.createTicket(concertElec, accountNieuwegein);
        helper.createTicket(concertElec, accountHouten);

        String requestedGenre = "metal";
        final MvcResult result = mvc.perform(
                get("/report/location").param("genre", requestedGenre)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();


        final LocationReport receivedReport= objectMapper.readValue(result.getResponse().getContentAsString(), LocationReport.class);

        assertThat(receivedReport.getGenre(), is(requestedGenre));

        assertThat(receivedReport.getAccountLocations(), contains(concertMetal1.getLocation().getName()));
        assertThat(receivedReport.getAccountLocations(), contains(concertMetal2.getLocation().getName()));
        assertThat(receivedReport.getAccountLocations(), not(contains(concertElec.getLocation().getName())));
        assertThat(receivedReport.getAccountLocations(), not(contains(accountZeist.getInfo().getCity())));
        assertThat(receivedReport.getAccountLocations(), not(contains(accountNieuwegein.getInfo().getCity())));
        assertThat(receivedReport.getAccountLocations(), not(contains(accountHouten.getInfo().getCity())));

        assertThat(receivedReport.getConcertLocations(), contains(accountZeist.getInfo().getCity()));
        assertThat(receivedReport.getConcertLocations(), contains(accountNieuwegein.getInfo().getCity()));
        assertThat(receivedReport.getConcertLocations(), not(contains(accountHouten.getInfo().getCity())));
        assertThat(receivedReport.getConcertLocations(), not(contains(concertMetal1.getLocation().getName())));
        assertThat(receivedReport.getConcertLocations(), not(contains(concertMetal2.getLocation().getName())));
        assertThat(receivedReport.getConcertLocations(), not(contains(concertElec.getLocation().getName())));

    }
}
