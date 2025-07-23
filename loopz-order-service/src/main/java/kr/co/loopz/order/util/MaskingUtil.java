package kr.co.loopz.order.util;

public class MaskingUtil {

    public static String maskName(String name) {
        if (name == null || name.length() <= 1) return name;
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;

        if (phoneNumber.matches("^01[016789]-\\d{3,4}-\\d{4}$")) {
            return phoneNumber.replaceAll("(?<=01[016789]-)\\d{3,4}(?=-)", "****");
        }

        if (phoneNumber.matches("^01[016789]\\d{7,8}$")) {
            return phoneNumber.replaceAll("(?<=^01[016789])\\d{4}(?=\\d{4}$)", "****");
        }

        return phoneNumber;
    }


}
