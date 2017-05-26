import java.io.*;
import java.util.ArrayList;

/**
 *工程主类，接受一段文本或一个txt文件路径，返回原文和译文的句对列表ArrayList<String[]>。
 * 该类有3个公开接口：1.setSourceLanguage()设置源语言（可选）；2.setTargetLanguage()设置目标语言
 *                   3.translate()，执行翻译流程。
 *
 * 该类工作流如下：
 * 1.如果接收到txt文件路径，则检查文件有效性，并读取文件文本。后续流程与输入一段文本相同，执行步骤2；
 * 2.用SentenceSeperator类对文本分句；
 * 3.生成一个临时HTML文件，并将步骤2的分句结果填入HTML；
 * 4.调用无界面浏览器phantomjs执行上述HTML文件，获得待翻译句子对应的已翻译句子，存入ArrayList<String[]>；
 * 5.将步骤4的结果写入本地文件（可选），删除步骤3中的临时HTML文件；
 * 6.返回步骤4的ArrayList<String[]>。
 *
 */
public class ChineseToVietnameseTranslator {
    public final static String TARGETELEMENTCLASS="ctvParagraph";
    public static String sourceLanguage = "";
    public static String targetLanguage = "en";
    private ArrayList<String[]> sentenceArrayPair;
    private File tempHTMLFile;

    public static void main(String args[]) {
        String inputTextPath = "inputText.txt";
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
//        writeArrayPairIntoLocalFile(sentenceArrayPair);  //句对写入output.txt文件
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