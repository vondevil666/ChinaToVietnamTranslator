import java.util.ArrayList;

/**
 * Created by dongz on 2017/5/15.
 * 中文分句，并将结果填入最终句对数组列表的数组第一个位置。
 * 句对数组列表结构可以查看addStringAndResetContainer()函数细节。
 */
public class SentenceSeperator {

    public static ArrayList<String[]> seperateInputTextIntoList(String inputText) {
        ArrayList<String[]> sentencePairArrayList=new ArrayList<String[]>();
        StringBuilder singleSentenceContainer=new  StringBuilder();
        char[] stringArray=inputText.toCharArray();
        for(int i=0,j=1;j<inputText.length();i++,j++){
            singleSentenceContainer.append(stringArray[i]);
            //处理 。”/！”/？”此类情况
            if(stringArray[i]=='。' || stringArray[i]=='！' || stringArray[i]=='？'){
                if(stringArray[j]=='”'){
                    singleSentenceContainer.append(stringArray[j]);
                    i++;j++;
                }
                String[] tmpStringArray = {singleSentenceContainer.toString(), ""};
                sentencePairArrayList.add(tmpStringArray);
                singleSentenceContainer=new StringBuilder();
            }
            //处理”。/”！/”？此类情况
            else if(stringArray[i]=='”' && (stringArray[j]=='。' || stringArray[j]=='！' || stringArray[j]=='？')){
                singleSentenceContainer.append(stringArray[j]);
                i++;j++;
                String[] tmpStringArray = {singleSentenceContainer.toString(), ""};
                sentencePairArrayList.add(tmpStringArray);
                singleSentenceContainer=new StringBuilder();
            }
            //处理文末最后一个符号
            else if(j==inputText.length()-1){
                singleSentenceContainer.append(stringArray[j]);
                String[] tmpStringArray = {singleSentenceContainer.toString(), ""};
                sentencePairArrayList.add(tmpStringArray);
                singleSentenceContainer=new StringBuilder();
            }
        }
        return sentencePairArrayList;
    }

/*
    private static void addStringAndResetContainer() {
        String[] tmpStringArray = {singleSentenceContainer.toString(), ""};
        sentencePairArrayList.add(tmpStringArray);
        singleSentenceContainer=new StringBuilder();
    }*/
}
