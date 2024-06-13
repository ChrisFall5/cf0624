import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import static java.time.temporal.TemporalAdjusters.firstInMonth;

public class ToolRental {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Determines when Independence Day should be observed.
     * If holiday falls on weekend day, it is observed on the closest weekday
     *
     * @param independenceDay the actual holiday, July 4 of a given year
     * @return LocalDate object for the date Independence day is observed
     */
    protected LocalDate getObservedIndependenceDay(LocalDate independenceDay) {
        LocalDate observedIndependenceDay = independenceDay;
        if (independenceDay.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            observedIndependenceDay = observedIndependenceDay.plusDays(-1);
        } else if (independenceDay.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            observedIndependenceDay = observedIndependenceDay.plusDays(1);
        }
        return observedIndependenceDay;
    }

    /**
     * Calculate the number of charge days, from day after rental through the due date
     *
     * @param numRentalDays total number of days in rental period
     * @param rentedTool the tool that is being rented
     * @param checkoutDate the date the tool was rented
     * @param dueDate the date the tool is due back
     * @return int representation of the number of days the customer will be charged
     */
    protected int getNumChargeDays(
            int numRentalDays,
            Tool rentedTool,
            LocalDate checkoutDate,
            LocalDate dueDate
    ) {
        int numChargeDays = numRentalDays;
        if (!rentedTool.weekendCharge) {
            int numWeekendDays = numWeekendDaysInRange(checkoutDate, numRentalDays);
            numChargeDays -= numWeekendDays;
        }
        if (!rentedTool.holidayCharge) {
            int numHolidays = numHolidaysInRange(checkoutDate, dueDate);
            numChargeDays -= numHolidays;
        }

        return numChargeDays;
    }

    /**
     * Rounds currency value to the nearest hundredths value
     * Multiply input by 100 to shift decimal and use Math.round() to handle rounding
     * Then divide by 100.0 to shift decimal back
     *
     * @param amount un-rounded value representing a currency amount
     * @return input amount rounded to the nearest hundredth
     */
    protected double roundCurrencyValue(double amount) {
        long rounded = Math.round(amount * 100);
        return rounded / 100.0;
    }

    /**
     * Determines if there is one or more holidays in a given date range.
     * For the purposes of this project, the only holidays considered are:
     * Independence Day: July 4 - Observed on closest weekday if it falls on a weekend
     * Labor Day: the first Monday in September
     *
     * @param startDate beginning of date range
     * @param endDate end of date range
     * @return int value representing the number of holidays in the date range provided
     */
    protected int numHolidaysInRange(LocalDate startDate, LocalDate endDate) {
        int numHolidays = 0;
        int startDateYear = startDate.getYear();
        Month startDateMonth = startDate.getMonth();
        int startDateDayOfMonth = startDate.getDayOfMonth();
        int endDateYear = endDate.getYear();
        LocalDate independenceDay;
        LocalDate laborDay;

        while(startDateYear <= endDateYear) {
            // Determine next occurrence of holidays
            // Month.getValue() returns an int representing month: Jan = 1 ... Dec = 12
            int monthInt = startDateMonth.getValue();
            if (monthInt < 7 || (monthInt == 7 && startDateDayOfMonth <= 4)) {
                independenceDay = LocalDate.of(startDateYear, 7, 4);
                LocalDate firstOfSeptember = LocalDate.of(startDateYear, 9, 1);
                laborDay = firstOfSeptember.with(firstInMonth(DayOfWeek.MONDAY));
            } else if (monthInt < 9) {
                // If holiday has passed for current year, next occurrence is the following year
                independenceDay = LocalDate.of(startDateYear + 1, 7, 4);

                // Labor day will still be the current year
                LocalDate firstOfSeptember = LocalDate.of(startDateYear, 9, 1);
                laborDay = firstOfSeptember.with(firstInMonth(DayOfWeek.MONDAY));
            } else if (monthInt == 9) { // Case: start date month is September
                // Next Independence Day is the following year
                independenceDay = LocalDate.of(startDateYear + 1, 7, 4);

                // Determine the day of month for first monday in september and compare against startDateDayOfMonth
                LocalDate firstOfSeptember = LocalDate.of(startDateYear, 9, 1);
                LocalDate firstMondayInSeptember = firstOfSeptember.with(firstInMonth(DayOfWeek.MONDAY));
                if (startDateDayOfMonth < firstMondayInSeptember.getDayOfMonth()) {
                    // start date is before labor day, so next occurrence is in the current year
                    laborDay = firstMondayInSeptember;
                } else {
                    // start date is after labor day, so next occurrence is the following year
                    firstOfSeptember =  LocalDate.of(startDateYear + 1, 9, 1);
                    laborDay = firstOfSeptember.with(firstInMonth(DayOfWeek.MONDAY));
                }
            } else { // both holidays have passed for the current year
                independenceDay = LocalDate.of(startDateYear + 1, 7, 4);
                LocalDate firstOfSeptember = LocalDate.of(startDateYear + 1, 9, 1);
                laborDay = firstOfSeptember.with(firstInMonth(DayOfWeek.MONDAY));
            }

            // Case: Independence Day
            LocalDate observedIndependenceDay = getObservedIndependenceDay(independenceDay);
            if (startDate.isBefore(observedIndependenceDay) && endDate.isAfter(observedIndependenceDay)) {
                numHolidays++;
            }

            // Case: Labor Day
            if (startDate.isBefore(laborDay) && endDate.isAfter(laborDay)) {
                numHolidays++;
            }

            startDateYear++;
        }

        return numHolidays;
    }

    /**
     * Determines the number of weekend days in a given date range
     *
     * @param startDate beginning of date range
     * @param rentalDays the number of days following the start date
     * @return the number of weekend days in the given date range
     */
    protected int numWeekendDaysInRange(LocalDate startDate, int rentalDays) {
        int numWeekendDays = 0;
        DayOfWeek startDateDayOfWeek = startDate.getDayOfWeek();
        int numFullWeeks = rentalDays / 7;
        int numAdditionalDays = rentalDays % 7;

        // Add 2 weekend days for each full week of time
        numWeekendDays += (2 * numFullWeeks);

        /*
            DayOfWeek.getValue() returns an int representation of the day of week
            Monday = 1 ... Sunday = 7

            Cases where one additional weekend day should be included:
            * dayOfWeekInt + numAdditionalDays == 6 (i.e. Friday (5) + 1 additional day)
            * day of week is Sunday (7) with the maximum (6) additional days -> 7 + 6 = 13
            * day of week is Saturday (6) and there are one or more additional days

            Cases where 2 additional weekend days should be included:
            * day of week is Monday-Friday (1-5) and the sum of dayOfWeekInt + numAdditionalDays >= 7
                (i.e. Friday (5) + 2 additional days)
         */
        int dayOfWeekInt = startDateDayOfWeek.getValue();
        if (dayOfWeekInt + numAdditionalDays == 6 || dayOfWeekInt + numAdditionalDays == 13 || (dayOfWeekInt == 6 && numAdditionalDays > 0)) {
            numWeekendDays++;
        } else if (dayOfWeekInt + numAdditionalDays >= 7 && dayOfWeekInt < 6) {
            numWeekendDays += 2;
        }

        return numWeekendDays;
    }

    /**
     * Generates Rental Agreement instance with user-provided data
     *
     * @param code ToolCode for the tool being rented
     * @param numRentalDays number of days tool will be rented for
     * @param discountPercent int representation of discount percentage to be applied
     * @param checkoutDate the date the tool is being rented
     * @return RentalAgreement instance based on input
     */
    public RentalAgreement generateRentalAgreement (
            ToolCode code,
            int numRentalDays,
            int discountPercent,
            LocalDate checkoutDate
    ) throws Exception {
        // Handle bad input
        if (discountPercent > 100 || discountPercent < 0) {
            throw new Exception("The discount percent must be a whole number between 0-100.");
        }

        if (numRentalDays < 1) {
            throw new Exception("The minimum rental period for a tool is 1 day.");
        }

        Tool rentedTool;

        // Set rented tool based on tool code provided.
        switch (code) {
            case CHNS -> rentedTool = new Chainsaw();
            case LADW -> rentedTool = new Ladder();
            default -> rentedTool = new Jackhammer(code);
        }

        // Set due date to numRentalDays after the checkout date
        LocalDate dueDate = checkoutDate.plusDays(numRentalDays);

        // Calculate the number of days charges apply, from day after rental through due date.
        int numChargeDays = getNumChargeDays(numRentalDays, rentedTool, checkoutDate, dueDate);

        double preDiscountCharge = roundCurrencyValue(rentedTool.dailyCharge * numChargeDays);
        double discountAmount = roundCurrencyValue(preDiscountCharge * (discountPercent / 100.0));
        double finalAmount = roundCurrencyValue(preDiscountCharge - discountAmount);

        return new RentalAgreement(
                rentedTool,
                checkoutDate,
                dueDate,
                numRentalDays,
                numChargeDays,
                discountPercent,
                preDiscountCharge,
                discountAmount,
                finalAmount
        );
    }

    /**
     * Runs the checkout process which asks the user to provide input that will
     * be used to complete the checkout process.
     */
    public void checkout() {
        Scanner scanner = new Scanner(System.in);
        ToolCode code;
        int numRentalDays;
        int discountPercent;
        LocalDate checkoutDate;

        System.out.println("Welcome to checkout!");

        // Get tool code input from user.
        while (true) {
            System.out.print("Please enter tool code: ");
            try {
                code = ToolCode.valueOf(scanner.next().toUpperCase());
                break;
            } catch (Exception e) {
                System.out.println("The tool code provided is invalid, please try again.");
            }
        }

        // Get number of rental days from user. Value must be >= 1.
        while (true) {
            System.out.print("Please enter the number of days for this rental: ");
            try {
                numRentalDays = scanner.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("The number of rental days must be a whole number >= 1, please try again.");
                scanner.nextLine();
            }
        }

        // Get number of rental days from user. Value must be >= 1.
        while (true) {
            System.out.print("Please enter the discount percentage as a whole number (0-100): ");
            try {
                discountPercent = scanner.nextInt();
                if (discountPercent < 0 || discountPercent > 100) {
                    throw new Exception("Percentage is not a value between 0 - 100.");
                }
                break;
            } catch (Exception e) {
                System.out.println("Discount percentage must be a whole number between 0-100, please try again.");
                scanner.nextLine();
            }
        }

        // Get checkout date from user.
        while (true) {
            System.out.print("Please enter checkout date in the format (MM/DD/YYYY): ");
            try {
                checkoutDate = LocalDate.parse(scanner.next(), formatter);
                break;
            } catch (Exception e) {
                System.out.println("Invalid date provided, please try again.");
            }
        }
        // End user input
        scanner.close();

        try {
            RentalAgreement rentalAgreement = generateRentalAgreement(code, numRentalDays, discountPercent, checkoutDate);
            System.out.println(); // add newline before rental agreement is printed
            rentalAgreement.printRentalAgreement();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        ToolRental toolRental = new ToolRental();
        toolRental.checkout();
    }
}