package org.asdt.interfaces;

import org.asdt.exception.InvalidPasswordException;

public interface ATMHardware {
    Long getAccountNumberFromCard(String password) throws InvalidPasswordException;
    void pay(Long amount);
    Long readEnvelope();
}
