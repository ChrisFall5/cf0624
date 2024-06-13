import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ToolRentalTest {
    @Test
    void testGetObservedIndependenceDay() {
        ToolRental toolRental = new ToolRental();

        // Test Independence day from different years
        LocalDate twentyFifteen = LocalDate.of(2015, 7, 4); // Saturday -> Friday before
        LocalDate twentyTwentyOne = LocalDate.of(2021, 7, 4); // Sunday -> Monday after
        LocalDate twentyTwentyFour = LocalDate.of(2024, 7, 4); // Thursday -> same day

        // Test holiday on various years
        assertEquals(LocalDate.of(2015, 7, 3), toolRental.getObservedIndependenceDay(twentyFifteen));
        assertEquals(LocalDate.of(2021, 7, 5), toolRental.getObservedIndependenceDay(twentyTwentyOne));
        assertEquals(LocalDate.of(2024, 7, 4), toolRental.getObservedIndependenceDay(twentyTwentyFour));
    }

    @Test
    void testNumWeekendDaysInRange() {
        ToolRental toolRental = new ToolRental();

        // Test date is a Thursday (June 13, 2024)
        LocalDate testDate = LocalDate.of(2024, 6, 13);
        // Test date 2 is a Sunday (June 16, 2024)
        LocalDate testDate2 = LocalDate.of(2024, 6, 16);
        // Test date 3 is a Saturday (June 15, 2024)
        LocalDate testDate3 = LocalDate.of(2024, 6, 15);

        // Check various ranges for expected number of weekend days
        assertEquals(4, toolRental.numWeekendDaysInRange(testDate, 10));
        assertEquals(2, toolRental.numWeekendDaysInRange(testDate, 5));
        assertEquals(1, toolRental.numWeekendDaysInRange(testDate, 2));
        assertEquals(0, toolRental.numWeekendDaysInRange(testDate, 1));

        assertEquals(2, toolRental.numWeekendDaysInRange(testDate2, 10));
        assertEquals(0, toolRental.numWeekendDaysInRange(testDate2, 5));
        assertEquals(0, toolRental.numWeekendDaysInRange(testDate2, 2));
        assertEquals(0, toolRental.numWeekendDaysInRange(testDate2, 1));

        assertEquals(3, toolRental.numWeekendDaysInRange(testDate3, 10));
        assertEquals(1, toolRental.numWeekendDaysInRange(testDate3, 5));
        assertEquals(1, toolRental.numWeekendDaysInRange(testDate3, 2));
        assertEquals(1, toolRental.numWeekendDaysInRange(testDate3, 1));
    }

    @Test
    void testNumHolidaysInRange() {
        ToolRental toolRental = new ToolRental();

        // Test calendar year(s)
        LocalDate jan1 = LocalDate.of(2024, 1,1);
        LocalDate dec31 = LocalDate.of(2024, 12,31);
        LocalDate nextDec31 = LocalDate.of(2025, 12,31);

        assertEquals(2, toolRental.numHolidaysInRange(jan1, dec31));
        assertEquals(4, toolRental.numHolidaysInRange(jan1, nextDec31));

        // Test months before any holidays occur
        LocalDate june30 = LocalDate.of(2024, 6,30);

        assertEquals(0, toolRental.numHolidaysInRange(jan1, june30));

        // Test range including independence day 2024
        LocalDate july1 = LocalDate.of(2024, 7, 1);
        LocalDate july5 = LocalDate.of(2024, 7, 5);

        assertEquals(1, toolRental.numHolidaysInRange(july1, july5));

        // Test range including Labor Day 2024 (Sept. 2)
        LocalDate sept1 = LocalDate.of(2024, 9, 1);
        LocalDate sept5 = LocalDate.of(2024, 9, 5);

        assertEquals(1, toolRental.numHolidaysInRange(sept1, sept5));
    }

    @Test
    void testRoundCurrencyValue() {
        ToolRental toolRental = new ToolRental();

        double shouldRoundDown = 5.12345;
        double shouldRoundUp = 123.4567;
        double hundredthsTest = 12.34;

        assertEquals(5.12, toolRental.roundCurrencyValue(shouldRoundDown));
        assertEquals(123.46, toolRental.roundCurrencyValue(shouldRoundUp));
        assertEquals(12.34, hundredthsTest);
    }

    @Test
    void testGetNumChargeDays() {
        ToolRental toolRental = new ToolRental();

        // Dates to consider as start dates (all based on 2024)
        LocalDate arbitraryDate = LocalDate.of(2024, 6, 13);
        LocalDate nearIndependenceDay = LocalDate.of(2024, 7, 1);
        LocalDate nearLaborDay = LocalDate.of(2024, 9, 1);

        // Test each tool type
        // Chainsaw: weekend charge NO, holiday charge: YES
        Chainsaw chainsaw = new Chainsaw();
        assertEquals(6, toolRental.getNumChargeDays(10, chainsaw, arbitraryDate, arbitraryDate.plusDays(10)));
        assertEquals(8, toolRental.getNumChargeDays(10, chainsaw, nearIndependenceDay, nearIndependenceDay.plusDays(10)));
        assertEquals(8, toolRental.getNumChargeDays(10, chainsaw, nearLaborDay, nearLaborDay.plusDays(10)));

        // Ladder: weekend charge: YES, holiday charge NO
        Ladder ladder = new Ladder();
        assertEquals(10, toolRental.getNumChargeDays(10, ladder, arbitraryDate, arbitraryDate.plusDays(10)));
        assertEquals(9, toolRental.getNumChargeDays(10, ladder, nearIndependenceDay, nearIndependenceDay.plusDays(10)));
        assertEquals(9, toolRental.getNumChargeDays(10, ladder, nearLaborDay, nearLaborDay.plusDays(10)));

        // Jackhammer: weekend charge: NO, holiday charge: NO
        Jackhammer jakd = new Jackhammer(ToolCode.JAKD);
        assertEquals(6, toolRental.getNumChargeDays(10, jakd, arbitraryDate, arbitraryDate.plusDays(10)));
        assertEquals(7, toolRental.getNumChargeDays(10, jakd, nearIndependenceDay, nearIndependenceDay.plusDays(10)));
        assertEquals(7, toolRental.getNumChargeDays(10, jakd, nearLaborDay, nearLaborDay.plusDays(10)));
    }

    @Test
    void testGenerateRentalAgreement() {
        ToolRental toolRental = new ToolRental();

        // Expect an exception if called with invalid input
        // numRentalDays must be >= 1
        // discountPercent must be an int between 0-100
        assertThrows(Exception.class, () -> {
            toolRental.generateRentalAgreement(
                    ToolCode.JAKR,
                    5,
                    101,
                    LocalDate.of(2015, 9, 3)
            );
        });
        assertThrows(Exception.class, () -> {
            toolRental.generateRentalAgreement(
                    ToolCode.JAKR,
                    0,
                    100,
                    LocalDate.of(2015, 9, 3)
            );
        });

        // Test various rental agreements
        try {
            RentalAgreement agreement1 = toolRental.generateRentalAgreement(
                    ToolCode.LADW,
                    3,
                    10,
                    LocalDate.of(2020, 7, 2)
            );

            // due date is 3 days after checkout date (2 + 3 = 5)
            assertEquals(LocalDate.of(2020, 7, 5), agreement1.dueDate);
            // checkout day is Thursday, Friday is holiday (no charges apply), weekend charges apply -> 2 days
            assertEquals(2, agreement1.chargeDays);
            // daily rate (1.99) * charge days (2) = 3.98
            assertEquals(3.98, agreement1.preDiscountCharge);
            // pre-discount charge (3.98) * 0.10 = 0.398 ~ 0.40
            assertEquals(0.4, agreement1.discountAmount);
            // pre-discount charge (3.98) - discount amount (0.40)
            assertEquals(3.58, agreement1.finalCharge);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            RentalAgreement agreement2 = toolRental.generateRentalAgreement(
                    ToolCode.CHNS,
                    5,
                    25,
                    LocalDate.of(2015, 7, 2)
            );

            // due date is 5 days after checkout date (2 + 5 = 7)
            assertEquals(LocalDate.of(2015, 7, 7), agreement2.dueDate);
            // checkout day is Thursday, Friday is holiday (charges apply), no weekend charges -> 3 days
            assertEquals(3, agreement2.chargeDays);
            // daily rate (1.49) * charge days (3) = 4.47
            assertEquals(4.47, agreement2.preDiscountCharge);
            // pre-discount charge (4.47) * 0.25 = 1.1175 ~ 1.12
            assertEquals(1.12, agreement2.discountAmount);
            // pre-discount charge (4.47) - discount amount (0.40)
            assertEquals(3.35, agreement2.finalCharge);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            RentalAgreement agreement3 = toolRental.generateRentalAgreement(
                    ToolCode.JAKD,
                    6,
                    0,
                    LocalDate.of(2015, 9, 3)
            );

            // due date is 6 days after checkout date (3 + 6 = 9)
            assertEquals(LocalDate.of(2015, 9, 9), agreement3.dueDate);
            // checkout day is Thursday, no weekend charges, no holiday charges (Mon.) -> 3 days
            assertEquals(3, agreement3.chargeDays);
            // daily rate (2.99) * charge days (3) = 4.47
            assertEquals(8.97, agreement3.preDiscountCharge);
            // pre-discount charge (8.97) * 0 = 0
            assertEquals(0, agreement3.discountAmount);
            // pre-discount charge (8.97) - discount amount (0)
            assertEquals(8.97, agreement3.finalCharge);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            RentalAgreement agreement4 = toolRental.generateRentalAgreement(
                    ToolCode.JAKR,
                    9,
                    0,
                    LocalDate.of(2015, 7, 2)
            );

            // due date is 9 days after checkout date (2 + 9 = 11)
            assertEquals(LocalDate.of(2015, 7, 11), agreement4.dueDate);
            // checkout day is Thursday, no weekend charges, no holiday charges (Fri.) -> 5 days
            assertEquals(5, agreement4.chargeDays);
            // daily rate (2.99) * charge days (5) = 14.95
            assertEquals(14.95, agreement4.preDiscountCharge);
            // pre-discount charge (14.95) * 0 = 0
            assertEquals(0, agreement4.discountAmount);
            // pre-discount charge (14.95) - discount amount (0)
            assertEquals(14.95, agreement4.finalCharge);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            RentalAgreement agreement5 = toolRental.generateRentalAgreement(
                    ToolCode.JAKR,
                    4,
                    50,
                    LocalDate.of(2020, 7, 2)
            );

            // due date is 4 days after checkout date (2 + 4 = 6)
            assertEquals(LocalDate.of(2020, 7, 6), agreement5.dueDate);
            // checkout day is Thursday, no weekend charges, no holiday charges (Fri.) -> 1 day
            assertEquals(1, agreement5.chargeDays);
            // daily rate (2.99) * charge days (1) = 2.99
            assertEquals(2.99, agreement5.preDiscountCharge);
            // pre-discount charge (2.99) * 0.5 = 1.495 ~ 1.50
            assertEquals(1.50, agreement5.discountAmount);
            // pre-discount charge (2.99) - discount amount (1.50)
            assertEquals(1.49, agreement5.finalCharge);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}