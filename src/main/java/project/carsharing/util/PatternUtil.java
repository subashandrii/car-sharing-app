package project.carsharing.util;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class PatternUtil {
    public static final String EMAIL_PATTERN = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`"
            + "{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])"
            + "(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    
    public static final String NAME_PATTERN = "^[A-Za-z\\-]{3,25}$";
    
    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    public static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
    
    public static double formatDoubleValue(double value) {
        String formattedValue = new DecimalFormat("#0.00")
                                            .format(value).replace(',', '.');
        return Double.parseDouble(formattedValue);
    }
}
