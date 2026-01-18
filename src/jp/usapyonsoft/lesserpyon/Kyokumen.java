package jp.usapyonsoft.lesserpyon;
import java.util.List;
import java.util.ArrayList;

class Kyokumen implements Constants,Cloneable {

  // 盤面
  int ban[][];

  // 持ち駒
  List<List<Integer>> hand;
  
  // 手番
  int teban=SENTE;
  
  public Kyokumen() {
    ban=new int[11][11];
    hand = new ArrayList<List<Integer>>();
    hand.add(new ArrayList<Integer>()); // hand.get(0)
    hand.add(new ArrayList<Integer>()); // hand.get(1)
  }
  
  // 局面のコピーを行う
  public Object clone() {
    Kyokumen k=new Kyokumen();

    // 盤面のコピー
    for(int suji=0;suji<11;suji++) {
      for(int dan=0;dan<11;dan++) {
        k.ban[suji][dan]=ban[suji][dan];
      }
    }
    
    // 持ち駒のコピー
    k.hand = new ArrayList<List<Integer>>();
    k.hand.add(new ArrayList<Integer>(hand.get(0)));
    k.hand.add(new ArrayList<Integer>(hand.get(1)));
    
    // 手番のコピー
    k.teban=teban;
    
    return k;
  }
  
  // 局面が同一かどうか
  public boolean equals(Object o) {
    Kyokumen k=(Kyokumen)o;
    if (k==null) return false;
    return equals(k);
  }

  // 局面が同一かどうか
  public boolean equals(Kyokumen k) {
    // 手番の比較
    if (teban!=k.teban) {
      return false;
    }
    
    // 盤面の比較
    // 各マスについて…
    for(int suji=1;suji<=9;suji++) {
      for(int dan=1;dan<=9;dan++) {
        // 盤面上の筋と段にある駒が、比較対象の盤面上の同じ位置にある駒と
        // 同じかどうか比較する。
        if (!(ban[suji][dan]==k.ban[suji][dan])) {
          // 違っていたら、falseを返す。
          return false;
        }
      }
    }
    
    // 持ち駒の比較
    // 駒の種類ごとに枚数が同じかどうかを比較してみる。
    // 比較用の配列を準備。
    int handSente[]=new int[Koma.HI+1];
    int handGote []=new int[Koma.HI+1];
    int compareHandSente[]=new int[Koma.HI+1];
    int compareHandGote []=new int[Koma.HI+1];

    // 各配列は0で初期化されている。
    // handに現れた駒を、駒の種類毎に数えていく。

    // まず、自分の先手の持ち駒
    for(int i=0;i<hand.get(0).size();i++) {
      int koma=hand.get(0).get(i);
      int komaShu=Koma.getKomashu(koma);
      handSente[komaShu]++;
    }
    // 自分の後手の持ち駒
    for(int i=0;i<hand.get(1).size();i++) {
      int koma=hand.get(1).get(i);
      int komaShu=Koma.getKomashu(koma);
      handGote[komaShu]++;
    }

    // 比較対象の先手の持ち駒
    for(int i=0;i<k.hand.get(0).size();i++) {
      int koma=k.hand.get(0).get(i);
      int komaShu=Koma.getKomashu(koma);
      compareHandSente[komaShu]++;
    }
    // 比較対象の後手の持ち駒
    for(int i=0;i<k.hand.get(1).size();i++) {
      int koma=k.hand.get(1).get(i);
      int komaShu=Koma.getKomashu(koma);
      compareHandGote[komaShu]++;
    }
    
    // 持ち駒の枚数を比較する。
    for(int i=Koma.FU;i<=Koma.HI;i++) {
      if (handSente[i]!=compareHandSente[i]) return false;
      if (handGote [i]!=compareHandGote [i]) return false;
    }
    
    // 完全に一致した。
    return true;
  }

  @Override
  public int hashCode() {
    int result = teban;
    // include board
    for (int suji = 1; suji <= 9; suji++) {
      for (int dan = 1; dan <= 9; dan++) {
        result = 31 * result + ban[suji][dan];
      }
    }
    // include hand counts (order-independent as equals does)
    int handSente[] = new int[Koma.HI + 1];
    int handGote[] = new int[Koma.HI + 1];
    for (int i = 0; i < hand.get(0).size(); i++) {
      int koma = hand.get(0).get(i);
      handSente[Koma.getKomashu(koma)]++;
    }
    for (int i = 0; i < hand.get(1).size(); i++) {
      int koma = hand.get(1).get(i);
      handGote[Koma.getKomashu(koma)]++;
    }
    for (int i = Koma.FU; i <= Koma.HI; i++) {
      result = 31 * result + handSente[i];
      result = 31 * result + handGote[i];
    }
    return result;
  }
  
  // ある位置にある駒を取得する
  public int get(Position p) {
    // 盤外なら、「盤外＝壁」を返す
    if (p.suji<1 || 9<p.suji || p.dan<1 || 9<p.dan) {
      return Koma.WALL;
    }
    return ban[p.suji][p.dan];
  }
  
  // ある位置にある駒を置く。
  public void put(Position p,int koma) {
    ban[p.suji][p.dan]=koma;
  }

  // 与えられた手で一手進めてみる。
  public void move(Te te) {
    // 駒の行き先に駒があったなら…
    if (get(te.to)!=Koma.EMPTY) {
      // 持ち駒にする
      if (Koma.isSente(get(te.to))) {
        // 取った駒が先手の駒なら後手の持ち駒に。
        int koma=get(te.to);
        // 成りなどのフラグ、先手・後手の駒のフラグをクリア。
        koma=koma & 0x07;
        // 後手の駒としてのフラグをセット
        koma=koma | GOTE;
        hand.get(1).add(Integer.valueOf(koma));
      } else {
        // 取った駒が後手の駒なら先手の持ち駒に。
        int koma=get(te.to);
        // 成りなどのフラグ、先手・後手の駒のフラグをクリア。
        koma=koma & 0x07;
        // 先手の駒としてのフラグをセット
        koma=koma | SENTE;
        hand.get(0).add(Integer.valueOf(koma));
      }
    }
    if (te.from.suji==0) {
      // 持ち駒を打った
        if (Koma.isSente(te.koma)) {
        // 先手の駒なら、先手の持ち駒を減らす。
        for(int i=0;i<hand.get(0).size();i++) {
          int koma=hand.get(0).get(i);
          if (koma==te.koma) {
            hand.get(0).remove(i);
            break;
          }
        }
      } else {
        // 後手の駒を打ったはずなので、後手の持ち駒を減らす
        for(int i=0;i<hand.get(1).size();i++) {
          int koma=hand.get(1).get(i);
          if (koma==te.koma) {
            hand.get(1).remove(i);
            break;
          }
        }
      }
    } else {
      // 盤上の駒を進めた→元の位置は、EMPTYに。
      put(te.from,Koma.EMPTY);
    }
    // 駒を移動先に進める。
    int koma=te.koma;
    if (te.promote) {
      // 「成り」の処理
      koma=koma|Koma.PROMOTE;
    }
    put(te.to,koma);
  }
  
  // 玉を探して、位置を返す
  public Position searchGyoku(int teban) {
    // 探す駒は、teban側の玉
    int toSearch=teban|Koma.OU;
    // 筋、段でループ
    for(int suji=1;suji<=9;suji++) {
      for(int dan=1;dan<=9;dan++) {
        if (ban[suji][dan]==toSearch) {
          // 見つかった位置を返す。
          return new Position(suji,dan);
        }
      }
    }
    // 見つからなかった…。
    // 駒の利きの届かない盤外を返す。
    // 0,0などに設定すると、１一にいる相手駒の利きを見つけてしまう可能性がある。
    return new Position(-2,-2);
  }
  
  // CSA形式の棋譜ファイル文字列
  static final String csaKomaTbl[] = {
    "   ","FU","KY","KE","GI","KI","KA","HI",
    "OU","TO","NY","NK ","NG","","UM","RY",
    ""  ,"+FU","+KY","+KE","+GI","+KI","+KA","+HI",
    "+OU","+TO","+NY","+NK","+NG",""   ,"+UM","+RY",
    ""  ,"-FU","-KY","-KE","-GI","-KI","-KA","-HI",
    "-OU","-TO","-NY","-NK","-NG",""   ,"-UM","-RY"
  };

  
  // CSA形式の棋譜ファイルから、局面を読み込む
  public void ReadCsaKifu(String[] csaKifu) {
    // 持ち駒…枚数の配列にしておく。
    int motigoma[][]=new int[2][Koma.HI+1];

    // 駒箱に入っている残りの駒。残りを全て持ち駒にする際などに使用する。
    int restKoma[]=new int[Koma.HI+1];
    
    // 持ち駒を空に。
    for (int i = 0; i <= Koma.HI; i++) {
      motigoma[0][i] = 0;
      motigoma[1][i] = 0;
    }

    // 駒箱に入っている駒＝その種類の駒の枚数
    restKoma[Koma.FU]=18;
    restKoma[Koma.KY]=4;
    restKoma[Koma.KE]=4;
    restKoma[Koma.GI]=4;
    restKoma[Koma.KI]=4;
    restKoma[Koma.KA]=2;
    restKoma[Koma.HI]=2;
    
    // 盤面を空に初期化
    for(int suji=1;suji<=9;suji++) {
      for(int dan=1;dan<=9;dan++) {
        ban[suji][dan]=Koma.EMPTY;
      }
    }
    
    // 文字列から読み込み
    for(int i=0;i<csaKifu.length;i++) {
      String line=csaKifu[i];
      System.out.println(""+i+" :"+line);
      if (line.startsWith("P+")) {
        if (line.equals("P+00AL")) {
          // 残りの駒は全部先手の持ち駒
          for(int koma=Koma.FU;koma<=Koma.HI;koma++) {
            motigoma[0][koma]=restKoma[koma];
          }
        } else {
          // 先手の持ち駒
          for(int j=0;j<=line.length()-6;j+=4) {
            int koma=0;
            String komaStr=line.substring(j+2+2,j+2+4);
            for(int k=Koma.FU;k<=Koma.HI;k++) {
              if(komaStr.equals(csaKomaTbl[k])) {
                koma=k;
                break;
              }
            }
            motigoma[0][koma]++;
          }
        }
      } else if (line.startsWith("P-")) {
        if (line.equals("P-00AL")) {
          // 残りの駒は全部後手の持ち駒
          for(int koma=Koma.FU;koma<=Koma.HI;koma++) {
            motigoma[1][koma]=restKoma[koma];
          }
        } else {
          // 後手の持ち駒
          for(int j=0;j<line.length();j+=4) {
            int koma=0;
            for(int k=Koma.FU;k<=Koma.HI;k++) {
              if(line.substring(j+2,j+4).equals(csaKomaTbl[k])) {
                koma=k;
                break;
              }
            }
            motigoma[1][koma]++;
          }
        }
      } else if (line.startsWith("P")) {
        // 盤面の表現
        // P1～P9まで。
        String danStr=line.substring(1,2);
        int dan=0;
        try {
          dan=Integer.parseInt(danStr);
        } catch(Exception e) {
          // …握りつぶすことにしておく。
        }
        String komaStr;
        for(int suji=1;suji<=9;suji++) {
          // ややこしいが、左側が９筋、右側が１筋…
          // 文字列の頭の方が９筋で、後ろの方が１筋。
          // そのため、読み込みの時に逆さに読み込む。
          komaStr=line.substring(2+(9-suji)*3,2+(9-suji)*3+3);
          int koma=Koma.EMPTY;
          for(int k=Koma.EMPTY;k<=Koma.GRY;k++) {
            if (komaStr.equals(csaKomaTbl[k])) {
              koma=k;
              // 成のフラグを取って、残りの駒から
              // その種類の駒を一枚ひいておく。
              restKoma[(Koma.getKomashu(koma) & ~Koma.PROMOTE)]--;
              break;
            }
          }
          ban[suji][dan]=koma;
        }
      } else if (line.equals("-")) {
        teban=GOTE;
      } else if (line.equals("+")) {
        teban=SENTE;
      }
    }
    // 持ち駒をhandにしまう。
    for(int i=Koma.FU;i<Koma.HI;i++) {
      for(int j=0;j<motigoma[0][i];j++) {
        hand.get(0).add(Integer.valueOf(i|SENTE));
      }
      for(int j=0;j<motigoma[1][i];j++) {
        hand.get(1).add(Integer.valueOf(i|GOTE));
      }
    }
  }
  
  // 局面を表示用に文字列化
  public String toString() {
    String s="";
    // 後手持ち駒表示
    s+="後手持ち駒：";
    for(int i=0;i<hand.get(1).size();i++) {
      s+=Koma.toString(hand.get(1).get(i));
    }
    s+="\n";
    // 盤面表示
    s+=" ９　８　７　６　５　４　３　２　１\n";
    s+="+---+---+---+---+---+---+---+---+---+\n";
    for(int dan=1;dan<=9;dan++) {
      for(int suji=9;suji>=1;suji--) {
        s+="|";
        s+=Koma.toBanString(ban[suji][dan]);
      }
      s+="|";
      s+=danStr[dan];
      s+="\n";
      s+="+---+---+---+---+---+---+---+---+---+\n";
    }
    // 先手持ち駒表示
    s+="先手持ち駒：";
    for(int i=0;i<hand.get(0).size();i++) {
      s+=Koma.toString(hand.get(0).get(i));
    }
    s+="\n";
    return s;
  }
}

