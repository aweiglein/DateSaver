package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Creates the 'Log' class - used to append login attempt, records login: username, date, time, and SUCCESS or FAIL.
 */
public class Log {

    private static final String loginActivity = "login_activity.txt";

    /**
     * Creates the 'login_activity' file.
     */
    public static void createLog(){
        File file = new File(loginActivity);
        try {
            if (file.createNewFile()) {
                System.out.println("\tFile created: " + file.getName() + "\n");
                Log.write("\tLog file created\n");
            } else {
                System.out.println("View login history at 'login_activity.txt'\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes to the 'login_activity' file.
     * @param str the string that is written to the file.
     */
    public static void write(String str){
        str = LocalDateTime.now().toString() + "] " + str;
        File file = new File(loginActivity);
        if (!file.exists()) {
            createLog();
        } else {
            try {
                FileWriter myWriter = new FileWriter(loginActivity, true);
                myWriter.append("[" + str);
                myWriter.close();
                System.out.println("\tLogin record added: [" + str);
            } catch (IOException e) {
                System.out.println("File error: " + e);
            }
        }
    }
}
