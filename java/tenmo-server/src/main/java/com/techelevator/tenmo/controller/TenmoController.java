package com.techelevator.tenmo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.AccountsDAO;
import com.techelevator.tenmo.dao.TransfersDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Accounts;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;

@RestController

public class TenmoController {
	
	private AccountsDAO accountsDAO;
	private TransfersDAO transfersDAO;
	private UserDAO userDAO;
	
	public TenmoController(AccountsDAO accountsDAO, TransfersDAO transfersDAO, UserDAO userDAO) {
		this.accountsDAO = accountsDAO;
		this.transfersDAO = transfersDAO;
		this.userDAO = userDAO;
	}
	
// Accounts	
	@RequestMapping(path = "/account/userId/{userId}", method = RequestMethod.GET)
	public Accounts getAccount(@PathVariable int userId) {
		return accountsDAO.getAccount(userId);
	}
	
	@RequestMapping(path = "/account/accountId/{accountId}", method = RequestMethod.GET)
	public int getUserIdFromAcountId(@PathVariable int accountId) {
		return accountsDAO.getUserIdFromAccountId(accountId);
	}
	
// Users
	@RequestMapping(path = "/user", method = RequestMethod.GET)
	public List<User> getUsers() {
		return userDAO.findAll();
	}
	
	@RequestMapping(path ="/user/username/{userId}", method = RequestMethod.GET)
	public String findUserNameByUserId(@PathVariable int userId) {
		return userDAO.findUserNameByUserId(userId);
	}
	
	@RequestMapping(path = "/user/{username}", method = RequestMethod.GET)
	public User getUser(@PathVariable String username) {
		return userDAO.findByUsername(username);
	}
	
	@RequestMapping(path = "/user", method = RequestMethod.POST)
	public boolean create(@RequestBody String username, String password) {
		return userDAO.create(username, password);
	}
	
// Transfers
	@RequestMapping(path = "/transfer/send", method = RequestMethod.POST)
	public boolean sendTransfer(@RequestBody Transfers transfer) {
		
		return transfersDAO.sendTransfer(transfer);
	}
	
	@RequestMapping(path = "/transfer/request", method = RequestMethod.POST)
	public Transfers transferRequest(@RequestBody Transfers transfer) {
		
		return transfersDAO.transferRequest(transfer);
	}
	
	@RequestMapping(path = "/transfer/update", method = RequestMethod.PUT)
	public boolean updateTransferStatus(@RequestBody Transfers transfer) {
		
		return transfersDAO.updateTransferStatus(transfer);
	}
	
	@RequestMapping(path = "/transfer/update/balance", method = RequestMethod.PUT)
	public boolean updateBalanceFromTransfer(@RequestBody Transfers transfer) {
		
		return transfersDAO.updateBalanceFromTransfer(transfer);
	}
	
	@RequestMapping(path = "/transfer/{userId}/history", method = RequestMethod.GET)
	public List<Transfers> history(@PathVariable int userId) {
		return transfersDAO.history(userId);
	}
	
	@RequestMapping(path = "/transfer/{transferId}/details", method = RequestMethod.GET)
	public Transfers transferDetails(@PathVariable int transferId) {
		return transfersDAO.transferDetails(transferId);
	}

	@RequestMapping(path = "/transfer/{accountId}/status", method = RequestMethod.GET)
	public List<Transfers> pending(@PathVariable int accountId) {
		return transfersDAO.pending(accountId);
	}
	
	@RequestMapping(path = "transfer/{transferStatusId}/statusname", method = RequestMethod.GET)
	public String getTransferStatusName(@PathVariable int transferStatusId) {
		return transfersDAO.getTransferStatusName(transferStatusId);
	}
	
	@RequestMapping(path = "transfer/{transferTypeId}/type", method = RequestMethod.GET)
	public String getTransferTypeName(@PathVariable int transferTypeId) {
		return transfersDAO.getTransferTypeName(transferTypeId);
	}

}
