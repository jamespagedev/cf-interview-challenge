package com.jp0421;

import com.jp0421.checkout.FormsInventory;
import com.jp0421.checkout.RentForm;
import com.jp0421.tools.ToolsInventory;
import com.jp0421.userinterfaces.Terminal;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StartTerminal {
    private final static Terminal ui = new Terminal();

    private static void addExampleToolsToToolsInventory() throws Exception {
        ui.displayAllToolsByType(ToolsInventory.getAllToolsByType()); // shows no tools have yet been added to inventory
        ui.displayAvailableToolsByType(ToolsInventory.getToolsAvailableByType()); // shows no tools have yet been added to available
        ui.displayRentedToolsByType(ToolsInventory.getToolsRentedByType()); // shows no tools have yet been added to what is rented out
        ui.displayMessage("Adding tools to inventory...\n");
        ToolsInventory.addNewToolByDetails("Ladder", "Werner", "LADW", new BigDecimal("1.99"), true, true, false);
        ToolsInventory.addNewToolByDetails("Chainsaw", "Stihl", "CHNS", new BigDecimal("1.49"), true, false, true);
        ToolsInventory.addNewToolByDetails("Jackhammer", "Ridgid", "JAKR", new BigDecimal("2.99"), true, false, false);
        ToolsInventory.addNewToolByDetails("Jackhammer", "DeWalt", "JAKD", new BigDecimal("2.99"), true, false, false);
        ui.displayAllToolsByType(ToolsInventory.getAllToolsByType()); // notice 4 tools shown
        ui.displayAvailableToolsByType(ToolsInventory.getToolsAvailableByType()); // notice 4 tools shown
        ui.displayRentedToolsByType(ToolsInventory.getToolsRentedByType()); // notice 0 tools still showing
    }

    private static RentForm checkoutFormByToolType(String selectedToolType, int rentDays, byte discount, LocalDate checkoutDate) throws Exception {
        ui.displayMessage("Filling checkout form...\n");
        RentForm rentForm = new RentForm();
        rentForm.setFormDetailsByToolType(selectedToolType, rentDays, discount, checkoutDate);

        // shows tool removed from inventory during checkout to avoid multi-user-access logic error
        ui.displayAllToolsByType(ToolsInventory.getAllToolsByType()); // notice 4 tools still showing
        ui.displayAvailableToolsByType(ToolsInventory.getToolsAvailableByType()); // notice 3 tools shown
        ui.displayRentedToolsByType(ToolsInventory.getToolsRentedByType()); // notice 1 tools shown (selected for rental)

        // shows form details
        ui.displayRentForm(rentForm);
        return rentForm;
    }

    private static RentForm checkoutFormByToolCode(String selectedToolCode, int rentDays, byte discount, LocalDate checkoutDate) throws Exception {
        ui.displayMessage("Filling checkout form...\n");
        RentForm rentForm = new RentForm();
        rentForm.setFormDetailsByToolCode(selectedToolCode, rentDays, discount, checkoutDate);

        // shows tool removed from inventory during checkout to avoid multi-user-access logic error
        ui.displayAllToolsByType(ToolsInventory.getAllToolsByType()); // notice 4 tools still showing
        ui.displayAvailableToolsByType(ToolsInventory.getToolsAvailableByType()); // notice 3 tools shown
        ui.displayRentedToolsByType(ToolsInventory.getToolsRentedByType()); // notice 1 tools shown (selected for rental)

        // shows form details
        ui.displayRentForm(rentForm);
        return rentForm;
    }

    private static RentForm checkoutTool(RentForm rentForm) {
        /* Note:
                    I'm leaving out user input with this UI as a UI is not part of the project,
                    and as such I don't have to spend a lot of time worrying about dealing with user level errors.
                    When a confirmation is added though, and the user decides to cancel...
                    don't forget to check the tool back into the available inventory.
             */
        boolean checkoutConfirm = true;
        ui.displayMessage("Are you sure you want to checkout this tool? ..." + (checkoutConfirm ? "yes" : "no") + "\n");
        if(checkoutConfirm){ // confirm checkout
            FormsInventory.checkout(rentForm);
        } else { // cancel checkout
            ToolsInventory.returnToolToAvailableFromRented(rentForm.getSelectedTool().getCode());
            rentForm = null;
        }
        ui.displayRentedToolsByType(ToolsInventory.getToolsRentedByType()); // show status of tools rented
        return rentForm;
    }

    private static void returnTool(RentForm rentForm) {
        // first check if tool exists and if it wasn't already returned
        ui.displayMessage("Returning tool to ToolsInventory Available\n");
        if(rentForm != null && !ToolsInventory.getToolsAvailableByCode().containsKey(rentForm.getSelectedTool().getCode())) {
            ToolsInventory.returnToolToAvailableFromRented(rentForm.getSelectedTool().getCode());
        }
        ui.displayRentedToolsByType(ToolsInventory.getToolsRentedByType()); // show status of tools rented
    }

    public static void main(String[] args) {
        /* High Level POS Checkout Process
            - After system starts, display all tools showing the ToolsInventory(unless getting from database) is empty
            - Add tools into ToolsInventory
              - Note: ToolsInventory should be later switched with a database to save the data
            - Display all tools showing the tools have been added to the ToolsInventory for both All and Available
              - Note: Rented should be empty because none have been checked out yet.
            - Fill out checkout form
              - Note: During this process the tool selected gets moved from ToolsInventory Available to Rented
                      to avoid multiple users trying to checkout the same tool on different forms.
                      If the form gets canceled, then the tool should be returned from ToolsInventory Rented to Available
            - Display updates for Available and Rented in ToolsInventory
            - Display rent form details
            - Checkout the tool
              - Use checkoutConfirm true/false to confirm/cancel the form/tool checkout process
                - Note: because this project does not require a UI, this UI is a stretch feature,
                        but does not include user input to avoid further handling requirements of user errors
                - If confirmed assign a uniqueId to the form and add it in the FormsInventory Data
                  - Use display form pulling the form from the FormsInventory Data to show it has been added
            - return the tool from ToolsInventory Rented to Available
         */
        try {
            // add tools to toolsInventory
            addExampleToolsToToolsInventory();

            // fill out checkout form
            RentForm rentForm = checkoutFormByToolCode("JAKD", 9, (byte) 0, LocalDate.of(2021, 5, 3));

            // checkout tool
            rentForm = checkoutTool(rentForm);

            // return tool (only if tool was checked out)
            returnTool(rentForm);

            /* (Optional) Feel free to play around with checkout/return forms/tools here. Just follow these steps as shown above...
                - (Optional) Add more new tools to ToolsInventory
                - Fill out checkout form
                - Checkout tool
                - Return tool
             */

        } catch (Exception e) {
            ui.displayMessage(e.toString());
        }
    }
}
