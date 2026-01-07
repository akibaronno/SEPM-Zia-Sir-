package validation.service;

import validation.exceptions.InvalidAgeException;
import validation.exceptions.InvalidDepartmentException;

public class Validator {

    // Validate age
    public static void validateAge(int age) throws InvalidAgeException {
        if (age < 18 || age > 60) {
            throw new InvalidAgeException("Age must be between 18 and 60");
        }
    }

    // Validate department
    public static void validateDepartment(String dept)
            throws InvalidDepartmentException {

        if (!(dept.equalsIgnoreCase("CSE")
                || dept.equalsIgnoreCase("EEE")
                || dept.equalsIgnoreCase("BBA"))) {
            throw new InvalidDepartmentException("Invalid Department");
        }
    }
}
