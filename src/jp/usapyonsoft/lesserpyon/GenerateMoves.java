package jp.usapyonsoft.lesserpyon;
import java.util.Vector;

public class GenerateMoves implements Constants,KomaMoves {

  // 各手について、自分の玉に王手がかかっていないかどうかチェックし、
  // 王手がかかっている手は取り除く。
  public static Vector removeSelfMate(Kyokumen k,Vector v) {
    Vector removed=new Vector();
    for(int i=0;i<v.size();i++) {
      // 手を取り出す。
      Te te=(Te)v.elementAt(i);

      // その手で１手進めてみる
      Kyokumen test=(Kyokumen)k.clone();
      test.move(te);

      // 自玉を探す
      Position gyokuPosition=test.searchGyoku(k.teban);

      // 王手放置しているかどうかフラグ
      boolean isOuteHouchi=false;

      // 玉の周辺（１２方向）から相手の駒が利いていたら、その手は取り除く
      for(int direct=0;direct<12 && !isOuteHouchi;direct++) {
        // 方向の反対方向にある駒を取得
        Position pos=(Position)gyokuPosition.clone();
        pos.sub(direct);
        int koma=test.get(pos);
        // その駒が敵の駒で、玉方向に動けるか？
        if (Koma.isEnemy(test.teban,koma) && canMove[direct][koma]) {
          // 動けるなら、この手は王手を放置しているので、
          // この手は、removedに追加しない。
          isOuteHouchi=true;
          break;
        }
      }
  
      // 玉の周り（８方向）から相手の駒の飛び利きがあるなら、その手は取り除く
      for(int direct=0;direct<8 && !isOuteHouchi;direct++) {
        // 方向の反対方向にある駒を取得
        Position pos=(Position)gyokuPosition.clone();
        int koma;
        // その方向にマスが空いている限り、駒を探す
        for(pos.sub(direct),koma=test.get(pos);
         koma!=Koma.WALL;pos.sub(direct),koma=test.get(pos)) {
          // 味方駒で利きが遮られているなら、チェック終わり。
          if (Koma.isSelf(test.teban,koma)) break;
          // 遮られていない相手の駒の利きがあるなら、王手がかかっている。
          if (Koma.isEnemy(test.teban,koma) && canJump[direct][koma]) {
            isOuteHouchi=true;
            break;
          }
          // 敵駒で利きが遮られているから、チェック終わり。
          if (Koma.isEnemy(test.teban,koma)) {
            break;
          }
        }
      }
      if (!isOuteHouchi) {
        removed.add(te);
      }
    }
    return removed;
  }
  
  // 与えられたVectorに、手番、駒の種類、移動元、移動先を考慮して、
  // 成る・不成りを判断しながら生成した手を追加する。
  public static void addTe(Vector v,int teban,int koma,Position from,Position to) {
    if (teban==SENTE) {
      // 先手番
      if ((Koma.getKomashu(koma)==Koma.KY || Koma.getKomashu(koma)==Koma.FU) && to.dan==1) {
        // 香車か歩が１段目に進むときには、成ることしか選べない。
        Te te=new Te(koma,from,to,true);
        v.add(te);
      } else if (Koma.getKomashu(koma)==Koma.KE && to.dan<=2) {
        // 桂馬が２段目以上に進む時には、成ることしか選べない。
        Te te=new Te(koma,from,to,true);
        v.add(te);
      } else if ((to.dan<=3 || from.dan<=3) && Koma.canPromote(koma)) {
        // 駒の居た位置が相手陣か、進む位置が相手陣で、
        // 駒が成ることが出来るなら
        // 成りと不成りの両方の手を生成
        Te te=new Te(koma,from,to,true);
        v.add(te);
        te=new Te(koma,from,to,false);
        v.add(te);
      } else {
        // 不成りの手のみ生成
        Te te=new Te(koma,from,to,false);
        v.add(te);
      }
    } else {
      // 後手番
      if ((Koma.getKomashu(koma)==Koma.KY || Koma.getKomashu(koma)==Koma.FU) && to.dan==9) {
        // 香車か歩が九段目に進むときには、成ることしか選べない。
        Te te=new Te(koma,from,to,true);
        v.add(te);
      } else if (Koma.getKomashu(koma)==Koma.KE && to.dan>=8) {
        // 桂馬が八段目以上に進む時には、成ることしか選べない。
        Te te=new Te(koma,from,to,true);
        v.add(te);
      } else if ((to.dan>=7 || from.dan>=7) && Koma.canPromote(koma)) {
        // 駒の居た位置が相手陣か、進む位置が相手陣で、
        // 駒が成ることが出来るなら
        // 成りと不成りの両方の手を生成
        Te te=new Te(koma,from,to,true);
        v.add(te);
        te=new Te(koma,from,to,false);
        v.add(te);
      } else {
        // 不成りの手のみ生成
        Te te=new Te(koma,from,to,false);
        v.add(te);
      }
    }
  }
  
  // 打ち歩詰めになっていないかどうかチェックする関数
  // 相手の玉頭に歩を打つ場合、
  // その手で一手進めてみて、相手の手番でGenerateLegalMoveを行い、
  // 帰ってくる手がなかったなら打ち歩詰めになっている。
  public static boolean isUtiFuDume(Kyokumen k,Te te) {
    if (te.from.suji!=0 && te.from.dan!=0) {
      // 駒を打つ手ではないので、打ち歩詰めではない。
      return false;
    }
    if (Koma.getKomashu(te.koma)!=Koma.FU) {
      // 歩を打つ手ではないので、打ち歩詰めではない。
      return false;
    }
    int teban;
    int tebanAite;
    if ((te.koma&SENTE)!=0) {
      // 先手の歩を打つから、自分の手番は先手、相手の手番は後手
      teban=SENTE;
      tebanAite=GOTE;
    } else {
      // そうでない時は、自分の手番は後手、相手の手番は先手
      teban=GOTE;
      tebanAite=SENTE;
    }
    Position gyokuPositionAite=k.searchGyoku(tebanAite);
    if (teban==SENTE) {
      if (gyokuPositionAite.suji!=te.to.suji || gyokuPositionAite.dan!=te.to.dan-1) {
        // 相手の玉の頭に歩を打つ手ではないので、打ち歩詰めになっていない。
        return false;
      }
    } else {
      if (gyokuPositionAite.suji!=te.to.suji || gyokuPositionAite.dan!=te.to.dan+1) {
        // 相手の玉の頭に歩を打つ手ではないので、打ち歩詰めになっていない。
        return false;
      }
    }
    // 実際に一手進めてみる…。
    Kyokumen test=(Kyokumen)k.clone();
    test.move(te);
    test.teban=tebanAite;
    // その局面で、相手に合法手があるか？なければ、打ち歩詰め。
    Vector v=generateLegalMoves(test);
    if (v.size()==0) {
      // 合法手がないので、打ち歩詰め。
      return true;
    }
    return false;
  }
  
  // 与えられた局面における合法手を生成する。
  public static Vector generateLegalMoves(Kyokumen k) {
    Vector v=new Vector();

    // 盤上の手番の側の駒を動かす手を生成
    for(int suji=1;suji<=9;suji++) {
      for(int dan=1;dan<=9;dan++) {
        Position from=new Position(suji,dan);
        int koma=k.get(from);
        // 自分の駒であるかどうか確認
        if (Koma.isSelf(k.teban,koma)) {
          // 各方向に移動する手を生成
          for(int direct=0;direct<12;direct++) {
            if (canMove[direct][koma]) {
              // 移動先を生成
              Position to=new Position(suji+diffSuji[direct],dan+diffDan[direct]);
              // 移動先は盤内か？
              if (1<=to.suji && to.suji<=9 && 1<=to.dan && to.dan<=9) {
                // 移動先に自分の駒がないか？
                if (Koma.isSelf(k.teban,k.get(to))) {
                  // 自分の駒だったら、次の方向を検討
                  continue;
                }
                // 成る・不成りを考慮しながら、手をvに追加
                addTe(v,k.teban,koma,from,to);
              }
            }
          }
          // 各方向に「飛ぶ」手を生成
          for(int direct=0;direct<8;direct++) {
            if (canJump[direct][koma]) {
              // そちら方向に飛ぶことが出来る
              for(int i=1;i<9;i++) {
                // 移動先を生成
                Position to=new Position(suji+diffSuji[direct]*i,dan+diffDan[direct]*i);
                // 行き先が盤外だったら、そこには行けない
                if (k.get(to)==Koma.WALL) break;
                // 行き先に自分の駒があったら、そこには行けない
                if (Koma.isSelf(k.teban,k.get(to))) break;
                // 成る・不成りを考慮しながら、手をvに追加
                addTe(v,k.teban,koma,from,to);
                // 空き升でなければ、ここで終わり
                if (k.get(to)!=Koma.EMPTY) break;
              }
            }
          }
        }
      }
    }
    
    
    // 手番の側の駒を打つ手を生成

    // 手番の側の持ち駒で、その駒を既に打ったかどうかチェックするための配列
    // 何もなし、歩～飛車まで
    boolean isPutted[]={false,false,false,false,false,false,false,false};

    // 手番の側の持ち駒
    Vector motigoma;
    if (k.teban==SENTE) {
      motigoma=k.hand[0];
    } else {
      motigoma=k.hand[1];
    }
    
    // まず、手番の側の持ち駒でループ
    for(int i=0;i<motigoma.size();i++) {
      // 持ち駒を一つ取り出す
      int koma=((Integer)motigoma.elementAt(i)).intValue();
      // 駒の種類を得る
      int komashu=Koma.getKomashu(koma);
      if (isPutted[komashu]) {
        // 既にその駒を打ったことがあるなら、同じ駒を打つ手を生成するのは
        // 無駄になるので、行わない。
        continue;
      }
      // この駒を打ったことがある、と印を付ける
      isPutted[komashu]=true;
      // 盤面の各升目でループ
      for(int suji=1;suji<=9;suji++) {
        // 二歩にならないかどうかチェック
        if (komashu==Koma.FU) {
          // 二歩のチェック用変数
          boolean isNifu=false;
          // 二歩チェック
          // 同じ筋に、手番の側の歩がいないことを確認する
          for(int dan=1;dan<=9;dan++) {
            Position p=new Position(suji,dan);
            // 手番の側の歩が、同じ筋にいないかどうかチェックする
            if (k.get(p)==(k.teban|Koma.FU)) {
              // 二歩になっている。
              isNifu=true;
              break;
            }
          }
          if (isNifu) {
            // 二歩になっているので、打つ手を生成しない。
            // 次の筋へ
            continue;
          }
        }
        for(int dan=1;dan<=9;dan++) {
          // 駒が桂馬の場合の扱い
          if (komashu==Koma.KE) {
            if (k.teban==SENTE && dan<=2) {
              // 先手なら、二段目より上に桂馬は打てない
              continue;
            } else if (k.teban==GOTE && dan>=8) {
              // 後手なら、八段目より下に桂馬は打てない
              continue;
            }
          }
          // 駒が歩、または香車の場合の扱い
          if (komashu==Koma.FU || komashu==Koma.KY) {
            if (k.teban==SENTE && dan==1) {
              // 先手なら、一段目に歩と香車は打てない
              continue;
            } else if (k.teban==GOTE && dan==9) {
              // 後手なら、九段目に歩と香車は打てない
              continue;
            }
          }
          // 移動元…駒を打つ手は、0,0
          Position from=new Position(0,0);
          // 移動先、駒を打つ場所
          Position to=new Position(suji,dan);
  
          // 空き升でなければ、打つ事は出来ない。
          if (k.get(to)!=Koma.EMPTY) {
            continue;
          }
          // 手の生成…駒を打つ際には、常に不成である。
          Te te=new Te(koma,from,to,false);
          // 打ち歩詰めの特殊扱い
          if (isUtiFuDume(k,te)) {
            // 打ち歩詰めなら、そこに歩は打てない
            continue;
          }
          // 駒を打つ手が可能なことが分かったので、合法手に加える。
          v.add(te);
        }
      }
    }
    
    // 生成した各手について、指してみて
    // 自分の玉に王手がかかっていないかどうかチェックし、
    // 王手がかかっている手は取り除く。
    v=removeSelfMate(k,v);

    return v;
  }
}

