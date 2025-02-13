package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8];
    public ChessBoard() {
        
    }
    public ChessBoard(ChessBoard copyBoard) {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(copyBoard.board[i][j] != null){
                    this.board[i][j] = copyBoard.board[i][j];
                }
                
            }
        }
    }

    public ChessPiece[][] getBoard() {
        return board;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                //Sets major white pieces
                if(i == 0){
                    //White Rooks
                    if(j == 0 || j == 7) {
                        ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                        ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                        addPiece(tempPosition, tempPiece);
                    }
                    //White Knights
                    if(j == 1 || j == 6) {
                        ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                        ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                        addPiece(tempPosition, tempPiece);
                    }
                    //White Bishops
                    if(j == 2 || j== 5) {
                        ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                        ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                        addPiece(tempPosition, tempPiece);
                    }
                    //White Queen
                    if(j == 3) {
                        ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                        ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                        addPiece(tempPosition, tempPiece);
                    }
                    //White King
                    if(j == 4) {
                        ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                        ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                        addPiece(tempPosition, tempPiece);
                    }

                }
                //Sets major Black pieces
                if(i == 7){
                    //Black Rooks
                    if(j == 0 || j == 7) {
                        ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                        ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                        addPiece(tempPosition, tempPiece);
                    }
                    //Black Knights
                    if(j == 1 || j == 6) {
                        ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                        ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                        addPiece(tempPosition, tempPiece);
                    }
                    //Black Bishops
                    if(j == 2 || j== 5) {
                        ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                        ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                        addPiece(tempPosition, tempPiece);
                    }
                    //Black Queen
                    if(j == 3) {
                        ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                        ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                        addPiece(tempPosition, tempPiece);
                    }
                    //Black King
                    if(j == 4) {
                        ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                        ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                        addPiece(tempPosition, tempPiece);
                    }

                }
                //Sets white pawns
                if(i == 1) {
                    ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                    ChessPosition tempPosition = new ChessPosition( i+ 1, j + 1);
                    addPiece(tempPosition, tempPiece);
                }
                //Sets black pawns
                if(i == 6) {
                    ChessPiece tempPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                    ChessPosition tempPosition = new ChessPosition(i + 1, j + 1);
                    addPiece(tempPosition, tempPiece);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(board);
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
        ChessBoard other = (ChessBoard) obj;
        if (!Arrays.deepEquals(board, other.board)){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ChessBoard [board=" + Arrays.toString(board) + "]";
    }

    
}
