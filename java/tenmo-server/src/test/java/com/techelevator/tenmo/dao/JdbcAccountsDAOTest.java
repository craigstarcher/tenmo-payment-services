package com.techelevator.tenmo.dao;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.tenmo.model.Accounts;

public class JdbcAccountsDAOTest {

	private static SingleConnectionDataSource dataSource;
	private static AccountsDAO dao;

	DecimalFormat f = new DecimalFormat("##0.00");
	private static final BigDecimal bal = new BigDecimal(888.00);
	private static final Accounts testAccount = new Accounts(888, 888, bal);

	

	@BeforeAll
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		dataSource.setAutoCommit(false);
	}

	@AfterAll
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}

	@BeforeEach
	public void setup() {
		String sqlInsertUser = "INSERT INTO users(user_id, username, password_hash) VALUES (888, 555, 555)";
		String sqlInsertAccount = "INSERT INTO accounts (account_id, user_id, balance) VALUES (888, 888, 888.00)";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertUser);
		jdbcTemplate.update(sqlInsertAccount);
		
		dao = new JdbcAccountsDAO(jdbcTemplate);
		
	}

	@AfterEach
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	
	@Test
	public void getAccount() {
		Accounts results = new Accounts();
		
		results = dao.getAccount(testAccount.getUserId());
		
		assertNotNull(results);
		assertEquals(f.format(testAccount.getBalance()), f.format(results.getBalance()));
		assertEquals(testAccount.getAccountId(), results.getAccountId());
		assertEquals(testAccount.getUserId(), results.getUserId());
	}
	
	@Test
	public void getUserIdFromAccountId() {
		int userId = 0;
		
		userId = dao.getUserIdFromAccountId(testAccount.getAccountId());
		
		assertNotNull(userId);
		assertEquals(testAccount.getUserId(), userId);
	}

//	 private Accounts setAccount(int accountId, int userId, BigDecimal balance) {
//		 Accounts account = new Accounts();
//		 account.setAccountId(accountId);
//		 account.setUserId(userId);
//		 account.setBalance(balance);
//		 return account;
//	 }
//	 
//	 private void assertAccountsEqual(Accounts expected, Accounts actual) {
//			assertEquals(expected.getAccountId(), actual.getAccountId());
//			assertEquals(expected.getUserId(), actual.getUserId());
//			assertEquals(expected.getBalance(), actual.getBalance());
//	 }
}
