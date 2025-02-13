package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        Collection<ChessMove> legalMoves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        //Stores the possible directions a piece can move
        int[][] directions;
        // Need to delete all these variables. Just make new position objects as needed
        // as I loop through
        if (board.getPiece(myPosition).type == ChessPiece.PieceType.ROOK) {
            directions = new int[][] {
                    { 1, 0 },
                    { 0, 1 },
                    { -1, 0 },
                    { 0, -1 }
            };
        } else if (board.getPiece(myPosition).type == ChessPiece.PieceType.KNIGHT) {
            directions = new int[][] {
                    { 2, 1 },
                    { 2, -1 },
                    { -2, 1 },
                    { -2, -1 },
                    { 1, 2 },
                    { 1, -2 },
                    { -1, 2 },
                    { -1, -2 }
            };

        } else if (board.getPiece(myPosition).type == ChessPiece.PieceType.BISHOP) {
            directions = new int[][] {
                    { 1, 1 },
                    { 1, -1 },
                    { -1, 1 },
                    { -1, -1 }
            };

        } else if (board.getPiece(myPosition).type == ChessPiece.PieceType.QUEEN) {
            directions = new int[][] {
                    { 1, 0 },
                    { 0, 1 },
                    { -1, 0 },
                    { 0, -1 },
                    { 1, 1 },
                    { 1, -1 },
                    { -1, 1 },
                    { -1, -1 }
            };
        } else if (board.getPiece(myPosition).type == ChessPiece.PieceType.KING) {
            directions = new int[][] {
                    { 1, 0 },
                    { 0, 1 },
                    { -1, 0 },
                    { 0, -1 },
                    { 1, 1 },
                    { 1, -1 },
                    { -1, 1 },
                    { -1, -1 }
            };
            
        }else if (board.getPiece(myPosition).type == ChessPiece.PieceType.PAWN) {
            if(board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE) {
                if(myPosition.getRow() == 2) {
                    directions = new int [][]{
                        {1,1},
                        {1,-1},
                        {1,0},
                        {2,0}
                    };
                }else{
                    directions = new int [][]{
                        {1,1},
                        {1,-1},
                        {1,0}
                    };
                }

            } else{
                if(myPosition.getRow() == 7) {
                    directions = new int [][]{
                        {-1,1},
                        {-1,-1},
                        {-1,0},
                        {-2,0}
                    };
                }else{
                    directions = new int [][]{
                        {-1,1},
                        {-1,-1},
                        {-1,0}
                    };
                }
            }
            for(int[] direction : directions){
                int row = startRow + direction[0];
                int col = startCol + direction[1];
                if(row > 8 || col > 8 || row < 1 || col < 1) {
                    continue;
                }
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                //Moving straight ahead
                if(col == startCol && pieceAtNewPosition == null) {
                    if(newPosition.getRow() == 8 || newPosition.getRow() == 1) {
                        legalMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                        legalMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                        legalMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                        legalMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    }else{
                        legalMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }else if(col == startCol && pieceAtNewPosition != null && (startRow == 2 || startRow == 7)) {
                    break;
                }else if(col != startCol && pieceAtNewPosition != null && pieceAtNewPosition.pieceColor != board.getPiece(myPosition).pieceColor) {
                    if(newPosition.getRow() == 8 || newPosition.getRow() == 1) {
                        legalMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                        legalMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                        legalMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                        legalMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    }else {
                        legalMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
            
        }else {
            directions = new int[][] {
                    { 0, 1 },
                    { 1, 1 },
                    { -1, 1 }
            };
        }
        if (board.getPiece(myPosition).type != ChessPiece.PieceType.KING 
        && board.getPiece(myPosition).type != ChessPiece.PieceType.PAWN 
        && board.getPiece(myPosition).type != ChessPiece.PieceType.KNIGHT) {
            // Loops through every possible direction
            for (int[] direction : directions) {
                int row = startRow + direction[0];
                int col = startCol + direction[1];
                // Loops in the given direction until you hit the edge of the board, or you hit
                // another piece
                while (row <= 8 && row >= 1 && col <= 8 && col >= 1) {
                    ChessPosition newPosition = new ChessPosition(row, col);
                    ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                    // Checks that for piece at newPosition
                    if (pieceAtNewPosition == null) {
                        // If piece is null, add it to the collection
                        legalMoves.add(new ChessMove(myPosition, newPosition, null));

                    } else {
                        // If piece is not null, check which team piece is on. If on differnt team, add
                        // move to collection
                        if (pieceAtNewPosition.pieceColor != board.getPiece(myPosition).pieceColor) {
                            legalMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                        break;
                    }
                    // Iterates further in the given direction
                    row += direction[0];
                    col += direction[1];
                }
            }
        }


        if(board.getPiece(myPosition).type == ChessPiece.PieceType.KING || board.getPiece(myPosition).type == ChessPiece.PieceType.KNIGHT) {
            for(int[] direction : directions) {
                int row = startRow + direction[0];
                int col = startCol + direction[1];
                if(row > 8 || col > 8 || row < 1 || col < 1) {
                    continue;
                }else {
                    ChessPosition newPosition = new ChessPosition(row, col);
                    ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                    if (pieceAtNewPosition == null) {
                        // If piece is null, add it to the collection
                        legalMoves.add(new ChessMove(myPosition, newPosition, null));

                    } 
                    else {
                        // If piece is not null, check which team piece is on. If on differnt team, add
                        // move to collection
                        if (pieceAtNewPosition.pieceColor != board.getPiece(myPosition).pieceColor) {
                            legalMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
                
            }
        }
        

        return legalMoves;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pieceColor == null) ? 0 : pieceColor.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        ChessPiece other = (ChessPiece) obj;
        if (pieceColor != other.pieceColor){
            return false;
        }
        if (type != other.type){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ChessPiece [pieceColor=" + pieceColor + ", type=" + type + "]";
    }
}
