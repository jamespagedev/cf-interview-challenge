import com.jp0421.checkout.FormsInventory;
import com.jp0421.checkout.RentForm;
import com.jp0421.tools.ToolsInventory;
import com.jp0421.userinterfaces.Terminal;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*; // use if 3 or more imports from Assert package

import java.math.BigDecimal;
import java.time.LocalDate;

public class StartTerminalTest {
    //====================================================================================
    //===================================== Variables ====================================
    //====================================================================================
    private final static Terminal ui = new Terminal();
    private RentForm rentForm = new RentForm();

    //====================================================================================
    //==================================== Test Setups ===================================
    //====================================================================================
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @BeforeClass
    public static void initializeToolsInventory() {
        try {
            ToolsInventory.addNewToolByDetails("Ladder", "Werner", "LADW", new BigDecimal("1.99"), true, true, false);
            ToolsInventory.addNewToolByDetails("Chainsaw", "Stihl", "CHNS", new BigDecimal("1.49"), true, false, true);
            ToolsInventory.addNewToolByDetails("Jackhammer", "Ridgid", "JAKR", new BigDecimal("2.99"), true, false, false);
            ToolsInventory.addNewToolByDetails("Jackhammer", "DeWalt", "JAKD", new BigDecimal("2.99"), true, false, false);
        } catch (Exception e) {
            ui.displayMessage(e.toString());
        }
    }

    @After
    public void testsCleanup() { rentForm = new RentForm(); }

    //====================================================================================
    //======================================= Tests ======================================
    //====================================================================================
    /* MVP Tests
        - Test 1 -> Tool code: "JAKR", Checkout date: "09/03/15", Rental days: 5, Discount: "101%"
            - Discount should raise exception, only 0-100 is allowed
        - Test 2 -> Tool code: "LADW", Checkout date: "07/02/20", Rental days: 3, Discount: "10%"
        - Test 3 -> Tool code: "CHNS", Checkout date: "07/02/15", Rental days: 5, Discount: "25%"
        - Test 4 -> Tool code: "JAKD", Checkout date: "09/03/15", Rental days: 6, Discount: "0%"
        - Test 5 -> Tool code: "JAKR", Checkout date: "07/02/15", Rental days: 9, Discount: "0%"
        - Test 6 -> Tool code: "JAKR", Checkout date: "07/02/20", Rental days: 4, Discount: "50%"
     */
    @Test
    public void mvpTestOne() throws Exception {
        // Tool("Jackhammer", "Ridgid", "JAKR", 2.99, true, false, false);
        // Test 1 -> Tool code: "JAKR", Checkout date: "09/03/15", Rental days: 5, Discount: "101%"
        exceptionRule.expect(Exception.class);
        exceptionRule.expectMessage("Invalid Discount(101%), must be between 0-100%");
        rentForm.setFormDetailsByToolCode("JAKR", 5, (byte) 101, LocalDate.of(2015, 9, 3));
    }

    @Test
    public void mvpTestTwo() throws Exception {
        // Tool("Ladder", "Werner", "LADW", 1.99, true, true, false);
        // Test 2 -> Tool code: "LADW", Checkout date: "07/02/20", Rental days: 3, Discount: "10%"
        rentForm.setFormDetailsByToolCode("LADW", 3, (byte) 10, LocalDate.of(2020, 7, 2));
        ui.displayMessage("mvpTestTwo output...\n");
        ui.displayRentForm(rentForm);
        assertEquals("LADW", rentForm.getSelectedTool().getCode());
        assertEquals("Ladder", rentForm.getSelectedTool().getType());
        assertEquals("Werner", rentForm.getSelectedTool().getBrand());
        assertEquals(3, rentForm.getRentDays());
        assertEquals(LocalDate.of(2020, 7, 2), rentForm.getCheckoutDate());
        assertEquals("07/02/20", rentForm.getCheckoutDateToString());
        assertEquals(LocalDate.of(2020, 7, 2).plusDays(3), rentForm.getReturnDate());
        assertEquals("07/05/20", rentForm.getReturnDateToString());
        assertEquals(new BigDecimal("1.99"), rentForm.getSelectedTool().getPrice());
        assertEquals("$1.99", rentForm.getSelectedTool().getPriceToString());
        assertEquals(2, rentForm.getChargeDays());
        assertEquals(new BigDecimal("3.98"), rentForm.getPreDiscountCharge());
        assertEquals("$3.98", rentForm.getPreDiscountChargeToString());
        assertEquals(10, rentForm.getDiscount());
        assertEquals("10%", rentForm.getDiscountToString());
        assertEquals(new BigDecimal("0.40"), rentForm.getDiscountAmount());
        assertEquals("$0.40", rentForm.getDiscountAmountToString());
        assertEquals(new BigDecimal("3.58"), rentForm.getFinalCharge());
        assertEquals("$3.58", rentForm.getFinalChargeToString());
        assertTrue(rentForm.getSelectedTool().isWeekdayCharge());
        assertTrue(rentForm.getSelectedTool().isWeekendCharge());
        assertFalse(rentForm.getSelectedTool().isHolidayCharge());

        // Add form to forms inventory
        FormsInventory.checkout(rentForm);

        // return tool if it was checked out
        if(!ToolsInventory.getToolsAvailableByCode().containsKey(rentForm.getSelectedTool().getCode())) {
            ToolsInventory.returnToolToAvailableFromRented(rentForm.getSelectedTool().getCode());
        }
    }

    @Test
    public void mvpTestThree() throws Exception {
        // Tool("Chainsaw", "Stihl", "CHNS", 1.49, true, false, true);
        // Test 3 -> Tool code: "CHNS", Checkout date: "07/02/15", Rental days: 5, Discount: "25%"
        rentForm.setFormDetailsByToolCode("CHNS", 5, (byte) 25, LocalDate.of(2015, 7, 2));
        ui.displayMessage("mvpTestThree output...\n");
        ui.displayRentForm(rentForm);
        assertEquals("CHNS", rentForm.getSelectedTool().getCode());
        assertEquals("Chainsaw", rentForm.getSelectedTool().getType());
        assertEquals("Stihl", rentForm.getSelectedTool().getBrand());
        assertEquals(5, rentForm.getRentDays());
        assertEquals(LocalDate.of(2015, 7, 2), rentForm.getCheckoutDate());
        assertEquals("07/02/15", rentForm.getCheckoutDateToString());
        assertEquals(LocalDate.of(2015, 7, 2).plusDays(5), rentForm.getReturnDate());
        assertEquals("07/07/15", rentForm.getReturnDateToString());
        assertEquals(new BigDecimal("1.49"), rentForm.getSelectedTool().getPrice());
        assertEquals("$1.49", rentForm.getSelectedTool().getPriceToString());
        assertEquals(3, rentForm.getChargeDays());
        assertEquals(new BigDecimal("4.47"), rentForm.getPreDiscountCharge());
        assertEquals("$4.47", rentForm.getPreDiscountChargeToString());
        assertEquals(25, rentForm.getDiscount());
        assertEquals("25%", rentForm.getDiscountToString());
        assertEquals(new BigDecimal("1.12"), rentForm.getDiscountAmount());
        assertEquals("$1.12", rentForm.getDiscountAmountToString());
        assertEquals(new BigDecimal("3.35"), rentForm.getFinalCharge());
        assertEquals("$3.35", rentForm.getFinalChargeToString());
        assertTrue(rentForm.getSelectedTool().isWeekdayCharge());
        assertFalse(rentForm.getSelectedTool().isWeekendCharge());
        assertTrue(rentForm.getSelectedTool().isHolidayCharge());

        // Add form to forms inventory
        FormsInventory.checkout(rentForm);

        // return tool if it was checked out
        if(!ToolsInventory.getToolsAvailableByCode().containsKey(rentForm.getSelectedTool().getCode())) {
            ToolsInventory.returnToolToAvailableFromRented(rentForm.getSelectedTool().getCode());
        }
    }

    @Test
    public void mvpTestFour() throws Exception {
        // Tool("Jackhammer", "DeWalt", "JAKD", 2.99, true, false, false);
        // Test 4 -> Tool code: "JAKD", Checkout date: "09/03/15", Rental days: 6, Discount: "0%"
        rentForm.setFormDetailsByToolCode("JAKD", 6, (byte) 0, LocalDate.of(2015, 9, 3));
        ui.displayMessage("mvpTestFour output...\n");
        ui.displayRentForm(rentForm);
        assertEquals("JAKD", rentForm.getSelectedTool().getCode());
        assertEquals("Jackhammer", rentForm.getSelectedTool().getType());
        assertEquals("DeWalt", rentForm.getSelectedTool().getBrand());
        assertEquals(6, rentForm.getRentDays());
        assertEquals(LocalDate.of(2015, 9, 3), rentForm.getCheckoutDate());
        assertEquals("09/03/15", rentForm.getCheckoutDateToString());
        assertEquals(LocalDate.of(2015, 9, 3).plusDays(6), rentForm.getReturnDate());
        assertEquals("09/09/15", rentForm.getReturnDateToString());
        assertEquals(new BigDecimal("2.99"), rentForm.getSelectedTool().getPrice());
        assertEquals("$2.99", rentForm.getSelectedTool().getPriceToString());
        assertEquals(3, rentForm.getChargeDays());
        assertEquals(new BigDecimal("8.97"), rentForm.getPreDiscountCharge());
        assertEquals("$8.97", rentForm.getPreDiscountChargeToString());
        assertEquals(0, rentForm.getDiscount());
        assertEquals("0%", rentForm.getDiscountToString());
        assertEquals(new BigDecimal("0.00"), rentForm.getDiscountAmount());
        assertEquals("$0.00", rentForm.getDiscountAmountToString());
        assertEquals(new BigDecimal("8.97"), rentForm.getFinalCharge());
        assertEquals("$8.97", rentForm.getFinalChargeToString());
        assertTrue(rentForm.getSelectedTool().isWeekdayCharge());
        assertFalse(rentForm.getSelectedTool().isWeekendCharge());
        assertFalse(rentForm.getSelectedTool().isHolidayCharge());

        // Add form to forms inventory
        FormsInventory.checkout(rentForm);

        // return tool if it was checked out
        if(!ToolsInventory.getToolsAvailableByCode().containsKey(rentForm.getSelectedTool().getCode())) {
            ToolsInventory.returnToolToAvailableFromRented(rentForm.getSelectedTool().getCode());
        }
    }

    @Test
    public void mvpTestFive() throws Exception {
        // Tool("Jackhammer", "Ridgid", "JAKR", 2.99, true, false, false);
        // Test 5 -> Tool code: "JAKR", Checkout date: "07/02/15", Rental days: 9, Discount: "0%"
        rentForm.setFormDetailsByToolCode("JAKR", 9, (byte) 0, LocalDate.of(2015, 7, 2));
        ui.displayMessage("mvpTestFive output...\n");
        ui.displayRentForm(rentForm);
        assertEquals("JAKR", rentForm.getSelectedTool().getCode());
        assertEquals("Jackhammer", rentForm.getSelectedTool().getType());
        assertEquals("Ridgid", rentForm.getSelectedTool().getBrand());
        assertEquals(9, rentForm.getRentDays());
        assertEquals(LocalDate.of(2015, 7, 2), rentForm.getCheckoutDate());
        assertEquals("07/02/15", rentForm.getCheckoutDateToString());
        assertEquals(LocalDate.of(2015, 7, 2).plusDays(9), rentForm.getReturnDate());
        assertEquals("07/11/15", rentForm.getReturnDateToString());
        assertEquals(new BigDecimal("2.99"), rentForm.getSelectedTool().getPrice());
        assertEquals("$2.99", rentForm.getSelectedTool().getPriceToString());
        assertEquals(5, rentForm.getChargeDays());
        assertEquals(new BigDecimal("14.95"), rentForm.getPreDiscountCharge());
        assertEquals("$14.95", rentForm.getPreDiscountChargeToString());
        assertEquals(0, rentForm.getDiscount());
        assertEquals("0%", rentForm.getDiscountToString());
        assertEquals(new BigDecimal("0.00"), rentForm.getDiscountAmount());
        assertEquals("$0.00", rentForm.getDiscountAmountToString());
        assertEquals(new BigDecimal("14.95"), rentForm.getFinalCharge());
        assertEquals("$14.95", rentForm.getFinalChargeToString());
        assertTrue(rentForm.getSelectedTool().isWeekdayCharge());
        assertFalse(rentForm.getSelectedTool().isWeekendCharge());
        assertFalse(rentForm.getSelectedTool().isHolidayCharge());

        // Add form to forms inventory
        FormsInventory.checkout(rentForm);

        // return tool if it was checked out
        if(!ToolsInventory.getToolsAvailableByCode().containsKey(rentForm.getSelectedTool().getCode())) {
            ToolsInventory.returnToolToAvailableFromRented(rentForm.getSelectedTool().getCode());
        }
    }

    @Test
    public void mvpTestSix() throws Exception {
        // Tool("Jackhammer", "Ridgid", "JAKR", 2.99, true, false, false);
        // Test 6 -> Tool code: "JAKR", Checkout date: "07/02/20", Rental days: 4, Discount: "50%"
        rentForm.setFormDetailsByToolCode("JAKR", 4, (byte) 50, LocalDate.of(2020, 7, 2));
        ui.displayMessage("mvpTestSix output...\n");
        ui.displayRentForm(rentForm);
        assertEquals("JAKR", rentForm.getSelectedTool().getCode());
        assertEquals("Jackhammer", rentForm.getSelectedTool().getType());
        assertEquals("Ridgid", rentForm.getSelectedTool().getBrand());
        assertEquals(4, rentForm.getRentDays());
        assertEquals(LocalDate.of(2020, 7, 2), rentForm.getCheckoutDate());
        assertEquals("07/02/20", rentForm.getCheckoutDateToString());
        assertEquals(LocalDate.of(2020, 7, 2).plusDays(4), rentForm.getReturnDate());
        assertEquals("07/06/20", rentForm.getReturnDateToString());
        assertEquals(new BigDecimal("2.99"), rentForm.getSelectedTool().getPrice());
        assertEquals("$2.99", rentForm.getSelectedTool().getPriceToString());
        assertEquals(1, rentForm.getChargeDays());
        assertEquals(new BigDecimal("2.99"), rentForm.getPreDiscountCharge());
        assertEquals("$2.99", rentForm.getPreDiscountChargeToString());
        assertEquals(50, rentForm.getDiscount());
        assertEquals("50%", rentForm.getDiscountToString());
        assertEquals(new BigDecimal("1.50"), rentForm.getDiscountAmount());
        assertEquals("$1.50", rentForm.getDiscountAmountToString());
        assertEquals(new BigDecimal("1.49"), rentForm.getFinalCharge());
        assertEquals("$1.49", rentForm.getFinalChargeToString());
        assertTrue(rentForm.getSelectedTool().isWeekdayCharge());
        assertFalse(rentForm.getSelectedTool().isWeekendCharge());
        assertFalse(rentForm.getSelectedTool().isHolidayCharge());

        // Add form to forms inventory
        FormsInventory.checkout(rentForm);

        // return tool if it was checked out
        if(!ToolsInventory.getToolsAvailableByCode().containsKey(rentForm.getSelectedTool().getCode())) {
            ToolsInventory.returnToolToAvailableFromRented(rentForm.getSelectedTool().getCode());
        }
    }

    @Test
    public void currencyNumberWithComma() throws Exception {
        // Tool("Jackhammer", "DeWalt", "JAKD", 2.99, true, false, false);
        rentForm.setFormDetailsByToolCode("JAKD", 1283, (byte) 10, LocalDate.of(2015, 7, 2));
        ui.displayMessage("currencyNumberWithComma output...\n");
        ui.displayRentForm(rentForm);
        assertEquals("JAKD", rentForm.getSelectedTool().getCode());
        assertEquals("Jackhammer", rentForm.getSelectedTool().getType());
        assertEquals("DeWalt", rentForm.getSelectedTool().getBrand());
        assertEquals(1283, rentForm.getRentDays());
        assertEquals(LocalDate.of(2015, 7, 2), rentForm.getCheckoutDate());
        assertEquals("07/02/15", rentForm.getCheckoutDateToString());
        assertEquals(LocalDate.of(2015, 7, 2).plusDays(1283), rentForm.getReturnDate());
        assertEquals("01/05/19", rentForm.getReturnDateToString());
        assertEquals(new BigDecimal("2.99"), rentForm.getSelectedTool().getPrice());
        assertEquals("$2.99", rentForm.getSelectedTool().getPriceToString());
        assertEquals(908, rentForm.getChargeDays());
        assertEquals(new BigDecimal("2714.92"), rentForm.getPreDiscountCharge());
        assertEquals("$2,714.92", rentForm.getPreDiscountChargeToString());
        assertEquals(10, rentForm.getDiscount());
        assertEquals("10%", rentForm.getDiscountToString());
        assertEquals(new BigDecimal("271.49"), rentForm.getDiscountAmount());
        assertEquals("$271.49", rentForm.getDiscountAmountToString());
        assertEquals(new BigDecimal("2443.43"), rentForm.getFinalCharge());
        assertEquals("$2,443.43", rentForm.getFinalChargeToString());
        assertTrue(rentForm.getSelectedTool().isWeekdayCharge());
        assertFalse(rentForm.getSelectedTool().isWeekendCharge());
        assertFalse(rentForm.getSelectedTool().isHolidayCharge());

        // Add form to forms inventory
        FormsInventory.checkout(rentForm);

        // return tool if it was checked out
        if(!ToolsInventory.getToolsAvailableByCode().containsKey(rentForm.getSelectedTool().getCode())) {
            ToolsInventory.returnToolToAvailableFromRented(rentForm.getSelectedTool().getCode());
        }
    }

    @Test
    public void rentDaysZeroException() throws Exception {
        // Tool("Chainsaw", "Stihl", "CHNS", 1.49, true, false, true);
        exceptionRule.expect(Exception.class);
        exceptionRule.expectMessage("Invalid Days(0), must be at least 1 or greater");
        rentForm.setFormDetailsByToolCode("CHNS", 0, (byte) 15, LocalDate.of(2015, 9, 3));
    }
}
