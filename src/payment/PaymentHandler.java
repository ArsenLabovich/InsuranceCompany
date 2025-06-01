package payment;

import company.InsuranceCompany;
import contracts.AbstractContract;
import contracts.InvalidContractException;
import contracts.MasterVehicleContract;
import contracts.SingleVehicleContract;

import java.util.*;

public class PaymentHandler {
    private final Map<AbstractContract, Set<PaymentInstance>> paymentHistory;
    private final InsuranceCompany insurer;

    public PaymentHandler(InsuranceCompany insurer) {
        validateInsuranceCompany(insurer);
        this.insurer = insurer;
        this.paymentHistory = new HashMap<>();
    }

    public Map<AbstractContract, Set<PaymentInstance>> getPaymentHistory() {
        return paymentHistory;
    }

    public void pay(MasterVehicleContract contract, int amount) {
        validateContract(contract, amount);
        validateInsurance(contract);

        processChildContracts(contract, amount);

        PaymentInstance paymentInstance = new PaymentInstance(this.insurer.getCurrentTime(), amount);
        addPaymentInstance(contract, paymentInstance);
    }

    public void pay(AbstractContract contract, int amount) {
        validateContract(contract, amount);
        validateInsurance(contract);

        int currentOutstandingBalance = contract.getContractPaymentData().getOutstandingBalance();
        int newOutstangingBalance = currentOutstandingBalance - amount;

        setNewOutstandingBalance(contract, newOutstangingBalance);
        PaymentInstance paymentInstance = new PaymentInstance(this.insurer.getCurrentTime(), amount);
        addPaymentInstance(contract, paymentInstance);
    }

    /// Validation methods
    private void validateInsuranceCompany(InsuranceCompany insurer) {
        if (insurer == null) {
            throw new IllegalArgumentException("Insurance company cannot be null");
        }
    }

    private void validateContract(AbstractContract contract, int amount) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private void validateInsurance(AbstractContract contract) {
        if (!contract.isActive()) {
            throw new InvalidContractException("Contract is not active");
        }
        if (!Objects.equals(contract.getInsurer(), insurer)) {
            throw new InvalidContractException("Contract does not belong to this insurance company");
        }
    }

    private void validateInsurance(MasterVehicleContract contract) {
        if (!contract.isActive()) {
            throw new InvalidContractException("Contract is not active");
        }
        if (contract.getInsurer() != insurer) {
            throw new InvalidContractException("Contract does not belong to this insurance company");
        }
        if (contract.getChildContracts().isEmpty()) {
            throw new InvalidContractException("Master vehicle contract has no child contracts");
        }
    }

    ///  Addition Methods to keep code clean and readable
    /// contains methods that are used multiple times

    private void addPaymentInstance(AbstractContract contract, PaymentInstance paymentInstance) {
        paymentHistory.computeIfAbsent(contract, k -> new TreeSet<>()).add(paymentInstance);
    }

    private void setNewOutstandingBalance(AbstractContract contract, int newOutstandingBalance) {
        contract.getContractPaymentData().setOutstandingBalance(newOutstandingBalance);
    }

    private void processChildContracts(MasterVehicleContract contract, int amount) {
        for (SingleVehicleContract childContract : contract.getChildContracts()) {
            if (childContract.isActive()) {
                int outstandingBalance = childContract.getContractPaymentData().getOutstandingBalance();
                if (outstandingBalance > 0) {
                    if (amount >= outstandingBalance) {
                        amount -= outstandingBalance;
                        childContract.getContractPaymentData().setOutstandingBalance(0);
                    } else {
                        childContract.getContractPaymentData().setOutstandingBalance(outstandingBalance - amount);
                        amount = 0;
                    }
                }
            }
        }

        while (amount > 0) {
            for (SingleVehicleContract childContract : contract.getChildContracts()) {
                if (childContract.isActive()) {
                    int premium = childContract.getContractPaymentData().getPremium();
                    int outstandingBalance = childContract.getContractPaymentData().getOutstandingBalance();
                    if (amount >= premium) {
                        childContract.getContractPaymentData().setOutstandingBalance(outstandingBalance - premium);
                        amount -= premium;
                    } else {
                        childContract.getContractPaymentData().setOutstandingBalance(outstandingBalance - amount);
                        amount = 0;
                    }
                }
            }
        }
    }


}
