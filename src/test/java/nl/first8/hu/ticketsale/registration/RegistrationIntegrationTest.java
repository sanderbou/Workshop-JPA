package nl.first8.hu.ticketsale.registration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;"
    })
    @Test
    public void testInsert() throws Exception {
        final AccountInfo expectedInfo = new AccountInfo("Kerkenbos 1059B", "024 – 348 35 70");
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

        assertThat(actualAccount.getId(), is(expectedAccount.getId()));
        assertThat(actualAccount.getEmailAddress(), is(actualAccount.getEmailAddress()));
        assertThat(actualInfo.getStreet(), is(expectedInfo.getStreet()));
        assertThat(actualInfo.getTelephoneNumber(), is(expectedInfo.getTelephoneNumber()));
    }

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)"
    })
    @Test
    public void testInsertDuplicateEmailAddress() throws Exception {
        final AccountInfo info = new AccountInfo("Kerkenbos 1059B", "024 – 348 35 70");

        mvc.perform(
                post("/registration/{emailAddress}", "f.dejong@first8.nl")
                        .content(accountInfoAsJSON(info))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isConflict());
    }

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)"})
    @Test
    public void testUpdateEmailAddress() throws Exception {
        final Account expectedAccount = new Account(1L, "t.poll@first8.nl", null);

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

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)"
    })
    @Test
    public void testUpdateEmailAddressNotExistent() throws Exception {
        final Account expectedAccount = new Account(2L, "t.poll@first8.nl", null);

        mvc.perform(
                put("/registration/{id}/{emailAddress}", expectedAccount.getId(), expectedAccount.getEmailAddress())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)"
    })
    @Test
    public void testUpdateEmailAddressNoChange() throws Exception {
        final Account expectedAccount = new Account(1L, "f.dejong@first8.nl", null);

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

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)"
    })
    @Test
    public void testUpdateAccountInfo() throws Exception {
        final AccountInfo expectedInfo = new AccountInfo(1L, "Edisonbaan 15", "024 – 348 35 71");
        final Account expectedAccount = new Account(1L, "f.dejong@first8.nl", expectedInfo);

        final MvcResult result = mvc.perform(
                put("/registration/{id}", expectedAccount.getId())
                        .content(accountInfoAsJSON(expectedInfo))
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

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Edisonbaan 15', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)"
    })
    @Test
    public void testUpdateAccountInfoNoChange() throws Exception {
        final AccountInfo expectedInfo = new AccountInfo(1L, "Edisonbaan 15", "024 – 348 35 70");
        final Account expectedAccount = new Account(1L, "f.dejong@first8.nl", expectedInfo);

        final MvcResult result = mvc.perform(
                put("/registration/{id}", expectedAccount.getId())
                        .content(accountInfoAsJSON(expectedInfo))
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

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)"
    })
    @Test
    public void testUpdateInfoNotExistent() throws Exception {
        final AccountInfo expectedInfo = new AccountInfo(1L, "Edisonbaan 15", "024 – 348 35 70");

        mvc.perform(
                put("/registration/{id}", 2L)
                        .content(accountInfoAsJSON(expectedInfo))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isConflict());
    }

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)",
        "INSERT INTO account_info (street,telephone_number) VALUES('Edisonbaan 15', '024 – 348 35 71');",
        "INSERT INTO account(email_address, info_id)  VALUES('t.poll@first8.nl', 2)"
    })
    @Test
    public void testGetByEmailAddress() throws Exception {
        final AccountInfo expectedInfo = new AccountInfo(2L, "Edisonbaan 15", "024 – 348 35 71");
        final Account expectedAccount = new Account(2L, "t.poll@first8.nl", expectedInfo);

        final MvcResult result = mvc.perform(
                get("/registration/email_address/{emailAddress}", expectedAccount.getEmailAddress())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        assertThat(account(result), is(expectedAccount));
    }

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)"
    })
    @Test
    public void testGetByEmailAddressNotAvailable() throws Exception {
        mvc.perform(
                get("/registration/email_address/{emailAddress}", "r.boss@first8.nl")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)",
        "INSERT INTO account_info (street,telephone_number) VALUES('Edisonbaan 15', '024 – 348 35 71');",
        "INSERT INTO account(email_address, info_id)  VALUES('t.poll@first8.nl', 2)"
    })
    @Test
    public void testGetById() throws Exception {
        final AccountInfo expectedInfo = new AccountInfo(2L, "Edisonbaan 15", "024 – 348 35 71");
        final Account expectedAccount = new Account(2L, "t.poll@first8.nl", expectedInfo);

        final MvcResult result = mvc.perform(
                get("/registration/id/{id}", expectedAccount.getId())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        assertThat(account(result), is(expectedAccount));
    }

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)",
        "INSERT INTO account_info (street,telephone_number) VALUES('Edisonbaan 15', '024 – 348 35 71');",
        "INSERT INTO account(email_address, info_id)  VALUES('t.poll@first8.nl', 2)"
    })
    @Test
    public void testGetByIdNotAvailable() throws Exception {
        mvc.perform(
                get("/registration/email_address/{emailAddress}", "r.boss@first8.nl")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;",
        "INSERT INTO account_info (street,telephone_number) VALUES('Kerkenbos 1059B', '024 – 348 35 70');",
        "INSERT INTO account(email_address, info_id)  VALUES('f.dejong@first8.nl', 1)",
        "INSERT INTO account_info (street,telephone_number) VALUES('Edisonbaan 15', '024 – 348 35 71');",
        "INSERT INTO account(email_address, info_id)  VALUES('t.poll@first8.nl', 2)"
    })
    @Test
    public void testGetAll() throws Exception {

        List<Account> expectedAccounts = Arrays.asList(
                new Account(2L, "t.poll@first8.nl", new AccountInfo(2L, "Edisonbaan 15", "024 – 348 35 71")),
                new Account(1L, "f.dejong@first8.nl", new AccountInfo(1L, "Kerkenbos 1059B", "024 – 348 35 70")));

        MvcResult result = mvc.perform(
                get("/registration/")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        assertThat(accounts(result), containsInAnyOrder(expectedAccounts.toArray()));
    }

    @Sql(statements = {
        "TRUNCATE TABLE account;",
        "DELETE FROM account_info;",
        "ALTER TABLE account_info AUTO_INCREMENT = 1;"
    })
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
