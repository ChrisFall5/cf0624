public class Ladder extends Tool {
    public Ladder() {
        toolCode = ToolCode.LADW;
        toolType = "Ladder";
        toolBrand = ToolCode.LADW.brand;
        dailyCharge = 1.99;
        weekendCharge = true;
        holidayCharge = false;
    }
}
