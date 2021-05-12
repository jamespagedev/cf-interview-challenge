package com.jp0421.userinterfaces;

import com.jp0421.checkout.RentForm;
import com.jp0421.tools.Tool;

import java.util.*;

public class Terminal implements UserInterface {
    //====================================================================================
    //================================== Helper Methods ==================================
    //====================================================================================
    private void printLineDivider (){
        System.out.println("====================================================");
    }
    private void printTool (Tool tool){
        System.out.println("    - " + tool.toString());
    }

    private void printToolsOfToolType (String toolType, HashMap<String, Tool> toolsByCode){
        ArrayList<Tool> tools = new ArrayList<>(toolsByCode.values());
        System.out.println("  - " + toolType + "'s");
        if (tools.isEmpty()){
            System.out.println("    - Not Found");
        } else {
            tools.forEach((tool) -> printTool(tool));
        }
    }

    private boolean isToolInHashTableHashMap (Hashtable<String, HashMap<String, Tool>> tools){
        boolean isToolFound = false;
        Iterator<Map.Entry<String, HashMap<String, Tool>>> pair = tools.entrySet().iterator();
        while (pair.hasNext() && !isToolFound){
            if (!pair.next().getValue().isEmpty()) {isToolFound = true;}
        }
        return isToolFound;
    }

    //====================================================================================
    //=============================== UserInterface Methods ==============================
    //====================================================================================
    @Override
    public void displayMessage(String msg){
        System.out.print(msg);
    }

    public void displayRentForm(RentForm rentForm) {
        printLineDivider();
        System.out.println("=============== Displaying Rent Form ===============");
        printLineDivider();
        System.out.println("  - Tool code: " + rentForm.getSelectedTool().getCode());
        System.out.println("  - Tool type: " + rentForm.getSelectedTool().getType());
        System.out.println("  - Tool brand: " + rentForm.getSelectedTool().getBrand());
        System.out.println("  - Rental days: " + rentForm.getRentDays());
        System.out.println("  - Check out date: " + rentForm.getCheckoutDateToString());
        System.out.println("  - Due date: " + rentForm.getReturnDateToString());
        System.out.println("  - Daily rental charge: " + rentForm.getSelectedTool().getPriceToString());
        System.out.println("  - Charge days: " + rentForm.getChargeDays());
        System.out.println("  - Pre-discount charge: " + rentForm.getPreDiscountChargeToString());
        System.out.println("  - Discount percent: " + rentForm.getDiscountToString());
        System.out.println("  - Discount amount: " + rentForm.getDiscountAmountToString());
        System.out.println("  - Final Charge: " + rentForm.getFinalChargeToString());
        printLineDivider();
        System.out.println();
    }

    @Override
    public void displayAllToolsByType(Hashtable<String, HashMap<String, Tool>> toolsByType) {
        printLineDivider();
        System.out.println("=========== Displaying All tools By Type ===========");
        printLineDivider();
        if(toolsByType.isEmpty() || !isToolInHashTableHashMap(toolsByType)){
            System.out.println("  - There are no tools in inventory");
        } else {
            toolsByType.forEach((toolType, toolsWithType) -> printToolsOfToolType(toolType, toolsWithType));
        }
        printLineDivider();
        System.out.println();
    }

    @Override
    public void displayAvailableToolsByType(Hashtable<String, HashMap<String, Tool>> toolsByType) {
        printLineDivider();
        System.out.println("======== Displaying Available tools By Type ========");
        printLineDivider();
        if(toolsByType.isEmpty() || !isToolInHashTableHashMap(toolsByType)){
            System.out.println("  - There are no tools available for renting");
        } else {
            toolsByType.forEach((toolType, toolsWithType) -> printToolsOfToolType(toolType, toolsWithType));
        }
        printLineDivider();
        System.out.println();
    }

    @Override
    public void displayRentedToolsByType(Hashtable<String, HashMap<String, Tool>> toolsByType) {
        printLineDivider();
        System.out.println("========== Displaying Rented tools By Type =========");
        printLineDivider();
        if(toolsByType.isEmpty() || !isToolInHashTableHashMap(toolsByType)){
            System.out.println("  - There are no tools being rented");
        } else {
            toolsByType.forEach(
                    (toolType, toolsWithType) -> printToolsOfToolType(toolType, toolsWithType)
            );
        }
        printLineDivider();
        System.out.println();
    }
}
