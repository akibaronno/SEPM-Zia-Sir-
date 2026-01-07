package app;

import java.util.Scanner;
import validation.service.Validator;
import validation.exceptions.*;

public class TestValidation {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Enter Age: ");
            int age = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter Department (CSE/EEE/BBA): ");
            String dept = sc.nextLine();

            // Validation calls
            Validator.validateAge(age);
            Validator.validateDepartment(dept);

            System.out.println("Validation Successful!");

        } catch (InvalidAgeException | InvalidDepartmentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }
}
