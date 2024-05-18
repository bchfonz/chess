package chess;

public class Move implements ChessMove{
  ChessPosition startPosition;
  ChessPosition endPosition;

  public Move(){

  }

  @Override
  public ChessPosition getStartPosition() {
    return startPosition;
  }

  @Override
  public ChessPosition getEndPosition() {
    return endPosition;
  }

  @Override
  public ChessPiece.PieceType getPromotionPiece() {
    return null;
  }
}
