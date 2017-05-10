import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static javax.swing.text.html.CSS.getAttribute;

public class ChineseToVietnameseTranslator {
    private static String inputTextPath = "inputText.txt";
    private ArrayList<String[]> sentenceArrayPair;
    private final String TARGETELEMENTCLASS="ctvParagraph";
    public final static String sourceLanguage = "zh-chs";
    public final static String targetLanguage = "en";

    public static void main(String args[]) {
        ChineseToVietnameseTranslator main = new ChineseToVietnameseTranslator();
        main.startProcess(inputTextPath);
    }

    public static void translate(String inputTextPath) {
        ChineseToVietnameseTranslator main = new ChineseToVietnameseTranslator();
        main.startProcess(inputTextPath);
    }

    private void startProcess(String inputTextPath) {
        File inputTextFile = new File(inputTextPath);
        if (!inputTextFile.exists()) {
            System.out.println("Input Text File Not Found.Check The Path.");
            return;
        }
        StringBuilder sbInputText = readFileReturnStringBuilder(inputTextFile);
        sentenceArrayPair = seperateInputTextIntoList(sbInputText);
        StringBuilder Wait_to_TranslateHTML = buildWait_to_TranslateHTML();
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver driver=prepareWebDriver(Wait_to_TranslateHTML);
        waitHTMLToFinishLoad(driver);
        getTranslatedElementContent(driver);                 //and add them to 'sentenceArrayPair'.
//        for (String[] array : sentenceArrayPair) {
//            System.out.println(array[0]+" &&& "+array[1]+'\n');
//        }
        writeArrayPairIntoLocalFile(sentenceArrayPair);

    }

    private void writeArrayPairIntoLocalFile(ArrayList<String[]> arrayList) {
        File file = new File("outputText.txt");
        StringBuilder sb=new StringBuilder();
        for (String[] s : arrayList) {
            sb.append(s[0]+"\r\n").append(s[1]).append("\r\n\r\n");
        }
        writeIntoLocalFile(file,sb.toString());
    }

    private void waitHTMLToFinishLoad(WebDriver driver) {
        MyExpectedCondition myExpCon = new MyExpectedCondition(driver);
        new WebDriverWait(driver, 20).until(myExpCon);
    }

    /**
     * 将待翻译html源码做成临时文件，让WebDriver读取该文件最后删除该文件
     * @param sb 待翻译文本做成的带翻译html源码
     * @return driver
     */
    private WebDriver prepareWebDriver(StringBuilder sb){
        WebDriver driver = new ChromeDriver();
        File file = new File("TEMP_FILE_AUTO_DELETE_IGNORE_THIS.html");
        writeIntoLocalFile(file, sb.toString());
        driver.get(file.getAbsolutePath());
        file.deleteOnExit();
        return driver;
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

    private StringBuilder buildWait_to_TranslateHTML() {
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
        return sb;
    }

    private StringBuilder readFileReturnStringBuilder(File file) {
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
        return sb;
    }

    private ArrayList<String[]> seperateInputTextIntoList(StringBuilder sb) {
        ArrayList<String[]> arrayList=new ArrayList<String[]>();
        StringBuilder singleSentence=new  StringBuilder();
        for (char c : sb.toString().toCharArray()) {
            singleSentence.append(c);
            if(c=='。' || c=='！' || c=='？'){
                String[] tmpStringArray = {singleSentence.toString(), ""};
                arrayList.add(tmpStringArray);
                singleSentence=new StringBuilder();
            }
        }
        return arrayList;
    }

    private void getTranslatedElementContent(WebDriver driver) {
        try {
            List<WebElement> elements = driver.findElements(By.className(TARGETELEMENTCLASS));
            if(elements.size()!=sentenceArrayPair.size()) System.out.println("抓取数据个数与输入句子个数不同");
            else{
                for (int i=0;i<elements.size();i++) {
                    sentenceArrayPair.get(i)[1] = elements.get(i).getText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MyExpectedCondition implements ExpectedCondition<Boolean> {
    private WebDriver driver;

    MyExpectedCondition(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public Boolean apply(WebDriver d) {
        return ifElementFontAttributeLangExist();
    }

    private boolean ifElementFontAttributeLangExist() {
        try {
            return driver.findElements(By.className("ctvParagraph")).get(0).getAttribute("lang").equals(ChineseToVietnameseTranslator.targetLanguage);
        } catch (Exception e) {
            System.out.println("Can Not Find Attribute of Elements with Target Language");
//            e.printStackTrace();
        }
        return false;
    }
}