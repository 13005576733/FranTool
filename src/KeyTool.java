import util.RuntimeHelper;
import util.Utils;

import java.beans.Encoder;
import java.io.*;

import static util.Utils.*;

/**
 * ����ǩ���ļ�
 */
public class KeyTool {
    KeyTool(String dir){
        String altas = "key";
        if (new File(dir,"key.keystore").exists()){
            Utils.log("key.keystore�ļ��Ѵ���");
            return;
        }

        String keystorefile = new File(dir,"key.keystore").getAbsolutePath();
//   ����������� ����8λ
        String password = sha1(""+System.currentTimeMillis()+""+random(0,10000), 8);
//    ������Կ�����ļ�
        writeFile(dir+"/password.ini", password+";"+altas, "utf-8");

        String s = "keytool -genkey -noprompt -alias key -keyalg RSA -dname \"CN=DL, OU=DL, O=DL, L=City, S=Province, C=CN\" -keystore \"" +
                keystorefile + "\" -storepass " + password + " -keypass " + password +
                " -validity 1000000 -keysize 1024";
        RuntimeHelper.getInstance().run(s);
        Utils.log("Done!");
    }


}
