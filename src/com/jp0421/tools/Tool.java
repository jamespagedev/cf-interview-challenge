package com.jp0421.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Tool {
    private final String type;
    private final String brand;
    private final String code;
    private final BigDecimal price;
    private final boolean isWeekdayCharge;
    private final boolean isWeekendCharge;
    private final boolean isHolidayCharge;


    //====================================================================================
    //================================== Constructor(s) ==================================
    //====================================================================================
    public Tool(String type, String brand, String code, BigDecimal price, boolean isWeekdayCharge, boolean isWeekendCharge, boolean isHolidayCharge) {
        this.type = type;
        this.brand = brand;
        this.code = code;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.isWeekdayCharge = isWeekdayCharge;
        this.isWeekendCharge = isWeekendCharge;
        this.isHolidayCharge = isHolidayCharge;
    }

    //====================================================================================
    //================================= Accessor Methods =================================
    //====================================================================================
    @Override
    public String toString() {
        return String.format(
                "%s(Tool Brand: %s, Tool Code: %s, Price: $%,.2f/day, Weekday Charge: %s, Weekend Charge: %s, Holiday Charge: %s)",
                getType(),
                getBrand(),
                getCode(),
                getPrice(),
                isWeekdayCharge() ? "yes" : "no",
                isWeekendCharge() ? "yes" : "no",
                isHolidayCharge() ? "yes" : "no"
        );
    }

    //====================================================================================
    //=============================== Getter/Setter Methods ==============================
    //====================================================================================
    public String getType() {
        return this.type;
    }

    public String getBrand() {
        return this.brand;
    }

    public String getCode() {
        return this.code;
    }

    public BigDecimal getPrice() {
        return this.price;
    }
    public String getPriceToString() {
        return String.format("$%,.2f", this.price);
    }

    //====================================================================================
    //================================== Is/Has Methods ==================================
    //====================================================================================
    public boolean isWeekdayCharge() {
        return this.isWeekdayCharge;
    }

    public boolean isWeekendCharge() {
        return this.isWeekendCharge;
    }

    public boolean isHolidayCharge() {
        return this.isHolidayCharge;
    }
}
