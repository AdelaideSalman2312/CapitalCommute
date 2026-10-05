package com.CapitalCommute.commute.util;

public class SessionManager {
    
    private static String currentRole;
    private static String currentUserId;
    private static String currentNationalId;
    private static String currentEmail;
    
    
    public static void setSession(String role, String userId, String nationalId) {
        currentRole = role;
        currentUserId = userId;
        currentNationalId = nationalId;

    }
    
    public static void clearSession() {
        currentRole = null;
        currentUserId = null;
        currentNationalId = null;
        currentEmail = null;
    }
    
    public static boolean isLoggedIn() {
        return currentRole != null && currentUserId != null;
    }
    
    public static String getCurrentRole() {
        return currentRole;
    }
    
    public static String getCurrentUserId() {
        return currentUserId;
    }
    
    public static String getCurrentNationalId() {
        return currentNationalId;
    }
    
    
    public static String getCurrentEmail() {
        return currentEmail;
    }
    
    public static void setCurrentEmail(String email) {
        currentEmail = email;
    }
}
