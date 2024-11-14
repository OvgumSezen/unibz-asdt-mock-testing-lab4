import org.asdt.ATM;
import org.asdt.exception.InvalidAccountNumberException;
import org.asdt.interfaces.ATMHardware;
import org.asdt.interfaces.ATMService;
import org.asdt.exception.InvalidPasswordException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ATMTest {
    ATMService atmService = mock(ATMService.class);
    ATMHardware atmHardware = mock(ATMHardware.class);

    ATM atm = new ATM(atmHardware, atmService);

    @Test
    public void loginTest_whenPasswordIsCorrect() throws InvalidPasswordException {
        Long accNum = 111L;
        String expectedLogin = "Account number " + accNum;

        when(atmHardware.getAccountNumberFromCard(any(String.class))).thenReturn(accNum);

        String actualLogin = atm.login("correctPassword");

        assertEquals(expectedLogin, actualLogin);
    }

    @Test
    public void loginTest_whenPasswordIsIncorrect() throws InvalidPasswordException {
        String expectedLogin = "Invalid Password";

        when(atmHardware.getAccountNumberFromCard(any(String.class))).thenThrow(new InvalidPasswordException("Invalid Password"));

        String actualLogin = atm.login("incorrectPassword");

        assertEquals(expectedLogin, actualLogin);
    }

    @Test
    public void balanceTest_whenAccNumValid() throws InvalidPasswordException, InvalidAccountNumberException {
        Long balance = 10000L;
        Long accNum = 111L;
        String expectedAccBalance = "The account " + accNum + " has balance " + balance + "$";

        when(atmHardware.getAccountNumberFromCard(any(String.class))).thenReturn(accNum);
        when(atmService.getAccountBalance(any(Long.class))).thenReturn(balance);

        String actualAccBalance = atm.balance("correctPassword");

        assertEquals(expectedAccBalance, actualAccBalance);
    }

    @Test
    public void balanceTest_whenAccNumInvalid() throws InvalidPasswordException, InvalidAccountNumberException {
        Long accNum = 111L;
        String expectedAccBalance = "Could not get balance";

        when(atmHardware.getAccountNumberFromCard(any(String.class))).thenReturn(accNum);
        when(atmService.getAccountBalance(any(Long.class))).thenThrow(new InvalidAccountNumberException("Invalid Account Number"));

        String actualAccBalance = atm.balance("correctPassword");

        assertEquals(expectedAccBalance, actualAccBalance);
    }

    @Test
    public void withdrawTest_ValidAmount() throws InvalidAccountNumberException, InvalidPasswordException {
        Long accNum = 111L;
        Long balance = 10000L;
        Long amount = 1000L;

        String expectedWithdrawal = "Withdrawal successful: " + amount + "$";

        when(atmHardware.getAccountNumberFromCard(any(String.class))).thenReturn(accNum);
        when(atmService.getAccountBalance(any(Long.class))).thenReturn(balance);
        doNothing().when(atmHardware).pay(any(Long.class));
        doNothing().when(atmService).persistAccountBalance(any(Long.class), any(Long.class));

        String actualWithdrawal = atm.withdraw("correctPassword", amount);

        verify(atmHardware).pay(amount);
        verify(atmService).persistAccountBalance(accNum, balance - amount);
        assertEquals(expectedWithdrawal, actualWithdrawal);
    }

    @Test
    public void withdrawTest_InvalidAmount() throws InvalidPasswordException, InvalidAccountNumberException {
        Long accNum = 111L;
        Long balance = 10000L;
        Long amount = 1000000L;

        String expectedWithdrawal = "Could not withdraw";

        when(atmHardware.getAccountNumberFromCard(any(String.class))).thenReturn(accNum);
        when(atmService.getAccountBalance(any(Long.class))).thenReturn(balance);

        String actualWithdrawal = atm.withdraw("password", amount);

        assertEquals(expectedWithdrawal, actualWithdrawal);
    }

    @Test
    public void depositTest_whenSuccessful() throws InvalidPasswordException, InvalidAccountNumberException {
        Long accNum = 111L;
        Long balance = 10000L;
        Long envelopeValue = 1000L;

        String expectedDeposit = "Deposit successful " + envelopeValue + "$" + " new balance " + (balance + envelopeValue) + "$";

        when(atmHardware.getAccountNumberFromCard(any(String.class))).thenReturn(accNum);
        when(atmService.getAccountBalance(any(Long.class))).thenReturn(balance);
        when(atmHardware.readEnvelope()).thenReturn(envelopeValue);
        doNothing().when(atmService).persistAccountBalance(any(Long.class), any(Long.class));

        String actualDeposit = atm.deposit("password");

        verify(atmService).persistAccountBalance(accNum, balance + envelopeValue);
        assertEquals(expectedDeposit, actualDeposit);
    }

    @Test
    public void depositTest_whenUnsuccessful() throws InvalidPasswordException, InvalidAccountNumberException {
        Long accNum = 111L;

        String expectedDeposit = "Could not deposit";

        when(atmHardware.getAccountNumberFromCard(any(String.class))).thenReturn(accNum);
        when(atmService.getAccountBalance(any(Long.class))).thenReturn(null);

        String actualDeposit = atm.deposit("password");

        assertEquals(expectedDeposit, actualDeposit);
    }

}
