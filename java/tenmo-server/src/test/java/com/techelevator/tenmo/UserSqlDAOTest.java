package com.techelevator.tenmo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.dao.UserSqlDAO;
import com.techelevator.tenmo.model.User;

public class UserSqlDAOTest {
	
	private static SingleConnectionDataSource dataSource;
	private static UserDAO dao;
	
	private static final Long testId = 555L;
	private static final User user = new User (testId, "555", "555555555555", "ROLE_user");

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
		String sqlInsertUser = "INSERT INTO users (user_id, username, password_hash) VALUES (555, 555, 555555555555)";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertUser);
		
		dao = new UserSqlDAO(jdbcTemplate);
	}

	@AfterEach
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	
	@Test
	public void findListOfUsers() {
		List<User> users = new ArrayList<>();
		
		users = dao.findAll();
		
		
		assertNotNull(users);
		User testUser = users.get(users.size() - 1);
		assertUsersEqual(user, testUser);
	}

//	 private User setUser(int id, String username, String password, boolean activated) {
//		 User user = new User();
//		 user.setId(id);
//		 user.setUsername(username);
//		 user.setPassword(password);
//		 user.setActivated(activated);
//		 return user;
//	 }
	 
	 private void assertUsersEqual(User expected, User actual) {
		 	assertEquals(expected.getId(), actual.getId());
			assertEquals(expected.getUsername(), actual.getUsername());
			assertEquals(expected.getPassword(), actual.getPassword());
			assertEquals(expected.isActivated(), actual.isActivated());
	 }

}
