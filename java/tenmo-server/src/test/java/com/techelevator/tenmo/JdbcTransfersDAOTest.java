package com.techelevator.tenmo;

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

import com.techelevator.tenmo.dao.JdbcTransfersDAO;
import com.techelevator.tenmo.dao.TransfersDAO;
import com.techelevator.tenmo.model.Transfers;

public class JdbcTransfersDAOTest {
	
	private static SingleConnectionDataSource dataSource;
	private static TransfersDAO dao;
	
	private static final BigDecimal bal = new BigDecimal(777);
	private static final Transfers transfer = new Transfers(777, 2, 2, 999, 888, bal);

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
		String sqlInsertTransfer = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
									"VALUES (777, 2, 2, 999, 888, 777)";
		
		String sqlInsertUserTo = "INSERT INTO users(user_id, username, password_hash) VALUES (888, 555, 555)";
		String sqlInsertAccountTo = "INSERT INTO accounts (account_id, user_id, balance) VALUES (888, 888, 888.00)";
		String sqlInsertUserFrom = "INSERT INTO users(user_id, username, password_hash) VALUES (999, 666, 666)";
		String sqlInsertAccountFrom = "INSERT INTO accounts (account_id, user_id, balance) VALUES (999, 999, 999.00)";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertUserTo);
		jdbcTemplate.update(sqlInsertAccountTo);
		jdbcTemplate.update(sqlInsertUserFrom);
		jdbcTemplate.update(sqlInsertAccountFrom);
		jdbcTemplate.update(sqlInsertTransfer);
		
		dao = new JdbcTransfersDAO(jdbcTemplate);
	}

	@AfterEach
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	
	@Test
	public void transferRequest() {
		Transfers results = new Transfers();
		
		results = dao.transferRequest(transfer);
		
		assertNotNull(results);
		assertEquals(transfer.getTransferId(), results.getTransferId());
		assertEquals(transfer.getTransferStatusId(), results.getTransferStatusId());
		assertEquals(transfer.getTransferTypeId(), results.getTransferTypeId());
		assertEquals(transfer.getAccountFrom(), results.getAccountFrom());
		assertEquals(transfer.getAccountTo(), results.getAccountTo());
		assertEquals(transfer.getAmount(), results.getAmount());
		
	}
	

	 private Transfers setAccount(int transferId, int transferTypeId, int transferStatusId, int accountFrom, int accountTo, BigDecimal amount) {
		 Transfers transfer = new Transfers();
		 transfer.setTransferId(transferId);
		 transfer.setTransferTypeId(transferTypeId);
		 transfer.setTransferStatusId(transferStatusId);
		 transfer.setAccountFrom(accountFrom);
		 transfer.setAccountTo(accountTo);
		 transfer.setAmount(amount);
		 return transfer;
	 }
	 
	 private void assertAccountsEqual(Transfers expected, Transfers actual) {
			assertEquals(expected.getTransferId(), actual.getTransferId());
			assertEquals(expected.getTransferTypeId(), actual.getTransferTypeId());
			assertEquals(expected.getTransferStatusId(), actual.getTransferStatusId());
			assertEquals(expected.getAccountFrom(), actual.getAccountFrom());
			assertEquals(expected.getAccountTo(), actual.getAccountTo());
	 }

}
