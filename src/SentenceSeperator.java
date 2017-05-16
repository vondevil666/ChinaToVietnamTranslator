import java.util.ArrayList;

/**
 * Created by dongz on 2017/5/15.
 */
public class SentenceSeperator {
    private static ArrayList<String[]> arrayList=new ArrayList<String[]>();
    private static StringBuilder singleSentenceContainer=new  StringBuilder();

    public static ArrayList<String[]> seperateInputTextIntoList(String inputText) {
//        for (char c : inputText.toCharArray()) {
        char[] stringArray=inputText.toCharArray();
        for(int i=0,j=1;j<inputText.length();i++,j++){
            singleSentenceContainer.append(stringArray[i]);
            //处理 。”/！”/？”此类情况
            if(stringArray[i]=='。' || stringArray[i]=='！' || stringArray[i]=='？'){
                if(stringArray[j]=='”'){
                    singleSentenceContainer.append(stringArray[j]);
                    i++;j++;
                }
                addStringAndResetContainer();
            }
            //处理”。/”！/”？此类情况
            else if(stringArray[i]=='”' && (stringArray[j]=='。' || stringArray[j]=='！' || stringArray[j]=='？')){
                singleSentenceContainer.append(stringArray[j]);
                i++;j++;
                addStringAndResetContainer();
            }
            //处理文末最后一个符号
            else if(j==inputText.length()-1){
                singleSentenceContainer.append(stringArray[j]);
                addStringAndResetContainer();
            }
        }
        return arrayList;
    }

    private static void addStringAndResetContainer() {
        String[] tmpStringArray = {singleSentenceContainer.toString(), ""};
        arrayList.add(tmpStringArray);
        singleSentenceContainer=new StringBuilder();
    }
}
