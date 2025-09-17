package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard gameBoard = new ChessBoard();
    private TeamColor curPlayer = TeamColor.WHITE;
    public ChessGame() {
        gameBoard.resetBoard();
    }
    public ChessGame(ChessGame copy){
        this.gameBoard = new ChessBoard(copy.gameBoard);
        this.curPlayer = copy.curPlayer;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return curPlayer;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        curPlayer = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    /*
        How to implement:
        1. Copy the board
        2. Make the move on the copied board
        3. Check for chekcmate
        4. Update the moves list
                */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> updatedMoves = new ArrayList<>();
        ChessPiece myPiece = gameBoard.getPiece(startPosition);
        if(myPiece == null){
            return null;
        }
        Collection<ChessMove> possibleMoves = myPiece.pieceMoves(gameBoard, startPosition);
        ChessBoard copyBoard = new ChessBoard(gameBoard);
        try{
            for(ChessMove move : possibleMoves){
                makeMove(move);
            }
        } catch (InvalidMoveException e) {
//            throw new RuntimeException(e);
        }

        return updatedMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        /*
        Find the King's location.
        Loop through all the other possible moves and see if any of the possible moves of the other team include the location of the king
        */
        Collection<ChessPiece> enemyPieces = new ArrayList<>();
        boolean isInCheck;
        ChessPosition kingPosition = null;
        ChessPiece kingPiece;
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition curPosition = new ChessPosition(i,j);
                ChessPiece curPiece = gameBoard.getPiece(curPosition);
                if(curPiece != null){
                    if(curPiece.getPieceType() == ChessPiece.PieceType.KING && curPiece.getTeamColor() == teamColor){
                        kingPosition = curPosition;
                        kingPiece = curPiece;
                        break;
                    }
                }
            }
        }
        return isInCheckHelper(kingPosition, teamColor);

    }
    private boolean isInCheckHelper(ChessPosition kingPosition, TeamColor team){
        boolean isInCheck = false;
        for(int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition curPosition = new ChessPosition(i,j);
                ChessPiece curPiece = gameBoard.getPiece(curPosition);
                if(curPiece != null){
                    if(curPiece.getTeamColor() != team){
                        Collection<ChessMove> enemyMoves = curPiece.pieceMoves(gameBoard, curPosition);
                        for(ChessMove moves : enemyMoves){
                            if(moves.getEndPosition().getRow() == kingPosition.getRow() && moves.getEndPosition().getColumn() == kingPosition.getColumn()){
                                isInCheck = true;
                                break;
                            }
                        }

                    }
                }

            }
        }

        return isInCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
