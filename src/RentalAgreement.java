import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RentalAgreement {
    public Tool toolRented;
    public LocalDate checkoutDate;
    public LocalDate dueDate;
    public int rentalDays; // number of days tool is rented for
    public int chargeDays; // number of days charges apply, from day after rental, through and including the due date
    public int discountPercent;
    public double preDiscountCharge;
    public double discountAmount;
    public double finalCharge;

    public RentalAgreement(
            Tool toolRented,
            LocalDate checkoutDate,
            LocalDate dueDate,
            int rentalDays,
            int chargeDays,
            int discountPercent,
            double preDiscountCharge,
            double discountAmount,
            double finalCharge
    ) {
        this.toolRented = toolRented;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.rentalDays = rentalDays;
        this.chargeDays = chargeDays;
        this.discountPercent = discountPercent;
        this.preDiscountCharge = preDiscountCharge;
        this.discountAmount = discountAmount;
        this.finalCharge = finalCharge;
    }

    /**
     * Converts double value for currency into readable String (i.e. $9,999.99)
     *
     * @param amount the price to be formatted
     * @return human-readable String representation of the amount
     */
    public String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(amount);
    }

    /**
     * Converts date object into readable string with format MM/DD/YYYY
     *
     * @param date the date object to be formatted
     * @return String representation of date in format MM/DD/YYYY
     */
    public String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return formatter.format(date);
    }

    /**
     * Returns percentage as readable String.
     *
     * @param percent int value provided which represents percentage
     * @return String representation of percentage (i.e. "25%")
     */
    public String formatPercent(int percent) {
        return percent + "%";
    }

    /**
     * Prints the rental agreement to the console.
     */
    public void printRentalAgreement() {
        System.out.println("RENTAL AGREEMENT");
        System.out.println("Tool code: " + toolRented.toolCode);
        System.out.println("Tool type: " + toolRented.toolType);
        System.out.println("Tool brand: " + toolRented.toolBrand);
        System.out.println("Rental days: " + rentalDays);
        System.out.println("Checkout date: " + formatDate(checkoutDate));
        System.out.println("Due date: " + formatDate(dueDate));
        System.out.println("Daily rental charge: " + formatCurrency(toolRented.dailyCharge));
        System.out.println("Charge days: " + chargeDays);
        System.out.println("Pre-discount charge: " + formatCurrency(preDiscountCharge));
        System.out.println("Discount percent: " + formatPercent(discountPercent));
        System.out.println("Discount amount: " + formatCurrency(discountAmount));
        System.out.println("Final charge: " + formatCurrency(finalCharge));
    }
}
