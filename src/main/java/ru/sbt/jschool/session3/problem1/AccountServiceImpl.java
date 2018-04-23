package ru.sbt.jschool.session3.problem1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 */
public class AccountServiceImpl implements AccountService {
    protected FraudMonitoring fraudMonitoring;
    private ReentrantLock operationCheckLock = new ReentrantLock();

    private Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private Map<Long, Set<Account>> clients = new ConcurrentHashMap<>();
    private Set<Long> operations = ConcurrentHashMap.newKeySet();

    public AccountServiceImpl(FraudMonitoring fraudMonitoring) {
        this.fraudMonitoring = fraudMonitoring;
    }

    @Override
    public Result create(long clientID, long accountID, float initialBalance, Currency currency) {
        if (fraudMonitoring.check(clientID)) {
            return Result.FRAUD;
        }

        if (accounts.putIfAbsent(accountID, new Account(clientID, accountID, currency, initialBalance)) != null) {
            return Result.ALREADY_EXISTS;
        }

        clients.putIfAbsent(clientID, ConcurrentHashMap.newKeySet());
        clients.get(clientID).add(accounts.get(accountID));

        return Result.OK;
    }

    @Override
    public Set<Account> findForClient(long clientID) {
        return clients.get(clientID);
    }

    @Override
    public Account find(long accountID) {
        return accounts.get(accountID);
    }

    @Override
    public Result doPayment(Payment payment) {
        operationCheckLock.lock();
        try {
            if (operations.contains(payment.getOperationID())) {
                return Result.ALREADY_EXISTS;
            }
            operations.add(payment.getOperationID());
        } finally {
            operationCheckLock.unlock();
        }


        Account payer = accounts.get(payment.getPayerAccountID());
        Account recipient = accounts.get(payment.getRecipientAccountID());

        if (payer == null || payment.getPayerID() != payer.getClientID()) {
            operations.remove(payment.getOperationID());
            return Result.PAYER_NOT_FOUND;
        }

        if (recipient == null || payment.getRecipientID() != recipient.getClientID()) {
            operations.remove(payment.getOperationID());
            return Result.RECIPIENT_NOT_FOUND;
        }

        if (fraudMonitoring.check(recipient.getClientID())) {
            operations.remove(payment.getOperationID());
            return Result.FRAUD;
        }

        if (fraudMonitoring.check(payer.getClientID())) {
            operations.remove(payment.getOperationID());
            return Result.FRAUD;
        }


        if (!payer.withdrawFromBalance(payment.getAmount()))  {
            operations.remove(payment.getOperationID());
            return Result.INSUFFICIENT_FUNDS;
        }

        float amount;
        if (payer.getCurrency() != recipient.getCurrency()) {
            amount = payer.getCurrency().to(payment.getAmount(), recipient.getCurrency());
        } else {
            amount = payment.getAmount();
        }

        recipient.depositeInToBalance(amount);


        return Result.OK;
    }
}
