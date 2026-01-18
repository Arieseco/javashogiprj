package jp.usapyonsoft.lesserpyon;

public class Te implements Cloneable,Constants {
  int koma;                 // どの駒が動いたか
  Position from;            // 動く前の位置（持ち駒の場合、０筋０段）
  Position to;              // 動いた先の位置
  boolean promote;          // 成る場合、true 成らない場合 false
  
  public Te(int _koma,Position _from,Position _to,boolean _promote) {
    koma=_koma;
    from=(Position)_from.clone();
    to=  (Position)_to.clone();
    promote=_promote;
  }
  
  public boolean equals(Te te) {
    return (te.koma==koma && te.from.equals(from) && te.to.equals(to) && te.promote==promote);
  }
  
  public boolean equals(Object _te) {
    Te te=(Te)_te;
    if (te==null) return false;
    return equals(te);
  }
  
  public Object clone() {
    return new Te(koma,from,to,promote);
  }
  
  // 手を文字列で表現する。
  public String toString() {
    return sujiStr[to.suji]+danStr[to.dan]+
            Koma.toString(koma)+(promote?"成":"")+
            (from.suji==0?"打　　":"("+sujiStr[from.suji]+danStr[from.dan]+")")+
            (promote?"":"　");
  }
}
