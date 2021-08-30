package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static util.Utils.log;


public class RuntimeHelper {

    public static boolean sIsWindow;
    private static RuntimeHelper mInstance;

    public static RuntimeHelper getInstance() {
        if (mInstance == null)
            synchronized (RuntimeHelper.class) {
                if (mInstance == null)
                    mInstance = new RuntimeHelper();
            }

        return mInstance;
    }

    //    ִ��DOS����,exe��
    public void run(String s) {
        String command = s.replace("\"", "");
        sIsWindow = System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
        Runtime run = Runtime.getRuntime();
        try {
            if (sIsWindow)
                command = "cmd /c " + s;
            Process process = run.exec(command);
            Utils.log(s);
            InputStream reader = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(reader));
            String ss;
            while ((ss = bufferedReader.readLine()) != null) {
                log(ss);
            }
            if (process.waitFor() == 0) {
//                    Utils.log("ִ�гɹ�");
            } else {
                Utils.log("ִ��ʧ��");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
