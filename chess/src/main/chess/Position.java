package chess;

public class Position implements ChessPosition{
  private int row;
  private int column;
  public Position(){
    row = 0;
    column = 0;
  }
  @Override
  public int getRow() {
    return row;
  }

  @Override
  public int getColumn() {
    return column;
  }
}
