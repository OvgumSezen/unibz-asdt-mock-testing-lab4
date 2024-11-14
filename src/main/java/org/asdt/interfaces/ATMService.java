package org.asdt.interfaces;

import org.asdt.exception.InvalidAccountNumberException;

public interface ATMService {
    Long getAccountBalance(Long accNum) throws InvalidAccountNumberException;
    void persistAccountBalance(Long accNum, Long balance);
}
