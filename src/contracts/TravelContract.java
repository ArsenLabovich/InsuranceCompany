package contracts;

import company.InsuranceCompany;
import objects.LegalForm;
import objects.Person;
import payment.ContractPaymentData;

import java.util.Objects;
import java.util.Set;

public class TravelContract extends AbstractContract {

    private final Set<Person> insuredPersons;

    public TravelContract(String contractNumber, InsuranceCompany insurer, Person policyHolder, ContractPaymentData contractPaymentData, int coverageAmount, Set<Person> personsToInsure) {

        super(contractNumber, insurer, policyHolder, contractPaymentData, coverageAmount);

        validateInsuredPersons(personsToInsure);
        validateContractPaymentData(contractPaymentData);

        this.insuredPersons = personsToInsure;
    }

    public Set<Person> getInsuredPersons() {
        return insuredPersons;
    }

    ///  Validation methods

    private void validateInsuredPersons(Set<Person> personsToInsure) {
        if (personsToInsure == null) {
            throw new IllegalArgumentException("The set of persons to insure cannot be null");
        }
        if (personsToInsure.isEmpty()) {
            throw new IllegalArgumentException("At least one person must be insured");
        }
        if (personsToInsure.stream().anyMatch(person -> person.getLegalForm() == LegalForm.LEGAL)) {
            throw new IllegalArgumentException("Only  natural persons can be insured in a travel contract");
        }
    }

    private void validateContractPaymentData(ContractPaymentData contractPaymentData) {
        if (contractPaymentData == null) {
            throw new IllegalArgumentException("Contract payment data cannot be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TravelContract that = (TravelContract) o;
        return Objects.equals(insuredPersons, that.insuredPersons);
    }

}
