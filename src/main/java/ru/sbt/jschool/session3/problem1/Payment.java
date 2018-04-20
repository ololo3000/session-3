package ru.sbt.jschool.session3.problem1;

import java.util.Objects;

/**
 */
public class Payment {
    private final long operationID;

    private final long payerID;
    private final long payerAccountID;

    private final long recipientID;
    private final long recipientAccountID;

    private final float amount;

    public Payment(long operationID, long payerID, long payerAccountID, long recipientID, long recipientAccountID,
        float amount) {
        this.operationID = operationID;
        this.payerID = payerID;
        this.payerAccountID = payerAccountID;
        this.recipientID = recipientID;
        this.recipientAccountID = recipientAccountID;
        this.amount = amount;
    }

    public long getPayerID() {
        return payerID;
    }

    public long getPayerAccountID() {
        return payerAccountID;
    }

    public long getRecipientID() {
        return recipientID;
    }

    public long getRecipientAccountID() {
        return recipientAccountID;
    }

    public float getAmount() {
        return amount;
    }

    public long getOperationID() {
        return operationID;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Payment payment = (Payment)o;
        return operationID == payment.operationID &&
            payerID == payment.payerID &&
            payerAccountID == payment.payerAccountID &&
            recipientID == payment.recipientID &&
            recipientAccountID == payment.recipientAccountID &&
            Float.compare(payment.amount, amount) == 0;
    }

    @Override public int hashCode() {

        return Objects.hash(operationID, payerID, payerAccountID, recipientID, recipientAccountID, amount);
    }
}
