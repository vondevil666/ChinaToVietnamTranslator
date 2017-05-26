import java.io.*;
import java.util.ArrayList;

/**
 * Created by dongz on 2017/5/25.
 *该类调用无界面浏览器phantomjs
 *入口函数为静态函数translate(),返回值类型为ArrayList<String>，即翻译结果的句子列表
 * phantomjs需要从cmd输入命令，本工程的命令至少需要以下4个参数：
 * 1.phantomjs.exe文件路径，本工程已放在工程根目录下
 * 2.phantom.js文件路径，该js文件执行无界面浏览器执行的具体命令
 * 3.待翻译文本组成的html文件路径
 * 4.目标语言。以上所有参数之间需要空格。
 *
 * java用Runtime类调用命令窗，执行上述js文件，从文件流中获得js的输出信息（console.log），即翻译完的句子。
 *
 **/
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

    //检查3个必备文件有效性。此处抛出异常处理得有些不成熟。
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

    //从流中获得翻译的句子
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
