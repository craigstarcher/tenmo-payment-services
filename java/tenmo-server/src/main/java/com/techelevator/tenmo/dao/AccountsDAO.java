package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Accounts;

public interface AccountsDAO {
	
	Accounts getAccount(int userId);

	int getUserIdFromAccountId(int accountId);
}
