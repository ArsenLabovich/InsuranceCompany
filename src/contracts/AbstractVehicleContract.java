package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

import java.util.Objects;

public abstract class AbstractVehicleContract extends AbstractContract {
    protected Person beneficiary;

    public AbstractVehicleContract(String contractNumber, InsuranceCompany insurer, Person beneficiary, Person policyHolder, ContractPaymentData contractPaymentData, int coverageAmount) {
        super(contractNumber, insurer, policyHolder, contractPaymentData, coverageAmount);
        validateBeneficiary(beneficiary, policyHolder);
        this.beneficiary = beneficiary;
    }

    public void setBeneficiary(Person beneficiary) {
        validateBeneficiary(beneficiary, this.policyHolder);
        this.beneficiary = beneficiary;
    }

    public Person getBeneficiary() {
        return beneficiary;
    }


    ///  Validation methods
    private void validateBeneficiary(Person beneficiary, Person policyHolder) {
        if (Objects.equals(beneficiary, policyHolder)) {
            throw new IllegalArgumentException("Beneficiary cannot be the same as policyholder");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractVehicleContract that = (AbstractVehicleContract) o;
        return Objects.equals(beneficiary, that.beneficiary);
    }

}
