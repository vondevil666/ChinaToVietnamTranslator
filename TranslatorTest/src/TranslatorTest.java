import java.util.ArrayList;

/**
 * Created by dongz on 2017/5/15.
 * 该类为ChineseToVietnameseTranslator的测试类。
 * ChineseToVietnameseTranslator共有3个静态函数可调用：
 * 1.ChineseToVietnameseTranslator.setSourceLanguage(),设定源语言，默认为空，即bing服务器自动识别输入语言。
 * 2.ChineseToVietnameseTranslator.setTargetLanguage(),设定目标语言，默认为英语（en）。
 * 3.ChineseToVietnameseTranslator.translate(),开始翻译流程。该函数可以接受String类型文本，或.txt文件路径，txt文件必须以utf-8无bom格式储存。
 *   translate()函数返回值类型为ArrayList<String[]>
 */
public class TranslatorTest {

    public static void main(String[] args) {
        TranslatorTest tester=new TranslatorTest();
        tester.test();
    }

    private void test(){
        String testSingleSentence = "人人为我，我为人人。";
        String testInputFilePath = "TranslatorTest/test.txt";

        //设定目标语言
        ChineseToVietnameseTranslator.setTargetLanguage("en");

        //测试翻译单句
        ArrayList<String[]> list = ChineseToVietnameseTranslator.translate(testSingleSentence);

        //测试翻译.txt文件内容
//        ArrayList<String[]> list = ChineseToVietnameseTranslator.translate(testInputFilePath);

        //输出结果s
        for (String[] s : list) {
            System.out.println(s[0]+" \n译文: "+s[1]+'\n');
        }
        System.out.println("翻译完成");
    }
}
