package payment;

import java.time.LocalDateTime;

public class PaymentInstance implements Comparable<PaymentInstance> {
    private final LocalDateTime paymentTime;
    private final int paymentAmount;

    public PaymentInstance(LocalDateTime paymentTime, int paymentAmount) {
        validateData(paymentTime, paymentAmount);
        this.paymentTime = paymentTime;
        this.paymentAmount = paymentAmount;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    /// Validation methods

    private void validateData(LocalDateTime paymentTime, int paymentAmount) {
        if (paymentTime == null) {
            throw new IllegalArgumentException("Payment time must not be null");
        }
        if (paymentAmount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
    }


    ///  Implementing of Comparable interface to sort payment instances by payment time
    @Override
    public int compareTo(PaymentInstance other) {
        return this.paymentTime.compareTo(other.paymentTime);
    }
}
