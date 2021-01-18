package com.techelevator.tenmo.dao;

import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Accounts;

@Component
public class JdbcAccountsDAO implements AccountsDAO {

	private JdbcTemplate jdbcTemplate;
	
	public JdbcAccountsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	
	@Override
	public Accounts getAccount(int userId) {
		Accounts account = new Accounts();
		String sql = "SELECT * FROM accounts WHERE user_id = ?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
		
		if (results.next()) {
			account = mapRowToAccounts(results);
		}
		return account;
	}
	
	@Override 
	public int getUserIdFromAccountId(int accountId) {
		int userId = 0;
		String sql = "SELECT * FROM accounts WHERE account_id = ?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
		
		if (results.next()) {
			userId = results.getInt("user_id");	
		}
		return userId;
	}
	
	private Accounts mapRowToAccounts(SqlRowSet rs) {
		Accounts accounts = new Accounts(); 
		
		accounts.setAccountId(rs.getInt("account_id"));
		accounts.setUserId(rs.getInt("user_id"));
		accounts.setBalance(rs.getBigDecimal("balance"));
		return accounts;
	}
}
