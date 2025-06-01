package contracts;

import company.InsuranceCompany;
import objects.LegalForm;
import objects.Person;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class MasterVehicleContract extends AbstractVehicleContract {

    private final Set<SingleVehicleContract> childContracts;

    public MasterVehicleContract(String contractNumber, InsuranceCompany insurer, Person beneficiary, Person policyHolder) {

        super(contractNumber, insurer, beneficiary, policyHolder, null, 0);

        validatePolicyHolder(policyHolder);

        this.childContracts = new LinkedHashSet<>();

    }

    public Set<SingleVehicleContract> getChildContracts() {
        return childContracts;
    }

    public void requestAdditionOfChildContract(SingleVehicleContract contract) {
        this.getInsurer().moveSingleVehicleContractToMasterVehicleContract(this, contract);
    }


    ///  Overriding for master contract, because MasterVehicleContract needs
    ///  to call different methods and has different logic for some methods
    @Override
    public void pay(int amount) {
        this.getInsurer().getHandler().pay(this, amount);
    }

    @Override
    public void updateBalance() {
        this.getInsurer().chargePremiumOnContract(this);
    }

    @Override
    public boolean isActive() {
        if (this.childContracts.isEmpty()) {
            return super.isActive();
        } else {
            return this.childContracts.stream().anyMatch(SingleVehicleContract::isActive);
        }
    }

    @Override
    public void setInactive() {
        super.setInactive();
        childContracts.forEach(SingleVehicleContract::setInactive);
    }


    /// Validation methods
    private void validatePolicyHolder(Person policyHolder) {
        if (policyHolder.getLegalForm() != LegalForm.LEGAL) {
            throw new IllegalArgumentException("Policyholder of MasterVehicleContract can be only a legal entity");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MasterVehicleContract that = (MasterVehicleContract) o;
        return Objects.equals(childContracts, that.childContracts);
    }

}
