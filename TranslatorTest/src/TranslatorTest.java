import java.util.ArrayList;

/**
 * Created by dongz on 2017/5/15.
 */
public class TranslatorTest {

    public static void main(String[] args) {
        TranslatorTest tt=new TranslatorTest();
        tt.test();
    }

    private void test(){
        String target = "no bullshit.";
        ChineseToVietnameseTranslator.setTargetLanguage("zh-CHS");
        ArrayList<String[]> list = ChineseToVietnameseTranslator.translate("TranslatorTest/test.txt");
        for (String[] s : list) {
            System.out.println(s[0]+" && "+s[1]+'\n');
        }
    }
}
