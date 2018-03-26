package com.bank.userUI;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bank.Utility;
import com.bank.entity.Account;
import com.bank.entity.AccountImpl;
import com.bank.entity.Bank;
import com.bank.form.LoginForm;

@Component
public class BankMainUI {
	
	public static boolean proceed = true;
	
	@Autowired
	private Bank bank;
	
	@Autowired
	private LoginForm loginForm;

	public void bankMainPage() {
		LoginUI.proceed = false;
		accountListText();
		menuText();
		
		while (proceed) {

			switch (Utility.userInput().nextLine()) {

			case ("1"):
				depositOrWithdraw(true);
				break;
			case ("2"):
				depositOrWithdraw(false);
				break;
			case ("3"):
				transfer();
				break;		
			case ("4"):
				openNewAccount();
				break;
			case ("5"):
				proceed = false;
				break;	
			default:
				Utility.log().info("no such option");
				accountListText();
				menuText();
			}
		}
	}
	
	private void openNewAccount() {
		bank.getClient(loginForm).getAccountsList().add(new AccountImpl());
		accountListText();
		menuText();
	}
	
	private void depositOrWithdraw(boolean deposit) {

		String accountName;
		double amount = 0;

		if (deposit)
			Utility.log().info("Enter name of the account for money deposit");
		else
			Utility.log().info("Enter name of the account for money withdraw");

		accountName = Utility.userInput().nextLine();

		try {

			if (deposit)
				Utility.log().info("Enter the amount of money to deposit");
			else
				Utility.log().info("Enter the amount of money to withdraw");

			amount = Utility.userInput().nextDouble();

			for (Account a : bank.getClient(loginForm).getAccountsList())
				if (a.getAccountName().equals(accountName)) {
					if (deposit) {
						if (!a.deposit(amount))
							Utility.log().info("Error - Cannot deposit negative amount");
					} else if (!a.withdraw(amount))
						Utility.log().info("Error - Insufficient funds or negative value passed as amount");
				}

		} catch (InputMismatchException e) {
			Utility.log().info("Error - Wrong input. Enter a valid amount of money");
		}

		if (!bank.getClient(loginForm).getAccountsList().stream().anyMatch(a -> a.getAccountName().equals(accountName)))
			Utility.log().info("Error - Wrong account name");

		accountListText();
		menuText();
	}
	
	private void transfer() {

		String senderAccountName;
		String recipientAccountName;
		boolean success = true;
		double amount = 0;

		Utility.log().info("Enter name of the 'SENDER' account");
		senderAccountName = Utility.userInput().nextLine();

		Utility.log().info("Enter name of the 'RECIPIENT' account");
		recipientAccountName = Utility.userInput().nextLine();

		try {
			Utility.log().info("Enter the amount of money to transfer");
			amount = Utility.userInput().nextDouble();

			if (bank.getClient(loginForm).getAccountsList().stream()
					.anyMatch(a -> a.getAccountName().equals(senderAccountName))
					&& bank.getClient(loginForm).getAccountsList().stream()
							.anyMatch(a -> a.getAccountName().equals(recipientAccountName))) {

				for (Account a : bank.getClient(loginForm).getAccountsList()) {
					if (a.getAccountName().equals(senderAccountName))
						if (!a.withdraw(amount)) {
							Utility.log().info("Error - Insufficient funds or negative value passed as amount");
							success = false;
						}
				}

				if (success) {
					for (Account a : bank.getClient(loginForm).getAccountsList()) {
						if (a.getAccountName().equals(recipientAccountName))
							a.deposit(amount);
					}
				}

			}
		} catch (InputMismatchException e) {
			Utility.log().info("Error - Wrong input. Enter a valid amount of money");
		}
		
		if (!bank.getClient(loginForm).getAccountsList().stream().anyMatch(a -> a.getAccountName().equals(senderAccountName)) 
				|| !bank.getClient(loginForm).getAccountsList().stream().anyMatch(a -> a.getAccountName().equals(recipientAccountName)))
			Utility.log().info("Error - At least one account name is wrong");
		
		accountListText();
		menuText();
	}
	
	private void accountListText() {
		Utility.log().info("+-----------------------------------------------------------------------------+");
		Utility.log().info("|                               Bank Main Page                                |");
		Utility.log().info("+--------------------------------------------------------+--------------------+");
		Utility.log().info("| List of your current Accounts     |    Account Name    |      Balance       |");
		Utility.log().info("+-----------------------------------+--------------------+--------------------+");
		printAccountsText();
	}
	
	private void printAccountsText() {
		try {
			for (Account a : bank.getClient(loginForm).getAccountsList()) {
				Utility.log().info("| " + a.displayAccountNo() + "  |     " + a.getAccountName() + "      | "
						+ a.checkBalance());
				Utility.log().info("+-----------------------------------|--------------------|--------------------+");

			}
		} catch (NoSuchElementException e) {
			Utility.log().warn("Error when loading accounts list");
		}
	}
	
	private void menuText() {
		Utility.log().info("+---------------------------------------------+");
		Utility.log().info("|                  Main Menu                  |");
		Utility.log().info("+---------------------------------------------+");
		Utility.log().info("| 1. Deposit                                  |");
		Utility.log().info("| 2. Withdraw                                 |");
		Utility.log().info("| 3. Transfer                                 |");
		Utility.log().info("| 4. Open new account                         |");
		Utility.log().info("| 5. Quit                                     |");
		Utility.log().info("| Wait for next commit to see more actions :) |");
		Utility.log().info("+---------------------------------------------+");
	}
	
}
