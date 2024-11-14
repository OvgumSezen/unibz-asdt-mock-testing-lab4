package org.asdt;

import org.asdt.exception.InvalidAccountNumberException;
import org.asdt.exception.InvalidPasswordException;
import org.asdt.interfaces.ATMHardware;
import org.asdt.interfaces.ATMService;

public class ATM {
    private final ATMHardware atmHardware;
    private final ATMService atmService;

    public ATM(ATMHardware atmHardware, ATMService atmService) {
        this.atmHardware = atmHardware;
        this.atmService = atmService;
    }

    public String login(String password) {
        Long accNum = getAccNum(password);
        if(accNum == null) {
            return "Invalid Password";
        }
        return "Account number " + accNum;
    }

    public String balance(String password) {
        Long accNum = getAccNum(password);
        Long balance = retrieveBalance(accNum);

        if(balance == null) {
            return "Could not get balance";
        }
        return "The account " + accNum + " has balance " + balance + "$";
    }

    public String withdraw(String password, Long amount) {
        Long accNum = getAccNum(password);
        Long balance = retrieveBalance(accNum);

        if(balance == null || amount > balance) {
            return "Could not withdraw";
        }

        atmHardware.pay(amount);
        atmService.persistAccountBalance(accNum, balance - amount);

        return "Withdrawal successful: " + amount + "$";
    }

    public String deposit(String password) {
        Long accNum = getAccNum(password);
        Long balance = retrieveBalance(accNum);
        Long envelopeValue = atmHardware.readEnvelope();

        if(balance == null) {
            return "Could not deposit";
        }

        atmService.persistAccountBalance(accNum, balance + envelopeValue);

        return "Deposit successful " + envelopeValue + "$" + " new balance " + (balance + envelopeValue) + "$";
    }

    private Long retrieveBalance(Long accNum) {
        try {
            return atmService.getAccountBalance(accNum);
        } catch (InvalidAccountNumberException e) {
            return null;
        }
    }

    private Long getAccNum(String password) {
        try{
            return atmHardware.getAccountNumberFromCard(password);
        } catch(InvalidPasswordException e) {
            return null;
        }
    }
}
