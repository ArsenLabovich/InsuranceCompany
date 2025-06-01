package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

import java.util.Objects;

public abstract class AbstractContract {

    private final String contractNumber;
    protected final InsuranceCompany insurer;
    protected final Person policyHolder;
    protected final ContractPaymentData contractPaymentData;
    protected int coverageAmount;
    protected boolean isActive;

    public AbstractContract(String contractNumber, InsuranceCompany insurer, Person policyHolder, ContractPaymentData contractPaymentData, int coverageAmount) {

        validateContractData(contractNumber, insurer, policyHolder);
        validateCoverageAmount(coverageAmount);
        validateContractNumberUniqueness(contractNumber, insurer);

        this.contractNumber = contractNumber;
        this.insurer = insurer;
        this.policyHolder = policyHolder;
        this.contractPaymentData = contractPaymentData;
        this.coverageAmount = coverageAmount;
        this.isActive = true;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public Person getPolicyHolder() {
        return policyHolder;
    }

    public InsuranceCompany getInsurer() {
        return insurer;
    }

    public int getCoverageAmount() {
        return coverageAmount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setInactive() {
        this.isActive = false;
    }

    public void setCoverageAmount(int coverageAmount) {
        validateCoverageAmount(coverageAmount);
        this.coverageAmount = coverageAmount;
    }

    public ContractPaymentData getContractPaymentData() {
        return contractPaymentData;
    }

    public void pay(int amount) {
        this.getInsurer().getHandler().pay(this, amount);
    }

    public void updateBalance() {
        this.getInsurer().chargePremiumOnContract(this);
    }


    /// Validation methods
    private void validateContractData(String contractNumber, InsuranceCompany insurer, Person policyHolder) {
        if (contractNumber == null || insurer == null || policyHolder == null) {
            throw new IllegalArgumentException("Contract number, insurer, and policyholder cannot be null");
        }
        if (contractNumber.isEmpty()) {
            throw new IllegalArgumentException("Contract number cannot be empty");
        }
    }

    private void validateCoverageAmount(int coverageAmount) {
        if (coverageAmount < 0) {
            throw new IllegalArgumentException("Coverage amount cannot be negative");
        }
    }

    private void validateContractNumberUniqueness(String contractNumber, InsuranceCompany insurer) {
        if (!isValidContractNumber(contractNumber, insurer)) {
            throw new IllegalArgumentException("Contract number already exists");
        }
    }


    private boolean isValidContractNumber(String contractNumber, InsuranceCompany insurer) {
        return insurer.getContracts().stream().noneMatch(contract -> contract.getContractNumber().equals(contractNumber));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractContract that = (AbstractContract) o;
        return coverageAmount == that.coverageAmount && isActive == that.isActive && Objects.equals(contractNumber, that.contractNumber) && Objects.equals(insurer, that.insurer) && Objects.equals(policyHolder, that.policyHolder) && Objects.equals(contractPaymentData, that.contractPaymentData);
    }

}
