package com.jp0421.tools;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Hashtable;

/* Note:
    Normally a database should handle this,
    but to keep things simple we are using a class with static methods and variables.
*/
public class ToolsInventory {
    /* Class Definition
        This class is used as a database placeholder for
        managing an inventory of what tools exists and
        keeping track of their locations
    */

    //====================================================================================
    //===================================== Variables ====================================
    //====================================================================================
    /*
        allTools* == tools owned by company
        toolsAvailable* == tools currently available for renting
        toolsRented* == keeps track of what tools are rented out
        note: Yes we are using 2 dimensional multiple hash tables to store the same tool.
              ...sorry, but this provides o(1) runtime operations in place of not having a database.
              Hashtable<toolProperty, HashMap<toolCode, Tool>>
    */
    private static final Hashtable<String, HashMap<String, Tool>> allToolsByType = new Hashtable<>(); // tool type > code > tool
    private static final Hashtable<String, HashMap<String, Tool>> toolsAvailableByType = new Hashtable<>(); // tool type > code > tool
    private static final Hashtable<String, HashMap<String, Tool>> toolsRentedByType = new Hashtable<>(); // tool type > code > tool
    private static final Hashtable<String, HashMap<String, Tool>> allToolsByBrand = new Hashtable<>(); // tool brand > code > tool
    private static final Hashtable<String, HashMap<String, Tool>> toolsAvailableByBrand = new Hashtable<>(); // tool brand > code > tool
    private static final Hashtable<String, HashMap<String, Tool>> toolsRentedByBrand = new Hashtable<>(); // tool brand > code > tool
    private static final Hashtable<String, Tool> allToolsByCode = new Hashtable<>(); // tool code >  tool
    private static final Hashtable<String, Tool> toolsAvailableByCode = new Hashtable<>(); // tool code >  tool
    private static final Hashtable<String, Tool> toolsRentedByCode = new Hashtable<>(); // tool code >  tool

    //====================================================================================
    //================================== Constructor(s) ==================================
    //====================================================================================
    /* MyCodeStyle
        Some people prefer no constructor if it's not needed(and I'm cool with that),
        but if there are no team code standards for this,
        then I like to show the constructor is not doing anything on purpose.
    */
    public ToolsInventory() {}

    //====================================================================================
    //================================== Helper Methods ==================================
    //====================================================================================
    private static void addToolToHashtableHashMapByKey(Hashtable<String, HashMap<String, Tool>> toolsInventory, String key, Tool tool) {
        if(!toolsInventory.containsKey(key)) { // add new HashMap if key doesn't exist
            toolsInventory.put(key, new HashMap<>());
        }
        toolsInventory.get(key).put(tool.getCode(), tool);
    }

    private static void addToolToHashtableByKey(Hashtable<String, Tool> toolsInventory, String key, Tool tool) {
        if(!toolsInventory.containsKey(key)) { // only add key/value pair if key doesn't exist
            toolsInventory.put(key, tool);
        }
    }

    private static Tool removeToolByTypeFromAvailableHashtables(String type, String code){
        Tool selectedToolByType = toolsAvailableByType.get(type).remove(code);
        toolsAvailableByBrand.get(selectedToolByType.getBrand()).remove(selectedToolByType.getCode());
        toolsAvailableByCode.remove(selectedToolByType.getCode());
        return selectedToolByType;
    }

    private static Tool removeToolByCodeFromAvailableHashtables(String code){
        Tool selectedToolByType = toolsAvailableByCode.remove(code);
        toolsAvailableByBrand.get(selectedToolByType.getBrand()).remove(selectedToolByType.getCode());
        toolsAvailableByType.get(selectedToolByType.getType()).remove(selectedToolByType.getCode());
        return selectedToolByType;
    }

    private static void addToolToAvailableHashtables(Tool tool){
        toolsAvailableByType.get(tool.getType()).put(tool.getCode(), tool);
        toolsAvailableByBrand.get(tool.getBrand()).put(tool.getCode(), tool);
        toolsAvailableByCode.put(tool.getCode(), tool);
    }

    private static void addToolToRented(Tool tool){
        toolsRentedByType.get(tool.getType()).put(tool.getCode(), tool);
        toolsRentedByBrand.get(tool.getBrand()).put(tool.getCode(), tool);
        toolsRentedByCode.put(tool.getCode(), tool);
    }

    private static Tool removeToolToRented(String toolCode){
        Tool selectedTool = toolsRentedByCode.remove(toolCode);
        toolsRentedByType.get(selectedTool.getType()).remove(toolCode);
        toolsRentedByBrand.get(selectedTool.getBrand()).remove(toolCode);
        return selectedTool;
    }

    private static void updateAllToolsHashTablesWithNewTool(Tool tool) throws Exception {
        // check for errors
        validateAddNewTool(tool);

        // update all tables (note: if this was a database we could use normalized relation tables instead...)
        // by type
        addToolToHashtableHashMapByKey(allToolsByType, tool.getType(), tool);
        addToolToHashtableHashMapByKey(toolsAvailableByType, tool.getType(), tool);
        if (!toolsRentedByType.containsKey(tool.getType())) {toolsRentedByType.put(tool.getType(), new HashMap<>());}

        // by brand
        addToolToHashtableHashMapByKey(allToolsByBrand, tool.getBrand(), tool);
        addToolToHashtableHashMapByKey(toolsAvailableByBrand, tool.getBrand(), tool);
        if (!toolsRentedByBrand.containsKey(tool.getType())) {toolsRentedByBrand.put(tool.getBrand(), new HashMap<>());}

        // by code
        addToolToHashtableByKey(allToolsByCode, tool.getCode(), tool);
        addToolToHashtableByKey(toolsAvailableByCode, tool.getCode(), tool);
    }

    //====================================================================================
    //================================== Support Methods =================================
    //====================================================================================
    private static void validateAddNewTool(Tool tool) throws Exception {
        // handle logic errors
        if(tool.getType().isEmpty()) {
            throw new Exception("Tool type required.");
        } else if(tool.getBrand().isEmpty()) {
            throw new Exception("Tool brand required.");
        } else if(tool.getCode().isEmpty()) {
            throw new Exception("Tool code required.");
        } else if(tool.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Invalid tool price. (" + tool.getPriceToString() + ")");
        }
    }

    private static void validateCheckoutToolByType(String type) throws Exception {
        // handle logic errors
        if(!toolsAvailableByType.containsKey(type)) {
            throw new Exception("No \"" + type + "\" tools exist.");
        } else if(toolsAvailableByType.get(type).isEmpty()) {
            throw new Exception("No available \"" + type + "\" tools.");
        }
    }

    private static void validateCheckoutToolByCode(String code) throws Exception {
        // handle logic errors
        if(!allToolsByCode.containsKey(code)) {
            throw new Exception("No tool with code(\"" + code + "\") exists in the system.");
        } else if(!toolsAvailableByCode.containsKey(code)) {
            throw new Exception("No tool with code(\"" + code + "\") is currently available for renting.");
        }
    }

    //====================================================================================
    //================================= Accessor Methods =================================
    //====================================================================================
    public static void addNewToolByTool(Tool tool) throws Exception {
        updateAllToolsHashTablesWithNewTool(tool);
    }

    public static void addNewToolByDetails(String type, String brand, String code, BigDecimal price, boolean hasWeekdayCharge, boolean hasWeekendCharge, boolean hasHolidayCharge) throws Exception {
        // create new tool from properties
        Tool tool = new Tool(type, brand, code, price, hasWeekdayCharge, hasWeekendCharge, hasHolidayCharge);

        // update all tables (note: if this was a database we could use normalized relation tables instead...)
        updateAllToolsHashTablesWithNewTool(tool);
    }

    public static Tool checkOutToolByType(String type) throws Exception {
        // check for errors
        validateCheckoutToolByType(type);

        // remove tool from available hashtables
        String toolCodeFound = toolsAvailableByType.get(type).entrySet().iterator().next().getKey();
        Tool selectedTool = removeToolByTypeFromAvailableHashtables(type, toolCodeFound);

        // add tool to the rented hashtable
        addToolToRented(selectedTool);
        return selectedTool;
    }

    public static Tool checkOutToolByCode(String code) throws Exception {
        // check for errors
        validateCheckoutToolByCode(code);

        // remove tool from available hashtables
        Tool selectedTool = removeToolByCodeFromAvailableHashtables(code);

        // add tool to the rented hashtable
        addToolToRented(selectedTool);
        return selectedTool;
    }

    public static Tool getToolDetailsByType(String type) throws Exception {
        // check for errors
        validateCheckoutToolByType(type);

        String toolCodeFound = allToolsByType.get(type).entrySet().iterator().next().getKey();
        return allToolsByType.get(type).get(toolCodeFound);
    }

    public static Tool getToolDetailsByCode(String code) throws Exception {
        // check for errors
        validateCheckoutToolByCode(code);

        return allToolsByCode.get(code);
    }

    public static void returnToolToAvailableFromRented(String toolCode) {
        // remove tool from rented hash tables
        Tool selectedTool = removeToolToRented(toolCode);

        // add tool to available hash tables
        addToolToAvailableHashtables(selectedTool);
    }

    //====================================================================================
    //=============================== Getter/Setter Methods ==============================
    //====================================================================================
    public static Hashtable<String, HashMap<String, Tool>> getAllToolsByType(){
        return allToolsByType;
    }

    public static Hashtable<String, HashMap<String, Tool>> getToolsAvailableByType(){
        return toolsAvailableByType;
    }

    public static Hashtable<String, Tool> getToolsAvailableByCode(){
        return toolsAvailableByCode;
    }

    public static Hashtable<String, HashMap<String, Tool>> getToolsRentedByType(){
        return toolsRentedByType;
    }
}
