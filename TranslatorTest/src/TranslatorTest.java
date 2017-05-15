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
        String target = "中文转英文。孙子说：“不舍昼夜。”老子说，孙子是个“孙子”！英文不能转中文吗？扯淡。";
        ArrayList<String[]> list = ChineseToVietnameseTranslator.translate("TranslatorTest/test.txt");
        for (String[] s : list) {
            System.out.println(s[0]+" && "+s[1]+'\n');
        }
    }
}
