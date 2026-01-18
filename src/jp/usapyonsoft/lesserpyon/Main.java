package jp.usapyonsoft.lesserpyon;
import java.util.Vector;
import java.io.*;

public class Main implements Constants {
  // 初期盤面を与える
  static final int ShokiBanmen[][]={
    {Koma.GKY,Koma.GKE,Koma.GGI,Koma.GKI,Koma.GOU,Koma.GKI,Koma.GGI,Koma.GKE,Koma.GKY},
    {Koma.EMP,Koma.GHI,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.GKA,Koma.EMP},
    {Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU},
    {Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP},
    {Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP},
    {Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP},
    {Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU},
    {Koma.EMP,Koma.SKA,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.SHI,Koma.EMP},
    {Koma.SKY,Koma.SKE,Koma.SGI,Koma.SKI,Koma.SOU,Koma.SKI,Koma.SGI,Koma.SKE,Koma.SKY},
  };
  
  // テスト用メイン関数
  public static void main(String argv[]) {
    try {
      Kyokumen k=new Kyokumen();
      if (argv.length==0) {
        // 引数の指定がない場合、初期配置を使う。
        // 先手番
        k.teban=SENTE;
        for(int dan=1;dan<=9;dan++) {
          for(int suji=9;suji>=1;suji--) {
            k.ban[suji][dan]=ShokiBanmen[dan-1][9-suji];
          }
        }
      } else {
        // 引数で指定があった場合、CSA形式の棋譜ファイルを読み込む。
        String csaFileName=argv[0];
        File f=new File(csaFileName);
        BufferedReader in=new BufferedReader(new FileReader(f));
        Vector v=new Vector();
        String s;
        while((s=in.readLine())!=null) {
          System.out.println("Read:"+s);
          v.add(s);
        }
        String csaKifu[]=new String[v.size()];
        v.copyInto(csaKifu);
        k.ReadCsaKifu(csaKifu);
      }
      System.out.println(k.toString());

      Vector v=GenerateMoves.generateLegalMoves(k);
      System.out.println("可能手："+v.size()+"手");
      for(int i=0;i<v.size();i++) {
        Te te=(Te)v.elementAt(i);
        System.out.println(te.toString());
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
