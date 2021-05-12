package com.jp0421.checkout;

import java.util.Hashtable;

/* Note:
    Normally a database should handle this,
    but to keep things simple we are using a class with static methods and variables.
*/
public class FormsInventory {
    private static int rentFormUniqueId = 1;
    private static final Hashtable<Integer, RentForm> rentFormsData = new Hashtable<>(); // rentFormUniqueId > RentForm

    //====================================================================================
    //================================== Constructor(s) ==================================
    //====================================================================================
    /* MyCodeStyle
        Some people prefer no constructor if it's not needed(and I'm cool with that),
        but if there are no team code standards for this,
        then I like to show the constructor is not doing anything on purpose.
    */
    public FormsInventory() {}

    //====================================================================================
    //================================= Accessor Methods =================================
    //====================================================================================
    public static void checkout(RentForm form){
        rentFormsData.put(rentFormUniqueId, form);
        rentFormUniqueId++;
    }

    //====================================================================================
    //=============================== Getter/Setter Methods ==============================
    //====================================================================================
    public static RentForm getRentForm(int formId) {
        return rentFormsData.get(formId);
    }
}
