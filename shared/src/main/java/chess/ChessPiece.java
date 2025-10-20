package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }
    public ChessPiece(ChessPiece pieceCopy){
        this.pieceColor = pieceCopy.pieceColor;
        this.type = pieceCopy.type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> chessMoves = new ArrayList<>();
        int [][] directions;
        ChessPiece piece = board.getPiece(myPosition);
        if(piece.type == PieceType.BISHOP){
            directions =  new int[][]{
                {1,1},
                {1,-1},
                {-1,1},
                {-1,-1}
            };
        }
        else if(piece.type == PieceType.KNIGHT){
            directions =  new int[][]{
                    {2,1},
                    {2,-1},
                    {-2,1},
                    {-2,-1},
                    {1,2},
                    {1,-2},
                    {-1,2},
                    {-1,-2}
            };
        }
        else if(piece.type == PieceType.ROOK){
            directions =  new int[][]{
                    {1,0},
                    {-1,0},
                    {0,1},
                    {0,-1}
            };
        }
        else if(piece.type == PieceType.PAWN){
            //I need to account for capturing a piece
            if(piece.pieceColor == ChessGame.TeamColor.WHITE){
                if(myPosition.getRow() == 2){
                    directions = new int[][]{
                        {1,1},
                        {1,-1},
                        {1,0},
                        {2,0}
                    };
                }
                else{
                    directions = new int[][]{
                        {1,1},
                        {1,-1},
                        {1,0}
                    };
                }
            }
            else{
                if(myPosition.getRow() == 7){
                    directions = new int[][]{
                        {-1,1},
                        {-1,-1},
                        {-1,0},
                        {-2,0}
                    };
                }
                else{
                    directions = new int[][]{
                        {-1,1},
                        {-1,-1},
                        {-1,0}
                    };
                }
            }
        }
        else if(piece.type == PieceType.QUEEN || piece.type == PieceType.KING){
            directions =  new int[][]{
                    {1,1},
                    {1,-1},
                    {-1,1},
                    {-1,-1},
                    {1,0},
                    {-1,0},
                    {0,1},
                    {0,-1}
            };
        }
        else{
            directions = new int[][]{
                {0,0}
            };
        }
        if(piece.type == PieceType.BISHOP || piece.type == PieceType.ROOK || piece.type == PieceType.QUEEN){
            return allOtherMoves(board, myPosition, directions);
        }
        else if(piece.type == PieceType.KING || piece.type == PieceType.KNIGHT){
            return kingAndKnightMoves(board, myPosition, directions);
        }
        else if(piece.type == PieceType.PAWN){
            return pawnMoves(board, myPosition, directions);
        }
        return List.of();
    }


    private Collection<ChessMove> kingAndKnightMoves(ChessBoard board, ChessPosition myPosition, int[][] directions){
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        for(int[] direction : directions){
            int curRow = myPosition.getRow();
            int curCol = myPosition.getColumn();
            curRow += direction[0];
            curCol += direction[1];
            if(curRow <= 8 && curRow > 0 && curCol <= 8 && curCol > 0){
                ChessPosition curPosition = new ChessPosition(curRow, curCol);
                ChessPiece curSquare = board.getPiece(curPosition);
                if(curSquare == null){
                    validMoves.add(new ChessMove(myPosition, curPosition, null));
                }
                else if(curSquare.pieceColor != myPiece.pieceColor){
                    validMoves.add(new ChessMove(myPosition, curPosition, null));
                }
            }

        }
        return validMoves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, int[][] directions){
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        for(int[] direction : directions) {
            int curRow = myPosition.getRow();
            int curCol = myPosition.getColumn();
            curRow += direction[0];
            curCol += direction[1];
            if(curRow <= 8 && curRow > 0 && curCol <= 8 && curCol > 0){
                ChessPosition curPosition = new ChessPosition(curRow, curCol);
                ChessPiece curPiece = board.getPiece(curPosition);
                if(curCol == myPosition.getColumn() && curPiece == null){
                    if(curRow != 8 && curRow != 1){
                        validMoves.add(new ChessMove(myPosition, curPosition, null));
                    }
                    else{
                        validMoves.add(new ChessMove(myPosition, curPosition, PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, curPosition, PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, curPosition, PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, curPosition, PieceType.QUEEN));
                    }
                }
                else if(curCol == myPosition.getColumn() && curPiece != null){
                    break;
                }
                else if(curCol != myPosition.getColumn() && curPiece != null){
                    if(myPiece.pieceColor != curPiece.pieceColor){
                        if(curRow != 8 && curRow != 1){
                            validMoves.add(new ChessMove(myPosition, curPosition, null));
                        }
                        else{
                            validMoves.add(new ChessMove(myPosition, curPosition, PieceType.BISHOP));
                            validMoves.add(new ChessMove(myPosition, curPosition, PieceType.KNIGHT));
                            validMoves.add(new ChessMove(myPosition, curPosition, PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, curPosition, PieceType.QUEEN));
                        }
                    }

                }
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> allOtherMoves(ChessBoard board, ChessPosition myPosition, int[][] directions){
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        for(int[] direction : directions){
            int curRow = myPosition.getRow();
            int curCol = myPosition.getColumn();
            curRow += direction[0];
            curCol += direction[1];
            while(curRow <= 8 && curRow > 0 && curCol <= 8 && curCol > 0){
                ChessPosition curPosition = new ChessPosition(curRow, curCol);
                ChessPiece curSquare = board.getPiece(curPosition);
                if(curSquare == null){
                    validMoves.add(new ChessMove(myPosition, curPosition, null));
                }
                else if(curSquare.pieceColor != myPiece.pieceColor){
                    validMoves.add(new ChessMove(myPosition, curPosition, null));
                    break;
                }
                else if(curSquare.pieceColor == myPiece.pieceColor){
                    break;
                }
                curRow += direction[0];
                curCol += direction[1];
            }
        }
        return validMoves;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
