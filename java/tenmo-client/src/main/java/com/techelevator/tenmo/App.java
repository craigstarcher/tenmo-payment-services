package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import com.techelevator.tenmo.models.Accounts;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfers;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TenmoService;
import com.techelevator.tenmo.services.TenmoServiceException;
import com.techelevator.view.ConsoleService;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private TenmoService tenmoService;
    private String user;
    private Scanner userInput = new Scanner(System.in);
    

    public static void main(String[] args) throws TenmoServiceException {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new TenmoService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, TenmoService tenmoService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.tenmoService = tenmoService;
	}

	public void run() throws TenmoServiceException {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() throws TenmoServiceException {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
					viewTransferHistory(userInput);	
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() throws TenmoServiceException {

		Accounts accounts = tenmoService.getAccount(getUserId());
				System.out.println("Your current account balance is: $" + accounts.getBalance());
	}

	private void viewTransferHistory(Scanner userInput) throws TenmoServiceException {

		Transfers[] transfers = tenmoService.history(getUserId());
		int accountId = tenmoService.getAccount(getUserId()).getUserId();
			
		System.out.println("\n------------------------------" +
				"\nTransfers\n" +
				"\nID\tFrom/To\t\tAmount" +
				"\n------------------------------");
			
			for (int i=0; i<transfers.length; i++) {

				if(accountId == transfers[i].getAccountFrom()) {
					int accountToUserId = tenmoService.getUserIdFromAcountId(transfers[i].getAccountTo());
					String toAccountUsername = tenmoService.findUserNameByUserId(accountToUserId);

					System.out.println(transfers[i].getTransferId() + "\tTo: " + toAccountUsername.toUpperCase() +
									"\t$ " + transfers[i].getAmount());
				} else {
					int accountFromUserId = tenmoService.getUserIdFromAcountId(transfers[i].getAccountFrom());
					String fromAccountUsername = tenmoService.findUserNameByUserId(accountFromUserId);
					System.out.println(transfers[i].getTransferId() + "\tFrom: " + fromAccountUsername.toUpperCase() +
							"\t$ " + transfers[i].getAmount());
				}
			}
			System.out.println("\nPlease enter Transfer ID to view details (0 to cancel): ");
			int transferId = Integer.parseInt(userInput.nextLine());
			
			if (transferId == 0) {
				System.out.println();
			} else {
				Transfers transfer = tenmoService.transferDetails(transferId);
				int accountToUserId = tenmoService.getUserIdFromAcountId(transfer.getAccountTo());
				String toAccountUsername = tenmoService.findUserNameByUserId(accountToUserId);
				int accountFromUserId = tenmoService.getUserIdFromAcountId(transfer.getAccountFrom());
				String fromAccountUsername = tenmoService.findUserNameByUserId(accountFromUserId);
				String status = tenmoService.getTransferStatusName(transfer.getTransferStatusId());
				String transferType = tenmoService.getTransferTypeName(transfer.getTransferTypeId());
			
				System.out.println("\n------------------------------" +
								"\nTransfer Details" +
								"\n------------------------------" +
								"\nId: " + transfer.getTransferId() +
								"\nFrom: " + fromAccountUsername.toUpperCase() +
								"\nTo: " + toAccountUsername.toUpperCase() +
								"\nType: " + transferType +
								"\nStatus: " + status +
								"\nAmount: $" + transfer.getAmount());
			}				
	}
			
	private void viewPendingRequests() throws TenmoServiceException {
		
		int accountId = tenmoService.getAccount(getUserId()).getAccountId();
		Transfers[] transfers = tenmoService.pending(accountId);
		
		boolean pendingRequest = false;
		
		for (int i=0; i<transfers.length; i++) {
			if(accountId == transfers[i].getAccountFrom()) {		
				pendingRequest = true;
			}
		}
		
		if (pendingRequest == true) {
			System.out.println("\n------------------------------" +
					"\nPending Transfers\n" +
					"\nID\tTo\t\tAmount" +
					"\n------------------------------");
		}
		
		for (int i=0; i<transfers.length; i++) {
			if(accountId == transfers[i].getAccountFrom()) {
				int accountToUserId = tenmoService.getUserIdFromAcountId(transfers[i].getAccountTo());
				String toAccountUsername = tenmoService.findUserNameByUserId(accountToUserId);
				System.out.println(transfers[i].getTransferId() + "\tTo: " + toAccountUsername.toUpperCase() +
								"\t\t$ " + transfers[i].getAmount());
			}
		}
				
		if (pendingRequest == true) {
			System.out.print("\nPlease enter Transfer ID to approve or reject request (0 to cancel): ");
			int transferId = Integer.parseInt(userInput.nextLine());
			System.out.print("\n1: Approve\n2: Reject\n0: Don't approve or reject\n----------\nPlease choose an option:");
			int transferStatusId = Integer.parseInt(userInput.nextLine());
			
			if (transferStatusId == 1) {
				transferStatusId = 2;				
			} else if (transferStatusId ==2) {
				transferStatusId = 3;
			} else System.out.println("Request status will remain \"Pending\"");
			
			if (transferStatusId == 3) {
				for (int i=0; i<transfers.length; i++) {
					if (transferId == transfers[i].getTransferId()) {
						transfers[i].setTransferStatusId(transferStatusId);
						tenmoService.updateTransferStatus(transfers[i]); // giving us an error. Kicks us out of app once reject is complete
						System.out.println("Reject successful");
					}
				}
			} else if (transferStatusId == 2) {
				for (int i=0; i<transfers.length; i++) {
					if (transferId == transfers[i].getTransferId()) {
						transfers[i].setTransferStatusId(transferStatusId);
						tenmoService.updateTransferStatus(transfers[i]);
						tenmoService.updateBalanceFromTransfer(transfers[i]); // gives us an error and does not update balance in database. 
					}
				}
			}
			
		} else System.out.println("You have no pending requests.");
	}

	private void sendBucks() throws TenmoServiceException {
		User[] allUsers = tenmoService.getUsers();
		System.out.println("\n------------------------------" +
				"\nUsers\n" +
				"\nID\t\tName" +
				"\n------------------------------");
		for (int i = 0; i < allUsers.length; i++) {
			System.out.println(allUsers[i].getId() + "\t\t" + allUsers[i].getUsername().toUpperCase());
		}
		System.out.println("------------------------------");
		System.out.println("\nEnter ID of user you are sending to (0 to cancel): ");
		int userId = Integer.parseInt(userInput.nextLine());
		System.out.println("\nEnter amount: ");
		BigDecimal amountToTransfer = new BigDecimal(Integer.parseInt(userInput.nextLine()));
		
		Transfers transfer = new Transfers();
		int accountId = tenmoService.getAccount(getUserId()).getAccountId();
		transfer.setAccountFrom(accountId);
		int accountToId = tenmoService.getAccount(userId).getAccountId();
		transfer.setAccountTo(accountToId);
		transfer.setAmount(amountToTransfer);
		
		tenmoService.sendTransfer(transfer);
		
		
	}

	private void requestBucks() throws TenmoServiceException {
		User[] allUsers = tenmoService.getUsers();
		System.out.println("\n------------------------------" +
				"\nUsers\n" +
				"\nID\t\tName" +
				"\n------------------------------");
		for (int i = 0; i < allUsers.length; i++) {
			System.out.println(allUsers[i].getId() + "\t\t" + allUsers[i].getUsername().toUpperCase());
		}
		System.out.println("------------------------------");
		System.out.println("\nEnter ID of user you are requesting from (0 to cancel): ");
		int userId = Integer.parseInt(userInput.nextLine());
		System.out.println("\nEnter amount: ");
		BigDecimal amountTransfer = new BigDecimal(Integer.parseInt(userInput.nextLine()));
		
		Transfers transfer = new Transfers();
		int accountId = tenmoService.getAccount(getUserId()).getAccountId();
		transfer.setAccountFrom(accountId);
		int accountToId = tenmoService.getAccount(userId).getAccountId();
		transfer.setAccountTo(accountToId);
		transfer.setAmount(amountTransfer);
		
		tenmoService.requestTransfer(transfer);
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		user = username;
		return new UserCredentials(username, password);
	}
	
	private int getUserId() throws TenmoServiceException {
	int user_id = tenmoService.getUser(user).getId();
	return user_id;
	}
	
	
}
