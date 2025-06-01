package objects;

import contracts.AbstractContract;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Person {
    private final String id;
    private final LegalForm legalForm;
    private int paidOutAmount;
    private final Set<AbstractContract> contracts;

    public Person(String id) {
        if (isValidBirthNumber(id)) {
            this.id = id;
            this.legalForm = LegalForm.NATURAL;
        } else if (isValidRegistrationNumber(id)) {
            this.id = id;
            this.legalForm = LegalForm.LEGAL;
        } else {
            throw new IllegalArgumentException("Invalid ID or registration number");
        }
        this.paidOutAmount = 0;
        this.contracts = new LinkedHashSet<>();
    }


    public static boolean isValidBirthNumber(String birthNumber) {
        if (birthNumber == null || !(birthNumber.length() == 9 || birthNumber.length() == 10) || !birthNumber.matches("\\d+")) {
            return false;
        }

        String yearStr = birthNumber.substring(0, 2);
        String monthStr = birthNumber.substring(2, 4);
        String dayStr = birthNumber.substring(4, 6);

        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        int day = Integer.parseInt(dayStr);

        if (month >= 51 && month <= 62) {
            month -= 50;
        }

        if (month < 1 || month > 12) {
            return false;
        }

        if (birthNumber.length() == 9) {
            if (year > 53) {
                return false;
            }
            year = 1900 + year;

        } else {
            int sum = 0;
            for (int i = 0; i < birthNumber.length(); i++) {
                int digit = Character.getNumericValue(birthNumber.charAt(i));
                sum += (int) (Math.pow(-1, i) * digit);
            }
            if (sum % 11 != 0) {
                return false;
            }
        }
        try {
            LocalDate check = LocalDate.of(year, month, day);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isValidRegistrationNumber(String registrationNumber) {
        return registrationNumber != null && (registrationNumber.length() == 6 || registrationNumber.length() == 8) && registrationNumber.matches("\\d+");
    }

    public String getId() {
        return id;
    }

    public int getPaidOutAmount() {
        return paidOutAmount;
    }

    public LegalForm getLegalForm() {
        return legalForm;
    }

    public Set<AbstractContract> getContracts() {
        return contracts;
    }

    public void addContract(AbstractContract contract) {
        validateContract(contract);
        contracts.add(contract);
    }

    public void payout(int paidOutAmount) {
        validatePaidOutAmount(paidOutAmount);
        this.paidOutAmount += paidOutAmount;
    }

    /// Validation methods

    private void validatePaidOutAmount(int paidOutAmount) {
        if (paidOutAmount <= 0) {
            throw new IllegalArgumentException("Paid out amount must be positive");
        }
    }

    private void validateContract(AbstractContract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(this.id, person.id);
    }

}

