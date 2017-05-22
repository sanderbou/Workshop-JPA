package nl.first8.hu.ticketsale.registration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Rollback(false)
public class RegistrationIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RegistrationTestHelperService helperService;

    @Before
    public void cleanDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "ticket", "account", "account_info");
    }

    @Test
    public void testInsert() throws Exception {
        final AccountInfo expectedInfo = new AccountInfo("Kerkenbos 1059B", "024 – 348 35 70", "Nijmegen");
        final Account expectedAccount = new Account(1L, "f.dejong@first8.nl", expectedInfo);

        final MvcResult result = mvc.perform(
                post("/registration/{emailAddress}", expectedAccount.getEmailAddress())
                        .content(accountInfoAsJSON(expectedInfo))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();

        final Long createdID = objectMapper.readValue(result.getResponse().getContentAsString(), Long.class);

        final Account actualAccount = entityManager.find(Account.class, createdID);
        final AccountInfo actualInfo = actualAccount.getInfo();

        assertThat(actualAccount.getId(), is(notNullValue()));
        assertThat(actualAccount.getEmailAddress(), is(actualAccount.getEmailAddress()));
        assertThat(actualInfo.getStreet(), is(expectedInfo.getStreet()));
        assertThat(actualInfo.getTelephoneNumber(), is(expectedInfo.getTelephoneNumber()));
    }


    @Test
    public void testInsertDuplicateEmailAddress() throws Exception {
        helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Nijmegen");
        final AccountInfo info = new AccountInfo("Kerkenbos 1059B", "024 – 348 35 70", "Nijmegen");

        mvc.perform(
                post("/registration/{emailAddress}", "f.dejong@first8.nl")
                        .content(accountInfoAsJSON(info))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isConflict());
    }

    @Test
    public void testUpdateEmailAddress() throws Exception {

        final Account expectedAccount = helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");
        final String newEmailAddress = "t.poll@first8.nl";

        final MvcResult result = mvc.perform(
                put("/registration/{id}/{emailAddress}", expectedAccount.getId(), newEmailAddress)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();

        //Verify that the correct entity was returned from the REST resource
        final Account returnedAccount = account(result);
        assertThat(returnedAccount.getId(), is(expectedAccount.getId()));
        assertThat(returnedAccount.getEmailAddress(), is(newEmailAddress));

        //Verify that the entity in the underlying data source matches the expected entity.
        //Since we don't change the info, check that the one in the data source matches the returned value from the REST resource
        final Account actualAccountInDS = entityManager.find(Account.class, returnedAccount.getId());
        assertThat(actualAccountInDS.getId(), is(expectedAccount.getId()));
        assertThat(actualAccountInDS.getEmailAddress(), is(newEmailAddress));
        assertThat(returnedAccount.getInfo(), is(actualAccountInDS.getInfo()));
    }

    @Test
    public void testUpdateEmailAddressNotExistent() throws Exception {

        Account createdAccount = helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");

        final Account expectedAccount = new Account(2L, "t.poll@first8.nl", null);

        mvc.perform(
                put("/registration/{id}/{emailAddress}", createdAccount.getId() + 1, expectedAccount.getEmailAddress())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateEmailAddressNoChange() throws Exception {
        final Account expectedAccount = helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");

        final MvcResult result = mvc.perform(
                put("/registration/{id}/{emailAddress}", expectedAccount.getId(), expectedAccount.getEmailAddress())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();

        //Verify that the correct entity was returned from the REST resource
        final Account returnedAccount = account(result);
        assertThat(returnedAccount.getId(), is(expectedAccount.getId()));
        assertThat(returnedAccount.getEmailAddress(), is(expectedAccount.getEmailAddress()));

        //Verify that the entity in the underlying data source matches the expected entity.
        //Since we don't change the info, check that the one in the data source matches the returned value from the REST resource
        final Account actualAccountInDS = entityManager.find(Account.class, returnedAccount.getId());
        assertThat(actualAccountInDS.getId(), is(expectedAccount.getId()));
        assertThat(actualAccountInDS.getEmailAddress(), is(expectedAccount.getEmailAddress()));
        assertThat(returnedAccount.getInfo(), is(actualAccountInDS.getInfo()));
    }

    @Test
    public void testUpdateAccountInfo() throws Exception {

        final Account expectedAccount = helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");

        expectedAccount.getInfo().setStreet("Edisonbaan 15");
        expectedAccount.getInfo().setStreet("024 – 348 35 71");

        final MvcResult result = mvc.perform(
                put("/registration/{id}", expectedAccount.getId())
                        .content(accountInfoAsJSON(expectedAccount.getInfo()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();

        //Verify that the correct entity was returned from the REST resource
        final Account returnedAccount = account(result);
        assertThat(returnedAccount.getId(), is(expectedAccount.getId()));
        assertThat(returnedAccount.getEmailAddress(), is(expectedAccount.getEmailAddress()));

        //Verify that the entity in the underlying data source matches the expected entity.
        //Since we don't change the info, check that the one in the data source matches the returned value from the REST resource
        final Account actualAccountInDS = entityManager.find(Account.class, returnedAccount.getId());
        assertThat(actualAccountInDS.getId(), is(expectedAccount.getId()));
        assertThat(actualAccountInDS.getEmailAddress(), is(expectedAccount.getEmailAddress()));
        assertThat(returnedAccount.getInfo(), is(actualAccountInDS.getInfo()));
    }

    @Test
    public void testUpdateAccountInfoNoChange() throws Exception {

        final Account expectedAccount = helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");

        final MvcResult result = mvc.perform(
                put("/registration/{id}", expectedAccount.getId())
                        .content(accountInfoAsJSON(expectedAccount.getInfo()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();

        //Verify that the correct entity was returned from the REST resource
        final Account returnedAccount = account(result);
        assertThat(returnedAccount.getId(), is(expectedAccount.getId()));
        assertThat(returnedAccount.getEmailAddress(), is(expectedAccount.getEmailAddress()));

        //Verify that the entity in the underlying data source matches the expected entity.
        //Since we don't change the info, check that the one in the data source matches the returned value from the REST resource
        final Account actualAccountInDS = entityManager.find(Account.class, returnedAccount.getId());
        assertThat(actualAccountInDS.getId(), is(expectedAccount.getId()));
        assertThat(actualAccountInDS.getEmailAddress(), is(expectedAccount.getEmailAddress()));
        assertThat(returnedAccount.getInfo(), is(actualAccountInDS.getInfo()));
    }

    @Test
    public void testUpdateInfoNotExistent() throws Exception {

        final Account expectedAccount = helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");

        final AccountInfo updatedInfo = new AccountInfo("Edisonbaan 15", "024 – 348 35 70", "Nimka");

        mvc.perform(
                put("/registration/{id}", expectedAccount.getId() + 1)
                        .content(accountInfoAsJSON(updatedInfo))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isConflict());
    }

    @Test
    public void testGetByEmailAddress() throws Exception {

        final Account accountFDeJong = helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");
        final Account accountTPoll = helperService.createAccount("t.poll@first8.nl", "Edisonbaan 15", "024 – 348 35 71", "Utrecht");

        final MvcResult firstResult = mvc.perform(
                get("/registration/email_address/{emailAddress}", accountFDeJong.getEmailAddress())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        assertThat(account(firstResult), is(accountFDeJong));

        final MvcResult secondResult = mvc.perform(
                get("/registration/email_address/{emailAddress}", accountTPoll.getEmailAddress())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        assertThat(account(secondResult), is(accountTPoll));
    }

    @Test
    public void testGetByEmailAddressNotAvailable() throws Exception {
        helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");

        mvc.perform(
                get("/registration/email_address/{emailAddress}", "r.boss@first8.nl")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testGetById() throws Exception {

        final Account accountFDeJong = helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");
        final Account accountTPoll = helperService.createAccount("t.poll@first8.nl", "Edisonbaan 15", "024 – 348 35 71", "Utrecht");

        final MvcResult firstResult = mvc.perform(
                get("/registration/id/{id}", accountTPoll.getId())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        assertThat(account(firstResult), is(accountTPoll));

        final MvcResult secondResult = mvc.perform(
                get("/registration/id/{id}", accountFDeJong.getId())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        assertThat(account(secondResult), is(accountFDeJong));
    }

    @Test
    public void testGetByIdNotAvailable() throws Exception {

        Account accountFDeJong = helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");
        Account accountTPoll = helperService.createAccount("t.poll@first8.nl", "Edisonbaan 15", "024 – 348 35 71", "Utrecht");

        mvc.perform(
                get("/registration/id/{id}", Math.max(accountFDeJong.getId(), accountTPoll.getId()) + 1)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testGetAll() throws Exception {

        Account accountFDeJong = helperService.createAccount("f.dejong@first8.nl", "Kerkenbos 1059B", "024 – 348 35 70", "Utrecht");
        Account accountTPoll = helperService.createAccount("t.poll@first8.nl", "Edisonbaan 15", "024 – 348 35 71", "Utrecht");

        List<Account> expectedAccounts = Arrays.asList(
                accountFDeJong,
                accountTPoll);

        MvcResult result = mvc.perform(
                get("/registration/")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        assertThat(accounts(result), containsInAnyOrder(expectedAccounts.toArray()));
    }

    @Test
    public void testGetAllNoResultsAvailable() throws Exception {
        List<Account> expectedAccounts = Collections.emptyList();

        MvcResult result = mvc.perform(
                get("/registration/")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        assertThat(accounts(result), containsInAnyOrder(expectedAccounts.toArray()));
    }

    private Account account(final MvcResult result) throws UnsupportedEncodingException, IOException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), Account.class);
    }

    private List<Account> accounts(final MvcResult result) throws UnsupportedEncodingException, IOException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Account>>() {
        });
    }

    private String accountInfoAsJSON(final AccountInfo info) throws JsonProcessingException {
        return objectMapper.writeValueAsString(info);
    }

}
