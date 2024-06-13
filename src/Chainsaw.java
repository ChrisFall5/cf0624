public class Chainsaw extends Tool {
    public Chainsaw() {
        toolCode = ToolCode.CHNS;
        toolType = "Chainsaw";
        toolBrand = ToolCode.CHNS.brand;
        dailyCharge = 1.49;
        weekendCharge = false;
        holidayCharge = true;
    }
}
