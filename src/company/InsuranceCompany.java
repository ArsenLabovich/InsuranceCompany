package company;

import contracts.*;
import objects.Person;
import objects.Vehicle;
import payment.ContractPaymentData;
import payment.PaymentHandler;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class InsuranceCompany {
    private final Set<AbstractContract> contracts;
    private final PaymentHandler handler;
    private LocalDateTime currentTime;

    public InsuranceCompany(LocalDateTime currentTime) {
        validateTime(currentTime);
        this.currentTime = currentTime;
        this.contracts = new LinkedHashSet<>();
        this.handler = new PaymentHandler(this);
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalDateTime currentTime) {
        validateTime(currentTime);
        this.currentTime = currentTime;
    }

    public Set<AbstractContract> getContracts() {
        return contracts;
    }

    public PaymentHandler getHandler() {
        return handler;
    }


    public SingleVehicleContract insureVehicle(String contractNumber, Person beneficiary, Person policyHolder, int proposedPremium, PremiumPaymentFrequency proposedPaymentFrequency, Vehicle vehicleToInsure) {
        validatePremiumSingleVehicle(proposedPremium, vehicleToInsure, proposedPaymentFrequency);

        ContractPaymentData contractPaymentData = new ContractPaymentData(proposedPremium, proposedPaymentFrequency, this.getCurrentTime(), 0);
        SingleVehicleContract singleVehicleContract = new SingleVehicleContract(contractNumber, this, beneficiary, policyHolder, contractPaymentData, vehicleToInsure.getOriginalValue() / 2, vehicleToInsure);

        this.chargePremiumOnContract(singleVehicleContract);

        this.contracts.add(singleVehicleContract);
        policyHolder.addContract(singleVehicleContract);

        return singleVehicleContract;
    }

    public TravelContract insurePersons(String contractNumber, Person policyHolder, int proposedPremium, PremiumPaymentFrequency proposedPaymentFrequency, Set<Person> personsToInsure) {
        validatePremiumTravel(proposedPremium, proposedPaymentFrequency, personsToInsure);

        ContractPaymentData contractPaymentData = new ContractPaymentData(proposedPremium, proposedPaymentFrequency, this.getCurrentTime(), 0);
        TravelContract travelContract = new TravelContract(contractNumber, this, policyHolder, contractPaymentData, personsToInsure.size() * 10, personsToInsure);

        this.chargePremiumOnContract(travelContract);

        this.contracts.add(travelContract);
        policyHolder.addContract(travelContract);

        return travelContract;
    }

    public MasterVehicleContract createMasterVehicleContract(String contractNumber, Person beneficiary, Person policyHolder) {

        MasterVehicleContract masterVehicleContract = new MasterVehicleContract(contractNumber, this, beneficiary, policyHolder);

        this.contracts.add(masterVehicleContract);
        policyHolder.addContract(masterVehicleContract);

        return masterVehicleContract;
    }

    public void moveSingleVehicleContractToMasterVehicleContract(MasterVehicleContract masterVehicleContract, SingleVehicleContract singleVehicleContract) {
        validateContractsPresenceForMoving(masterVehicleContract, singleVehicleContract);
        validateContractsInsuranceForMoving(masterVehicleContract, singleVehicleContract);

        singleVehicleContract.getInsurer().getContracts().remove(singleVehicleContract);
        singleVehicleContract.getPolicyHolder().getContracts().remove(singleVehicleContract);

        masterVehicleContract.getChildContracts().add(singleVehicleContract);

    }

    public void chargePremiumsOnContracts() {
        contracts.stream().filter(AbstractContract::isActive).forEach(AbstractContract::updateBalance);
    }


    public void chargePremiumOnContract(MasterVehicleContract contract) {
        contract.getChildContracts().stream().filter(AbstractContract::isActive).forEach(AbstractContract::updateBalance);
    }

    public void chargePremiumOnContract(AbstractContract contract) {
        if (!contract.isActive()) {
            return;
        }
        ContractPaymentData paymentData = contract.getContractPaymentData();
        int premium = paymentData.getPremium();
        while (!currentTime.isBefore(paymentData.getNextPaymentTime())) {
            int currentOutstandingBalance = paymentData.getOutstandingBalance();
            paymentData.setOutstandingBalance(currentOutstandingBalance + premium);
            paymentData.updateNextPaymentTime();
        }
    }

    public void processClaim(TravelContract travelContract, Set<Person> affectedPersons) {

        validateContractDataTravel(travelContract, affectedPersons);
        validateContractActivityStatus(travelContract);

        int payoutPerPerson = travelContract.getCoverageAmount() / affectedPersons.size();
        affectedPersons.forEach(person -> person.payout(payoutPerPerson));
        travelContract.setInactive();
    }

    public void processClaim(SingleVehicleContract singleVehicleContract, int expectedDamages) {
        validateProcessClaimDataSingleVehicle(singleVehicleContract, expectedDamages);
        validateContractActivityStatus(singleVehicleContract);


        Person beneficiary = singleVehicleContract.getBeneficiary();
        int coverageAmount = singleVehicleContract.getCoverageAmount();

        if (beneficiary != null) {
            beneficiary.payout(coverageAmount);
        } else {
            singleVehicleContract.getPolicyHolder().payout(coverageAmount);
        }
        if (expectedDamages >= 0.7 * singleVehicleContract.getInsuredVehicle().getOriginalValue()) {
            singleVehicleContract.setInactive();
        }
    }

    /// Helper methods for validation

    private void validateTime(LocalDateTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
    }

    private void validatePremiumSingleVehicle(int proposedPremium, Vehicle vehicleToInsure, PremiumPaymentFrequency proposedPaymentFrequency) {
        if (vehicleToInsure == null || proposedPaymentFrequency == null) {
            throw new IllegalArgumentException("Vehicle to insure and premium payment frequency cannot be null");
        }
        if (proposedPremium * paymentsPerYear(proposedPaymentFrequency) < 0.02 * vehicleToInsure.getOriginalValue()) {
            throw new IllegalArgumentException("The annual premium must be at least 2% of the vehicle's value.");
        }
    }

    private void validatePremiumTravel(int proposedPremium, PremiumPaymentFrequency proposedPaymentFrequency, Set<Person> personsToInsure) {
        if (personsToInsure == null || proposedPaymentFrequency == null) {
            throw new IllegalArgumentException("Persons to insure and premium payment frequency cannot be null");
        }
        if (proposedPremium * paymentsPerYear(proposedPaymentFrequency) < 5 * personsToInsure.size()) {
            throw new IllegalArgumentException("The annual premium must be at least five times the number of insured persons.");
        }
    }

    private void validateContractsPresenceForMoving(MasterVehicleContract c1, SingleVehicleContract c2) {
        if (c1 == null || c2 == null) {
            throw new IllegalArgumentException("Contracts cannot be null");
        }
    }

    private void validateContractsInsuranceForMoving(MasterVehicleContract c1, SingleVehicleContract c2) {
        if (!c1.isActive() || !c2.isActive()) {
            throw new InvalidContractException("Contracts must be active");
        }
        if (!Objects.equals(c1.getInsurer(), c2.getInsurer())) {
            throw new InvalidContractException("Contracts must have the same insurer");
        }
        if (!Objects.equals(this, c2.getInsurer()) || !Objects.equals(this, c1.getInsurer())) {
            throw new InvalidContractException("Contracts must have the same insurer");
        }
        if (!Objects.equals(c1.getPolicyHolder(), c2.getPolicyHolder())) {
            throw new InvalidContractException("Contracts must have the same policyholder");
        }
        if (!c1.getPolicyHolder().getContracts().contains(c1) || !c2.getPolicyHolder().getContracts().contains(c2)) {
            throw new InvalidContractException("Contracts must be present in the policyHolder's contract list");
        }
        if (!c1.getInsurer().getContracts().contains(c1) || !c2.getInsurer().getContracts().contains(c2)) {
            throw new InvalidContractException("Contracts must be present in the insurer's contract list");
        }
    }

    private void validateProcessClaimDataSingleVehicle(SingleVehicleContract singleVehicleContract, int expectedDamages) {
        if (singleVehicleContract == null) {
            throw new IllegalArgumentException("SingleVehicleContract cannot be null");
        }
        if (expectedDamages <= 0) {
            throw new IllegalArgumentException("Expected damages must be positive");
        }
    }

    private void validateContractActivityStatus(AbstractContract contract) {
        if (!contract.isActive()) {
            throw new InvalidContractException("The contract must be active");
        }
    }

    private void validateContractDataTravel(TravelContract travelContract, Set<Person> affectedPersons) {
        if (travelContract == null) {
            throw new IllegalArgumentException("TravelContract cannot be null");
        }
        if (affectedPersons == null || affectedPersons.isEmpty()) {
            throw new IllegalArgumentException("Affected persons must be a non-empty set");
        }
        if (!travelContract.getInsuredPersons().containsAll(affectedPersons)) {
            throw new IllegalArgumentException("Affected persons must be a subset of insured persons in the contract");
        }
    }

    /// Helper method to calculate the number of payments per year based on the payment frequency

    private int paymentsPerYear(PremiumPaymentFrequency premiumPaymentFrequency) {
        return 12 / premiumPaymentFrequency.getValueInMonths();
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InsuranceCompany that = (InsuranceCompany) o;
        return Objects.equals(contracts, that.contracts) && Objects.equals(handler, that.handler) && Objects.equals(currentTime, that.currentTime);
    }


}

