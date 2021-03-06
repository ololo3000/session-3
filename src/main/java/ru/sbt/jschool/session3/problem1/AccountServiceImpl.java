package ru.sbt.jschool.session3.problem1;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 */
public class AccountServiceImpl implements AccountService {
    protected FraudMonitoring fraudMonitoring;
    private Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private Map<Long, Set<Account>> clients = new ConcurrentHashMap<>();
    private Set<Long> executedOperations = ConcurrentHashMap.newKeySet();
    private Map<Long, Lock> onGoingOperations = new ConcurrentHashMap<>();

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
        ReentrantLock l = new ReentrantLock();
        ReentrantLock existing = (ReentrantLock) onGoingOperations.putIfAbsent(payment.getOperationID(), l);
        if (existing != null) {
            l = existing;
        }

        l.lock();
        try {

            if (executedOperations.contains(payment.getOperationID())) {
                return Result.ALREADY_EXISTS;
            }

            Account payer = accounts.get(payment.getPayerAccountID());
            Account recipient = accounts.get(payment.getRecipientAccountID());

            if (payer == null || payment.getPayerID() != payer.getClientID()) {
                return Result.PAYER_NOT_FOUND;
            }

            if (recipient == null || payment.getRecipientID() != recipient.getClientID()) {
                return Result.RECIPIENT_NOT_FOUND;
            }

            if (fraudMonitoring.check(recipient.getClientID())) {
                return Result.FRAUD;
            }

            if (fraudMonitoring.check(payer.getClientID())) {
                return Result.FRAUD;
            }

            float recAmount;
            if (payer.getCurrency() != recipient.getCurrency()) {
                recAmount = payer.getCurrency().to(payment.getAmount(), recipient.getCurrency());
            } else {
                recAmount = payment.getAmount();
            }

            if (!payer.withdrawFromBalance(payment.getAmount())) {
                return Result.INSUFFICIENT_FUNDS;
            }

            recipient.depositeInToBalance(recAmount);
            executedOperations.add(payment.getOperationID());

            return Result.OK;
        } finally {
            l.unlock();
            onGoingOperations.remove(payment.getOperationID());
        }
    }
}
