package chess;

import java.util.Collection;

public class Piece implements ChessPiece{
  @Override
  public ChessGame.TeamColor getTeamColor() {
    return null;
  }

  @Override
  public PieceType getPieceType() {
    return null;
  }

  @Override
  public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    return null;
  }
}
