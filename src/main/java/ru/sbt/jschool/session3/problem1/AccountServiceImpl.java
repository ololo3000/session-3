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

/**
 */
public class AccountServiceImpl implements AccountService {
    protected FraudMonitoring fraudMonitoring;

    private Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private Map<Long, List<Account>> clients = new ConcurrentHashMap<>();
    private Set<Long> operations = ConcurrentHashMap.newKeySet();

    public AccountServiceImpl(FraudMonitoring fraudMonitoring) {
        this.fraudMonitoring = fraudMonitoring;
    }

    @Override
    public Result create(long clientID, long accountID, float initialBalance, Currency currency) {
        if (fraudMonitoring.check(clientID)) {
            return Result.FRAUD;
        }

        if (!accounts.containsKey(accountID)) {
            Account account = new Account(clientID, accountID, currency, initialBalance);
            accounts.put(accountID, account);
            if (clients.containsKey(clientID)) {
                clients.get(clientID).add(account);
            } else {
                List<Account> list = new CopyOnWriteArrayList<>();
                list.add(account);
                clients.put(clientID, list);
            }
            return Result.OK;
        }
        return Result.ALREADY_EXISTS;
    }

    @Override
    public List<Account> findForClient(long clientID) {
        return clients.get(clientID);
    }

    @Override
    public Account find(long accountID) {
        return accounts.get(accountID);
    }

    @Override
    public Result doPayment(Payment payment) {
        if (operations.contains(payment.getOperationID())) {
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


        if (!payer.withdrawMoneyFromBalance(payment.getAmount()))  {
            return Result.INSUFFICIENT_FUNDS;
        }

        float amount;
        if (payer.getCurrency() != recipient.getCurrency()) {
            amount = payer.getCurrency().to(payment.getAmount(), recipient.getCurrency());
        } else {
            amount = payment.getAmount();
        }

        recipient.depositeInToBalance(amount);

        operations.add(payment.getOperationID());

        return Result.OK;
    }
}
