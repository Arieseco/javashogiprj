package jp.usapyonsoft.lesserpyon;

// 駒の位置を表すクラス
class Position implements Cloneable,KomaMoves {
  // 筋
  public int suji;
  // 段
  public int dan;
  
  // コンストラクタ
  public Position() {
    suji=0;
    dan=0;
  }
  
  // コンストラクタ
  public Position(int _suji,int _dan) {
    suji=_suji;
    dan=_dan;
  }
  
  // 同一性比較用メソッド
  public boolean equals(Position p) {
    return (p.suji==suji && p.dan==dan);
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Position)) return false;
    Position p = (Position) o;
    return equals(p);
  }
  
  // コピーを返す
  @Override
  public Object clone() {
    return new Position(suji,dan);
  }

  @Override
  public int hashCode() {
    return suji * 31 + dan;
  }
  
  // ある方向への動きを行う
  public void add(int diffSuji,int diffDan) {
    suji+=diffSuji;
    dan+=diffDan;
  }
  
  // ある方向への逆向きの動きを行う
  public void sub(int diffSuji,int diffDan) {
    suji-=diffSuji;
    dan-=diffDan;
  }
  
  // ある方向への動きを行う
  public void add(int direct) {
    add(diffSuji[direct],diffDan[direct]);
  }
  
  // ある方向への逆向きの動きを行う
  public void sub(int direct) {
    sub(diffSuji[direct],diffDan[direct]);
  }
}

