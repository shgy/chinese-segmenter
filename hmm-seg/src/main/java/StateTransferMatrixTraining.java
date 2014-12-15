import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import be.ac.ulg.montefiore.run.jahmm.ViterbiCalculator;
/*
 * 根据语料的结果训练HMM模型的状态转移矩阵
 * 一共有四个状态：
 * B：一个词的开始
 * E：一个词的结束
 * M：一个词的中间
 * S：单字成词
 * 统计公式： Aij = P(Cj|Ci)  =  P(Ci,Cj) / P(Ci) = countC(Ci,Cj) / countC(Ci)
 * */
public class StateTransferMatrixTraining {
    public StateTransferMatrixTraining(){
        readFile("src/main/resources/msr_training.utf8");
        hmm=buildHMM();
    }
    Hmm<ObservationInteger> hmm;
    //private String fileName;
    private final static HashMap<Character,Integer> map=new HashMap<Character,Integer>();
    private final static HashMap<Integer,Character> remap=new HashMap<Integer,Character>();
    //对汉字进行编码
    private final static HashMap<Character,Integer> cceMap=ChineseCharacterEncoding.getEncoding();
    static{
        map.put('B', 0);map.put('M', 1);
        map.put('E', 2);map.put('S', 3);
        remap.put(0, 'B');
        remap.put(1, 'M');
        remap.put(2, 'E');
        remap.put(3, 'S');
    }
    private long freqC[][]=new long[4][4];
    //统计混淆矩阵用到的
    private long freqCO[][]=new long[4][7004];
    private long countC[]=new long[4];
                                                       
    private double[][] transferMatrix=new double[4][4];
    private double[][] mixedMatrix=new double[4][7004];
    //M和E不可能出现在句子的首位
    private double[] Pi = {0.5, 0.0, 0.0, 0.5};
                                                       
    private void insert(StringBuilder sb,int start,int end){
        if(end-start>1){
            sb.append('B');
            for(int i=0;i<end-start-2;++i){
                sb.append('M');
            }
            sb.append('E');
        }else{
            sb.append('S');
        }
    }
    /*
     * 带文本内容,比如：你 现在 应该 去 幼儿园 了，
     *    输出的结果为：你S现B在E应B该E去S幼B儿M园E了S
     *    测试时用
     * */
    private void insertWithContent(String content,StringBuilder sb,int start,int end){
        if(end-start>1){
            sb.append(content.charAt(start));
            sb.append('B');
            for(int i=0;i<end-start-2;++i){
                sb.append(content.charAt(start+i+1));
                sb.append('M');
            }
            sb.append(content.charAt(end-1));
            sb.append('E');
        }else{
            sb.append(content.charAt(end-1));
            sb.append('S');
        }
    }
    /*
     * “  一点  外语  知识  、  数理化  知识  也  没有  ，  还  攀  什么  高峰  ？
     * 对一段文本按BEMS规则进行编码，标点符号有两种处理方法：
     *
     * 1、算作单字成词。
     * 2、直接过滤，不予考虑。
     * 个人认为方案2比较合理，单字成词受到字出现的语境有影响，而标点符号永远是单一的。
     * 在训练的过程中，其实用content.split("\\s{1,}");会更简单，清晰，但个人觉得
     * 用这种方法在数据量大的情况下，性能不咋地
     * @param content,需要编码的文本
     * @param withContent,编码后的文本是否带原文
     * @return 编码后的文本
     * */
    private String encode(String content,boolean withContent){
        if(content==null||"".equals(content.trim()))return null;
        //分词后的文本，去掉标点符号
        content=content.replaceAll("\\pP", " ").trim();
                                                           
        StringBuilder sb=new StringBuilder();
        int start,end,len;
        start=end=0;len=content.length();
        //根据空格对文本进行分词
        while(end<len){
            if(Character.isWhitespace(content.charAt(end))){
                if(end>start){
                    //得到一个词
                    if(withContent)
                        insertWithContent(content,sb,start,end);
                    else
                        insert(sb,start,end);
                    ++end;start=end;
                                                                       
                }else{++start;++end;}
                                                                   
            }else{++end;}
        }
        if(end>start){
            if(withContent)
                insertWithContent(content,sb,start,end);
            else
                insert(sb,start,end);
        }
                                                           
        return sb.toString();
    }
    //计算状态转移矩阵
    private void calStatus(){
        int i,j;
        for(i=0;i<4;i++){
            for(j=0;j<4;j++){
                transferMatrix[i][j]=(double)freqC[i][j]/countC[i];
            }
        }
    }
                                                       
    public double[][] getStatus(){
                                                           
        return transferMatrix;
    }
    //计算混淆矩阵
    private void calMixed(){
        int i,j;
        for(i=0;i<4;i++){
            for(j=0;j<7002;j++){
                mixedMatrix[i][j]=(double)(freqCO[i][j]+1)/countC[i];
            }
        }
    }
    public double[][] getMixed(){
                                                           
        return mixedMatrix;
    }
    //读入训练文本
    public void readFile(String fileName){
        BufferedReader br=null;
        String line,temp;
        try {
            br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)),"utf-8"));
            while((line=br.readLine())!=null){
                if("".equals(line.trim()))continue;
                //统计分类标签，不需要带字符
                temp=encode(line,false);
                stmStatus(temp);
                //统计混淆矩阵，需要带字符
                temp=encode(line,true);
                stmMixed(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {br.close(); }catch (IOException e) {e.printStackTrace();}
        }
        //根据freq和count矩阵来计算转移矩阵
        calStatus();
        calMixed();
    }
    /*
     * 统计每一行编码
     * */
    private void stmStatus(String encodeStr){
        int i,j,len;
        len=encodeStr.length();
        if(len<=0)return;
        for(i=0;i<len-1;++i){
            ++countC[map.get(encodeStr.charAt(i))];
            j=i+1;
            ++freqC[map.get(encodeStr.charAt(i))][map.get(encodeStr.charAt(j))];
        }
        ++countC[map.get(encodeStr.charAt(len-1))];
    }
    /*
     * 这里的话就需要两个字符两个字符一读
     * 你S现B在E应B该E去S幼B儿M园E了S
     * */
    private void stmMixed(String encodeStr){
        int i,j,len;
        len=encodeStr.length();
        //有错误的句子，直接忽略
        if(len%2!=0)return;
        Integer c,o;
        for(i=0;i<len;i+=2){
            j=i+1;
            c=map.get(encodeStr.charAt(j));
            o=cceMap.get(encodeStr.charAt(i));
            if(c==null||o==null){
                //System.out.println(encodeStr.charAt(i));
                continue;
            }
            ++freqCO[c][o-1];
        }
    }
                                                       
    private void print(double[][] A){
        int i,j;
        char[] chs={'B','M','E','S'};
        System.out.println("\t\t"+"B"+"\t\t\t"+"M"+"\t\t\t"+"E"+"\t\t\t"+"S");
        for(i=0;i<4;i++){
            System.out.print(chs[i]+"\t");
            for(j=0;j<4;j++){
                System.out.format("%.12f\t\t",A[i][j]);
                                                                   
            }
            System.out.println();
        }
    }
    //对观察字符进行编码，注意这里需要减一，其目的在于使下标从0开始
    private List<ObservationInteger> getOseq(String sen){
        List<ObservationInteger> oseq=new ArrayList<ObservationInteger>();
        for (int i = 0; i < sen.length(); i++) {
            oseq.add(new ObservationInteger(cceMap.get(sen.charAt(i))-1));
        }
        return oseq;
    }
    //对文本分词后的文本进行解码
    private String decode(String sen,int[] seqrs){
        StringBuilder sb=new StringBuilder();
        char ch;
        for(int i=0;i<sen.length();i++){
            sb.append(sen.charAt(i));
            ch=remap.get(seqrs[i]);
            if(ch=='E'||ch=='S')
                sb.append("/");
        }
        return sb.toString();
    }
    //训练hmm模型
    private Hmm<ObservationInteger> buildHMM(){
        Hmm<ObservationInteger> hmm=new Hmm < ObservationInteger >(4 ,new OpdfIntegerFactory (7004) );
        int i,j;
        for( i=0;i<4;i++){
            hmm.setPi(i, Pi[i]);
        }
        for(i=0;i<4;i++){
            for(j=0;j<4;j++){
                hmm.setAij(i, j, transferMatrix[i][j]);
            }
            hmm.setOpdf(i, new OpdfInteger(mixedMatrix[i]));
        }
        return hmm;
    }
    public String seg(String sen){
        List<ObservationInteger> oseq=getOseq( sen);
        ViterbiCalculator vc=new ViterbiCalculator(oseq, hmm);
        int[] segrs= vc.stateSequence();
        return decode(sen,segrs);
    }
    public static void main(String[] args) {
        StateTransferMatrixTraining tr=new StateTransferMatrixTraining();
        /*
         * 由于这里只是简单的实现HMM模型，在细节上并没有作过多的处理，所以不能真正意义上用于分词。
         * 这里的文本是不能加标点符号的，原因在于标点符号并没有编码
         * */
        String[] segs={"检察院鲍绍坤检察长","人们常说生活是一部教科书","改判被告人死刑立即执行",
                "结婚的和尚未结婚的都需要登记","邓颖超生前使用过的物品"};
        for (String string : segs) {
            System.out.println(tr.seg(string));
        }
    }
}