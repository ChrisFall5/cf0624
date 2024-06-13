public class Jackhammer extends Tool {
    public Jackhammer(ToolCode code) {
        toolCode = code;
        toolType = "Jackhammer";
        toolBrand = code.brand;
        dailyCharge = 2.99;
        weekendCharge = false;
        holidayCharge = false;
    }
}
