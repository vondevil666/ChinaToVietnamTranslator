import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static javax.swing.text.html.CSS.getAttribute;

public class ChineseToVietnameseTranslator {
    public final static String TARGETELEMENTCLASS="ctvParagraph";
    public static String sourceLanguage = "";
    public static String targetLanguage = "en";
    private static String inputTextPath = "inputText.txt";
    private ArrayList<String[]> sentenceArrayPair;
    private File tempHTMLFile;

    public static void main(String args[]) {
        ChineseToVietnameseTranslator main = new ChineseToVietnameseTranslator();
        main.startProcessFromText(main.readFileToText(inputTextPath));
    }

    public static ArrayList<String[]> translate(String inputString) {
        ChineseToVietnameseTranslator main = new ChineseToVietnameseTranslator();
        if(inputString.endsWith(".txt")){
            inputString = main.readFileToText(inputString);
        }
        return main.startProcessFromText(inputString);
    }

    private ArrayList<String[]> startProcessFromText(String inputText){
        GetTranslatedStringFromPhantomjs.setTargetLanguage(targetLanguage);
        sentenceArrayPair = SentenceSeperator.seperateInputTextIntoList(inputText);
        buildWait_to_TranslateHTML();
        getTranslatedContent();
        writeArrayPairIntoLocalFile(sentenceArrayPair);
        tempHTMLFile.deleteOnExit();
        return sentenceArrayPair;
    }

    public static void setSourceLanguage(String sourceLang) {
        sourceLanguage=sourceLang;
    }

    public static void setTargetLanguage(String targetLang) {
        targetLanguage=targetLang;
    }

    private String readFileToText(String inputTextPath) {
        File inputTextFile = new File(inputTextPath).getAbsoluteFile();
        if(!inputTextFile.exists()){
            System.out.println("Error:Input Text File Not Found.Check The Path.");
            System.exit(1);
        }
        return(readFileContent(inputTextFile));
    }

    private void writeArrayPairIntoLocalFile(ArrayList<String[]> arrayList) {
        File file = new File("outputText.txt");
        StringBuilder sb=new StringBuilder();
        for (String[] s : arrayList) {
            sb.append(s[0]+"\r\n").append(s[1]).append("\r\n\r\n");
        }
        writeIntoLocalFile(file,sb.toString());
    }

    private void writeIntoLocalFile(File file,String string) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(string.toString());
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildWait_to_TranslateHTML() {
        StringBuilder sb=new StringBuilder();
        sb.append("<html><head><meta charset=UTF-8>");
        sb.append("<script type='text/javascript'>setTimeout(function(){{var s=document.createElement('script');s.type='text/javascript';s.charset='UTF-8';");
        sb.append("s.src=((location && location.href && location.href.indexOf('https') == 0)?'https://ssl.microsofttranslator.com':'http://www.microsofttranslator.com')");
        sb.append("+'/ajax/v3/WidgetV3.ashx?siteData=ueOIGRSKkd965FeEGM5JtQ**&ctf=False&ui=false&settings=Auto&from="+sourceLanguage+"&loc="+targetLanguage+"';");
        sb.append("var p=document.getElementsByTagName('head')[0]||document.documentElement;p.insertBefore(s,p.firstChild);}},0);</script>");
        sb.append("<style>#WidgetFloater,#WidgetFloaterPanels,#ProgressFill{visibility:hidden;}[translate=\"no\"]{visibility:hidden;}}</style>");
        sb.append("<script>function findWidgetFloater(){if(document.getElementById(\"WidgetFloaterPanels\")==null){console.log(\"!!!没找到WidgetFloaterPanels\");setTimeout('findWidgetFloater()',200);}");
        sb.append("else {console.log(\"WidgetFloaterPanels Founded！！！\");}}findWidgetFloater();</script>");
        sb.append("</head><body>");
        for (String[] s : sentenceArrayPair) {
            sb.append("<p class="+TARGETELEMENTCLASS+">").append(s[0]+"</p>");
        }
        sb.append("</body></html>");
        tempHTMLFile = new File("TEMP_FILE_AUTO_DELETE_IGNORE_THIS.html");
        writeIntoLocalFile(tempHTMLFile, sb.toString());
    }

    private String readFileContent(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String tmp = "";
            while ((tmp = br.readLine()) != null) {
                sb.append(tmp).append('\n');
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }



    private void getTranslatedContent() {
        try {
//            List<WebElement> elements = driver.findElements(By.className(TARGETELEMENTCLASS));
            ArrayList<String> translatedStringArray=GetTranslatedStringFromPhantomjs.translate();
            int translatedStringArraySize=translatedStringArray.size();
            if(translatedStringArraySize!=sentenceArrayPair.size()) {
                System.out.println("抓取数据个数与输入句子个数不同");
                for(String array: translatedStringArray){
                    System.out.println(array);
                }
            }
            else{
                for (int i=0;i<translatedStringArraySize;i++) {
                    sentenceArrayPair.get(i)[1] = translatedStringArray.get(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}