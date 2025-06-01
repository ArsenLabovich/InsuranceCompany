package contracts;

import company.InsuranceCompany;
import objects.Person;
import objects.Vehicle;
import payment.ContractPaymentData;

import java.util.Objects;

public class SingleVehicleContract extends AbstractVehicleContract {
    private final Vehicle insuredVehicle;

    public SingleVehicleContract(String contractNumber, InsuranceCompany insurer, Person beneficiary, Person policyHolder, ContractPaymentData contractPaymentData, int coverageAmount, Vehicle vehicleToInsure) {

        super(contractNumber, insurer, beneficiary, policyHolder, contractPaymentData, coverageAmount);

        validateInsuredVehicle(vehicleToInsure);
        validateContractPaymentData(contractPaymentData);

        this.insuredVehicle = vehicleToInsure;
    }

    public Vehicle getInsuredVehicle() {
        return insuredVehicle;
    }

    ///  Validation methods

    private void validateContractPaymentData(ContractPaymentData contractPaymentData) {
        if (contractPaymentData == null) {
            throw new IllegalArgumentException("Contract payment data cannot be null");
        }
    }

    private void validateInsuredVehicle(Vehicle vehicleToInsure) {
        if (vehicleToInsure == null) {
            throw new IllegalArgumentException("Vehicle to insure cannot be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SingleVehicleContract that = (SingleVehicleContract) o;
        return Objects.equals(insuredVehicle, that.insuredVehicle);
    }
}
