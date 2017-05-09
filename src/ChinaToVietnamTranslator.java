import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChinaToVietnamTranslator {
    public static final String loadFinishedFlagId = "WidgetFloaterPanels";
    public static final String loadFinishedFlagAttr = "len";
    private static String inputTextPath = "inputText.txt";
    public String[] inputTextSentenceArray;

    public static void main(String args[]) {
        ChinaToVietnamTranslator main = new ChinaToVietnamTranslator();
        main.startProcess(inputTextPath);
    }

    public void startProcess(String inputTextPath) {
        File inputTextFile = new File(inputTextPath);
        if (!inputTextFile.exists()) {
            System.out.println("Input Text File Not Found.Check The Path.");
            return;
        }
        StringBuilder sbInputText = readFileReturnStringBuilder(inputTextFile);
        inputTextSentenceArray = seperateInputText(sbInputText);
        StringBuilder Wait_to_TranslateHTML = buildWait_to_TranslateHTML();
        WebDriver driver=prepareWebDriver(Wait_to_TranslateHTML);
        waitHTMLToFinishLoad(driver);

//        System.setProperty("webdriver.chrome.driver", "E://JJavaWorkspace//chromedriver.exe");
//        WebDriver driver = new ChromeDriver();
//		driver.get("C:/Users/gbw/Desktop/chichichi.html");

        //
//        StringBuilder sb = new StringBuilder();
//        try {
//            File file = new File("C:/Users/gbw/Desktop/chichichi.html");
//            FileReader fr = new FileReader(file);
//            BufferedReader br = new BufferedReader(fr);
//            String tmp = "";
//            while ((tmp = br.readLine()) != null) {
//                sb.append(tmp).append('\n');
//            }
//            br.close();
//            fr.close();
//        } catch (Exception e) {
//        }

        //
//        File file = new File("TEMP_FILE_AUTO_DELETE_IGNORE_THIS.html");
//        try {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
//            bw.write(sb.toString());
//            bw.close();
//        } catch (Exception e) {
//        }
//        driver.get(file.getAbsolutePath());
//        file.delete();

//        MyExpectedCondition myExpCon = new MyExpectedCondition(driver);
//        new WebDriverWait(driver, 10).until(myExpCon);

        WebElement element = getElementContentToCheckTranslated(driver);
        System.out.println(element.getText());
    }

    private void waitHTMLToFinishLoad(WebDriver driver) {
        MyExpectedCondition myExpCon = new MyExpectedCondition(driver);
        new WebDriverWait(driver, 10).until(myExpCon);
    }

    /**
     * 将待翻译html源码做成临时文件，让WebDriver读取该文件并删除该文件
     * @param sb 待翻译文本做成的带翻译html源码
     * @return driver
     */
    private WebDriver prepareWebDriver(StringBuilder sb){
        System.setProperty("webdriver.chrome.driver", "E://JJavaWorkspace//chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        File file = new File("TEMP_FILE_AUTO_DELETE_IGNORE_THIS.html");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(sb.toString());
            bw.close();
        } catch (Exception e) {
        }
        driver.get(file.getAbsolutePath());
        file.delete();
        return driver;
    }

    private StringBuilder buildWait_to_TranslateHTML() {
        StringBuilder sb=new StringBuilder();
        sb.append("<head>");
        sb.append("<script type='text/javascript'>setTimeout(function(){{var s=document.createElement('script');s.type='text/javascript';s.charset='UTF-8';");
        sb.append("s.src=((location && location.href && location.href.indexOf('https') == 0)?'https://ssl.microsofttranslator.com':'http://www.microsofttranslator.com')");
        sb.append("+'/ajax/v3/WidgetV3.ashx?siteData=ueOIGRSKkd965FeEGM5JtQ**&ctf=False&ui=false&settings=Auto&from=&loc=zh-chs';");
        sb.append("var p=document.getElementsByTagName('head')[0]||document.documentElement;p.insertBefore(s,p.firstChild);}},0);</script>");
        sb.append("<style>#WidgetFloater,#WidgetFloaterPanels,#ProgressFill{visibility:hidden;}[translate=\"no\"]{visibility:hidden;}}</style>");
        sb.append("<script>function findWidgetFloater(){if(document.getElementById(\"WidgetFloaterPanels\")==null){console.log(\"!!!没找到WidgetFloaterPanels\");setTimeout('findWidgetFloater()',200);}");
        sb.append("else {console.log(\"WidgetFloaterPanels Founded！！！\");}}findWidgetFloater();</script>");
        sb.append("</head><body>");
        for(int i=0;i<inputTextSentenceArray.length;i++) {
            sb.append("<p>").append(inputTextSentenceArray[i]+'\n');
        }
        sb.append("</body>");
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

    private String[] seperateInputText(StringBuilder sb) {
        return sb.toString().split("。\\|！\\|？\\|");
    }

    public WebElement getElementContentToCheckTranslated(WebDriver driver) {
        try {
            return driver.findElement(By.id("5"));
        } catch (Exception e) {
            System.out.println("No ID Founded.");
        }
        return null;
    }
}

class MyExpectedCondition implements ExpectedCondition<Boolean> {
    private WebDriver driver;

    public MyExpectedCondition(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public Boolean apply(WebDriver d) {
        int len = 0;
        len = getElementByIdAndGetAttributeLenth(ChinaToVietnamTranslator.loadFinishedFlagId, ChinaToVietnamTranslator.loadFinishedFlagAttr);
        return len > 0;
    }

    public int getElementByIdAndGetAttributeLenth(String id, String attr) {
        try {
            return driver.findElement(By.id(id)).getAttribute(attr).length();
        } catch (Exception e) {
            System.out.println("Can Not Find loadFinishedFlagId.Wait for another shot.");
        }
        return 0;
    }
}