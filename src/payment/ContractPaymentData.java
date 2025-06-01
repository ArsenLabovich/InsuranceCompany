package payment;

import java.time.LocalDateTime;

public class ContractPaymentData {
    private int premium;
    private PremiumPaymentFrequency premiumPaymentFrequency;
    private LocalDateTime nextPaymentTime;
    private int outstandingBalance;

    public ContractPaymentData(int premium, PremiumPaymentFrequency premiumPaymentFrequency, LocalDateTime nextPaymentTime, int outstandingBalance) {
        validateData(premium, premiumPaymentFrequency, nextPaymentTime, outstandingBalance);
        this.premium = premium;
        this.premiumPaymentFrequency = premiumPaymentFrequency;
        this.nextPaymentTime = nextPaymentTime;
        this.outstandingBalance = outstandingBalance;
    }

    public int getPremium() {
        return premium;
    }

    public void setPremium(int premium) {
        validatePremium(premium);
        this.premium = premium;

    }

    public void setOutstandingBalance(int outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public int getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setPremiumPaymentFrequency(PremiumPaymentFrequency premiumPaymentFrequency) {
        validatePremiumPaymentFrequency(premiumPaymentFrequency);
        this.premiumPaymentFrequency = premiumPaymentFrequency;
    }

    public PremiumPaymentFrequency getPremiumPaymentFrequency() {
        return premiumPaymentFrequency;
    }

    public LocalDateTime getNextPaymentTime() {
        return nextPaymentTime;
    }

    public void updateNextPaymentTime() {
        nextPaymentTime = nextPaymentTime.plusMonths(premiumPaymentFrequency.getValueInMonths());
    }


    /// Validation methods
    private void validateData(int premium, PremiumPaymentFrequency premiumPaymentFrequency, LocalDateTime nextPaymentTime, int outstandingBalance) {
        validatePremium(premium);
        validatePremiumPaymentFrequency(premiumPaymentFrequency);
        validateNextPaymentTime(nextPaymentTime);
    }

    private void validatePremium(int premium) {
        if (premium <= 0) {
            throw new IllegalArgumentException("Premium must be positive");
        }
    }

    private void validatePremiumPaymentFrequency(PremiumPaymentFrequency premiumPaymentFrequency) {
        if (premiumPaymentFrequency == null) {
            throw new IllegalArgumentException("Premium payment frequency cannot be null");
        }
    }

    private void validateNextPaymentTime(LocalDateTime nextPaymentTime) {
        if (nextPaymentTime == null) {
            throw new IllegalArgumentException("Next payment time cannot be null");
        }
    }
}