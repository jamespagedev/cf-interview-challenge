package com.jp0421.checkout;

import com.jp0421.tools.Tool;
import com.jp0421.tools.ToolsInventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RentForm {
    private Tool selectedTool;
    private int rentDays;
    LocalDate checkoutDate;
    LocalDate returnDate;
    private int chargeDays;
    private BigDecimal preDiscountCharge;
    private byte discount;
    private BigDecimal discountAmount;
    private BigDecimal finalCharge;

    //====================================================================================
    //================================== Constructor(s) ==================================
    //====================================================================================
    /* MyCodeStyle
        Some people prefer no constructor if it's not needed(and I'm cool with that),
        but if there are no team code standards for this,
        then I like to show the constructor is not doing anything on purpose.
     */
    public RentForm(){}

    //====================================================================================
    //================================== Helper Methods ==================================
    //====================================================================================
    private boolean isLocalDateWeekend(LocalDate day, int saturdayValue, int sundayValue) {
        return day.getDayOfWeek().getValue() == saturdayValue || day.getDayOfWeek().getValue() == sundayValue;
    }

    private boolean isLocalDateBeforeAfter4thJulyWeekend(LocalDate day, int mondayValue, int fridayValue) {
        /* Business Rule:
            Independence Day, July 4th - If falls on weekend,
            it is observed on the closest weekday (if Sat, then Friday before,
            if Sunday, then Monday after)
         */
        if (day.getMonth().getValue() == 7 && day.getDayOfMonth() == 3 && day.getDayOfWeek().getValue() == fridayValue) {
            // friday before the 4th
            return true;
        } else if(day.getMonth().getValue() == 7 && day.getDayOfMonth() == 5 && day.getDayOfWeek().getValue() == mondayValue) {
            // monday after the 4th
            return true;
        }
        return false;
    }

    private boolean isLocalDate4thJulyWeekday(LocalDate day, int saturdayValue, int sundayValue) {
        return day.getMonth().getValue() == 7 && day.getDayOfMonth() == 4 && (day.getDayOfWeek().getValue() != saturdayValue && day.getDayOfWeek().getValue() != sundayValue);
    }

    private boolean isLocalDateLaborDay(LocalDate day, int mondayValue) {
        // first monday of september
        return day.getMonth().getValue() == 9 && day.getDayOfMonth() <= 7 && day.getDayOfWeek().getValue() == mondayValue;
    }

    private boolean isLocalDateHoliday(LocalDate day, int mondayValue, int fridayValue, int saturdayValue, int sundayValue) {
        if(isLocalDate4thJulyWeekday(day, saturdayValue, sundayValue)) {
            return true;
        } else if(isLocalDateBeforeAfter4thJulyWeekend(day, mondayValue, fridayValue)) {
            return true;
        } else if(isLocalDateLaborDay(day, mondayValue)) {
            return true;
        }
        return false;
    }

    private BigDecimal calculateCurrencyPercentage(BigDecimal curValue, BigDecimal perc) {
        return curValue.multiply(perc).divide(new BigDecimal(100));
    }

    //====================================================================================
    //================================== Support Methods =================================
    //====================================================================================
    private void validateSetRentDays(int days) throws Exception {
        // handle logic errors
        if(days <= 0) {
            throw new Exception("Invalid Days(" + days + "), must be at least 1 or greater");
        }
    }

    private void validateSetDiscount(byte discount) throws Exception {
        // handle logic errors
        if(discount < 0 || discount > 100) {
            throw new Exception("Invalid Discount(" + discount + "%), must be between 0-100%");
        }
    }

    private void validateSetReturnDate() throws Exception {
        // handle logic errors
        if(getCheckoutDate() == null) {
            throw new Exception("Missing Checkout Date");
        } else if(getRentDays() <= 0) {
            throw new Exception("Invalid Days(" + getRentDays() + "), must be at least 1 or greater");
        }
    }

    private LocalDate calculateReturnDate(int days) {
        // Pre-requirement: checkoutDate must be set
        return this.checkoutDate.plusDays(days);
    }

    private int calculateChargeDays() {
        /* Pre-requirements:
            - tool must be populated with day charge settings
            - getCheckoutDate must be set
            - rentDays must be > 0
         */
        int cDays = 0;
        // first charge day is day AFTER checkoutDate
        // last charge day is day INCLUDING returnDate
        int monday = 1; // LocalDate standard value for monday
        int friday = 5; // LocalDate standard value for friday
        int saturday = 6; // LocalDate standard value for saturday
        int sunday = 7; // LocalDate standard value for sunday
        for (int day = 1; day <= getRentDays(); day++) {
            if(!getSelectedTool().isWeekendCharge() && isLocalDateWeekend(getCheckoutDate().plusDays(day), saturday, sunday)){
                cDays += 0;
            } else if(!getSelectedTool().isHolidayCharge() && isLocalDateHoliday(getCheckoutDate().plusDays(day), monday, friday, saturday, sunday)) {
                cDays += 0;
            } else {
                cDays += 1;
            }
        }
        return cDays;
    }

    //====================================================================================
    //=============================== Getter/Setter Methods ==============================
    //====================================================================================
    public Tool getSelectedTool() {
        return this.selectedTool;
    }

    public int getRentDays() {
        return this.rentDays;
    }

    public byte getDiscount() {
        return this.discount;
    }

    public String getDiscountToString() {
        return String.format("%s%%", getDiscount());
    }

    public LocalDate getCheckoutDate() {
        return this.checkoutDate;
    }

    public String getCheckoutDateToString() {
        return checkoutDate.format(DateTimeFormatter.ofPattern("MM/dd/yy"));
    }

    public LocalDate getReturnDate() {
        return this.returnDate;
    }

    public String getReturnDateToString() {
        return returnDate.format(DateTimeFormatter.ofPattern("MM/dd/yy"));
    }

    public int getChargeDays() {
        return this.chargeDays;
    }

    public BigDecimal getPreDiscountCharge() {
        return this.preDiscountCharge;
    }

    public String getPreDiscountChargeToString() {
        return String.format("$%,.2f", getPreDiscountCharge());
    }

    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }

    public String getDiscountAmountToString() {
        return String.format("$%,.2f", getDiscountAmount());
    }

    public BigDecimal getFinalCharge() {
        return this.finalCharge;
    }

    public String getFinalChargeToString() {
        return String.format("$%,.2f", getFinalCharge());
    }

    public void setSelectedToolByType(String selectedToolType, LocalDate checkoutDate) throws Exception {
        // if today is before checkout date or after return date, just get tool details
        // else checkout the tool for rental
        if(LocalDate.now().isBefore(checkoutDate) || LocalDate.now().isAfter(this.returnDate)) {
            // Note: ToolsInventory already validates tool settings for errors
            this.selectedTool = ToolsInventory.getToolDetailsByType(selectedToolType);
        } else {
            // Note: ToolsInventory already validates tool settings for errors
            this.selectedTool = ToolsInventory.checkOutToolByType(selectedToolType);
        }
    }

    public void setSelectedToolByCode(String selectedToolByCode, LocalDate checkoutDate) throws Exception {
        // if today is before checkout date or after return date, just get tool details
        // else checkout the tool for rental
        if(LocalDate.now().isBefore(checkoutDate) || LocalDate.now().isAfter(this.returnDate)) {
            // Note: ToolsInventory already validates tool settings for errors
            this.selectedTool = ToolsInventory.getToolDetailsByCode(selectedToolByCode);
        } else {
            // Note: ToolsInventory already validates tool settings for errors
            this.selectedTool = ToolsInventory.checkOutToolByCode(selectedToolByCode);
        }
    }

    public void setRentDays(int rentDays) throws Exception {
        validateSetRentDays(rentDays);
        this.rentDays = rentDays;
    }

    public void setDiscount(byte discount) throws Exception {
        validateSetDiscount(discount);
        this.discount = discount;
    }

    public void setCheckoutDate(LocalDate date) {
        /* ToDo:
                I didn't see any requirements for dates prior to date of today and so I left validation for this out,
                but it is something I would recommend...
        */
        this.checkoutDate = date;
    }

    public void setReturnDate() throws Exception {
        validateSetReturnDate(); // check if checkoutDate and rentDays has been set first
        this.returnDate = calculateReturnDate(getRentDays());
    }

    public void setChargeDays() {
        this.chargeDays = calculateChargeDays();
    }

    public void setPreDiscountCharge() {
        this.preDiscountCharge = getSelectedTool().getPrice().multiply(new BigDecimal(getChargeDays())).setScale(2, RoundingMode.HALF_UP);;
    }

    public void setDiscountAmount() {
        this.discountAmount = calculateCurrencyPercentage(getPreDiscountCharge(), new BigDecimal(getDiscount())).setScale(2, RoundingMode.HALF_UP);
    }

    public void setFinalCharge() {
        this.finalCharge = getPreDiscountCharge().subtract(getDiscountAmount()).setScale(2, RoundingMode.HALF_UP);;
    }

    public void setFormDetailsByToolType(String selectedToolType, int days, byte discount, LocalDate checkoutDate) throws Exception {
        setRentDays(days);
        setDiscount(discount);
        setCheckoutDate(checkoutDate);
        setReturnDate();
        setSelectedToolByType(selectedToolType, checkoutDate); // must have returnDate set, and first gone through validations such as (days, discount, checkout)
        setChargeDays();
        setPreDiscountCharge();
        setDiscountAmount();
        setFinalCharge();
    }

    public void setFormDetailsByToolCode(String selectedToolCode, int days, byte discount, LocalDate checkoutDate) throws Exception {
        setRentDays(days);
        setDiscount(discount);
        setCheckoutDate(checkoutDate);
        setReturnDate();
        setSelectedToolByCode(selectedToolCode, checkoutDate); // must have returnDate set, and first gone through validations such as (days, discount, checkout)
        setChargeDays();
        setPreDiscountCharge();
        setDiscountAmount();
        setFinalCharge();
    }
}
