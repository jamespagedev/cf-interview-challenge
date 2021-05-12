package com.jp0421.userinterfaces;

import com.jp0421.checkout.RentForm;
import com.jp0421.tools.Tool;

import java.util.HashMap;
import java.util.Hashtable;

public interface UserInterface {
    void displayMessage(String msg);
    void displayRentForm(RentForm rentForm);
    void displayAllToolsByType(Hashtable<String, HashMap<String, Tool>> toolsByType);
    void displayAvailableToolsByType(Hashtable<String, HashMap<String, Tool>> toolsByType);
    void displayRentedToolsByType(Hashtable<String, HashMap<String, Tool>> toolsByType);
}
