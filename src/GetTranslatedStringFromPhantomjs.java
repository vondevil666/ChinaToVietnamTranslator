import java.io.*;
import java.util.ArrayList;

/**
 * Created by dongz on 2017/5/25.
 */
public class GetTranslatedStringFromPhantomjs {
    private static final String phantomjsEXEPath = "phantomjs.exe";
    private static final String phantom_jsPath = "phantom.js";
    private static final String tempHTMLPath="TEMP_FILE_AUTO_DELETE_IGNORE_THIS.html";
    public static String targetLanguage="en";

    public static void setTargetLanguage(String targetLang) {
        targetLanguage=targetLang;
    }

    public static ArrayList<String> translate() throws Exception{
        ArrayList<String> translatedStringArray=new ArrayList<String>();
        checkThreeFilesValidation();
        Runtime rt=Runtime.getRuntime();
        Process p=rt.exec(phantomjsEXEPath+" "+phantom_jsPath+" "+tempHTMLPath+" "+targetLanguage);
        translatedStringArray = interpretStreamToArray(p.getInputStream());
        return translatedStringArray;
    }

    private static void checkThreeFilesValidation(){
        try {
            checkFileValidation(phantomjsEXEPath);
            checkFileValidation(phantom_jsPath);
            checkFileValidation(tempHTMLPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkFileValidation(String filePath) throws IOException{
        File file = new File(filePath);
        if(!file.exists()) {
           throw new IOException();
        }
    }

    private static ArrayList<String> interpretStreamToArray(InputStream inputStream){
        ArrayList<String> stringArray = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String tmp="";
            while((tmp=br.readLine())!=null){
                stringArray.add(tmp);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//		System.out.println(translatedStringArray);
        return stringArray;
    }
}
