package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Accounts;
import com.techelevator.tenmo.models.Transfers;
import com.techelevator.tenmo.models.User;

public class TenmoService {
	
	private String BASE_URL;
	private RestTemplate restTemplate;
	private String AUTH_TOKEN = "";
	
	public TenmoService(String URL) {
		this.BASE_URL = URL;
		restTemplate = new RestTemplate();
	}

//Accounts
	public Accounts getAccount (int userId) throws TenmoServiceException {
		Accounts account = null;
		try {
			account = restTemplate.exchange(BASE_URL + "account/userId/" + userId, HttpMethod.GET, makeAuthEntity(), Accounts.class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		} return account;
	}
	
	public int getUserIdFromAcountId( int accountId) throws TenmoServiceException {
		int userId = 0;
		try {
			userId = restTemplate.exchange(BASE_URL + "account/accountId/" + accountId, HttpMethod.GET, makeAuthEntity(), Integer.class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		} return userId;
	}
	
//Users
	public User[] getUsers() throws TenmoServiceException {
		User[] users = null;
		try {
			users = restTemplate.exchange(BASE_URL + "user", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		} return users;
	}
	
	public String findUserNameByUserId(int userId) throws TenmoServiceException {
		String user = "";
		try {
			user = restTemplate.exchange(BASE_URL + "user/username/" + userId, HttpMethod.GET, makeAuthEntity(), String.class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		} return user;
	}
	
	public User getUser(String username) throws TenmoServiceException {
		User user = null;
		try {
			user = restTemplate.exchange(BASE_URL + "user/" + username, HttpMethod.GET, makeAuthEntity(), User.class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		} return user;
	}
	
	public boolean create(String username, String password) throws TenmoServiceException {
		try {
			restTemplate.exchange(BASE_URL + "user", HttpMethod.POST, makeAuthEntity(), Boolean.class);
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		} return true;
	}
	
//Transfers
	public boolean sendTransfer(Transfers transfer) throws TenmoServiceException {
		
		try {
			restTemplate.exchange(BASE_URL + "transfer/send", HttpMethod.POST, makeTransferEntity(transfer), Boolean.class);
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return true;
	}
	
	public Transfers requestTransfer(Transfers transfer) throws TenmoServiceException {
		Transfers thisTransfer = null;
		try {
			thisTransfer = restTemplate.exchange(BASE_URL + "transfer/request", HttpMethod.POST, makeTransferEntity(transfer), Transfers.class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return thisTransfer;
	}
	
	public boolean updateTransferStatus(Transfers transfer) throws TenmoServiceException {
		
		try {
			restTemplate.exchange(BASE_URL + "transfer/update", HttpMethod.PUT, makeTransferEntity(transfer), Boolean.class);
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return true;
	}
	
	public boolean updateBalanceFromTransfer(Transfers transfer) throws TenmoServiceException {
		try {
			restTemplate.exchange(BASE_URL + "transfer/update/balance", HttpMethod.PUT, makeTransferEntity(transfer), Boolean.class);
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return true;
	}
	
	public Transfers[] history (int userId) throws TenmoServiceException {
		Transfers[] transferHistory = null;
		try { 
			transferHistory = restTemplate.exchange(BASE_URL + "transfer/" + userId + "/history" , HttpMethod.GET, makeAuthEntity(), Transfers[].class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			
		} return transferHistory;
	}
	
	public Transfers transferDetails(int transferId) throws TenmoServiceException {
		Transfers transfer = null;
		try {
			transfer = restTemplate.exchange(BASE_URL + "transfer/" + transferId + "/details", HttpMethod.GET, makeAuthEntity(), Transfers.class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			
		} return transfer;
	}
	
	public Transfers[] pending (int accountId) throws TenmoServiceException {
		Transfers[] transfersPending = null;
		try { 
			transfersPending = restTemplate.exchange(BASE_URL + "transfer/" + accountId + "/status" , HttpMethod.GET, makeAuthEntity(), Transfers[].class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			
		} return transfersPending;
	}
	
	public String getTransferStatusName(int transferStatusId) throws TenmoServiceException {
		String status = "";
		try {
			status = restTemplate.exchange(BASE_URL + "transfer/" + transferStatusId + "/statusname", HttpMethod.GET, makeAuthEntity(), String.class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			
		} return status;
	}
	
	public String getTransferTypeName(int transferTypeId) throws TenmoServiceException {
		String transferType = "";
		try {
			transferType = restTemplate.exchange(BASE_URL + "transfer/" + transferTypeId + "/type" , HttpMethod.GET, makeAuthEntity(), String.class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			
		} return transferType;
	}

// Helpers	
	private HttpEntity makeAuthEntity() {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(AUTH_TOKEN);
	    HttpEntity entity = new HttpEntity<>(headers);
	    return entity;
	  }
	
	private HttpEntity<Transfers> makeTransferEntity(Transfers transfer) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setBearerAuth(AUTH_TOKEN);
	    HttpEntity<Transfers> entity = new HttpEntity<>(transfer, headers);
	    return entity;
	  }
}
