package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard gameBoard = new ChessBoard();
    private TeamColor curPlayer = TeamColor.WHITE;
    private boolean gameOver = false;
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

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
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
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = gameBoard.getPiece(startPosition);
        if(myPiece == null){
            return null;
        }
        Collection<ChessMove> validMoves = myPiece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> possibleMoves = myPiece.pieceMoves(gameBoard, startPosition);
        for(ChessMove move : possibleMoves){
            ChessPosition moveStart = move.getStartPosition();
            ChessPosition moveEnd = move.getEndPosition();
            ChessPiece startPiece = gameBoard.getPiece(moveStart);
            ChessPiece endPiece = gameBoard.getPiece(moveEnd);
            //Simulate making the move
            gameBoard.addPiece(moveStart, null);
            gameBoard.addPiece(moveEnd, startPiece);
            if(isInCheck(myPiece.getTeamColor())){
                validMoves.remove(move);
            }
            //Returns board to the way it was before the move was made
            gameBoard.addPiece(moveStart, startPiece);
            gameBoard.addPiece(moveEnd, endPiece);
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        Collection<ChessMove> moves = validMoves(startPosition);
        ChessPiece startPiece = gameBoard.getPiece(startPosition);
        if(startPiece == null){
            throw new InvalidMoveException("Piece is null");
        }
        else if(!moves.contains(move)){
            throw new InvalidMoveException("Attempted move isn't in possible moves");
        }
        else if(startPiece.getTeamColor() != curPlayer){
            throw new InvalidMoveException("Wrong team");
        }
            if(move.getPromotionPiece() != null){
                gameBoard.addPiece(endPosition, new ChessPiece(curPlayer, move.getPromotionPiece()));
            }
            else{
                gameBoard.addPiece(endPosition, startPiece);
            }
            gameBoard.addPiece(startPosition, null);
            if(startPiece.getTeamColor() == TeamColor.WHITE){
                setTeamTurn(TeamColor.BLACK);
            }
            else{
                setTeamTurn(TeamColor.WHITE);
            }


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition curPosition = new ChessPosition(i,j);
                ChessPiece curPiece = gameBoard.getPiece(curPosition);
                if(curPiece != null){
                    if(curPiece.getPieceType() == ChessPiece.PieceType.KING && curPiece.getTeamColor() == teamColor){
                        kingPosition = curPosition;
                        break;
                    }
                }
            }
        }
        return isInCheckHelper(kingPosition, teamColor);

    }
    private boolean isInCheckHelper(ChessPosition kingPosition, TeamColor team) {
        boolean isInCheck = false;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition curPosition = new ChessPosition(i, j);
                ChessPiece curPiece = gameBoard.getPiece(curPosition);
                if (curPiece == null) {
                    continue;
                }
                if (curPiece.getTeamColor() == team) {
                    continue;
                }
                Collection<ChessMove> enemyMoves = curPiece.pieceMoves(gameBoard, curPosition);
                for (ChessMove moves : enemyMoves) {
                    if (moves.getEndPosition().getRow() == kingPosition.getRow() &&
                            moves.getEndPosition().getColumn() == kingPosition.getColumn()) {
                        isInCheck = true;
                        break;
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
        if(!isInCheck(teamColor)){
            return false;
        }
        return stalemateAndCheckmateHelper(teamColor);

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        return stalemateAndCheckmateHelper(teamColor);
    }
    private boolean stalemateAndCheckmateHelper(TeamColor teamColor){
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition curPosition = new ChessPosition(i,j);
                ChessPiece curPiece = gameBoard.getPiece(curPosition);
                if(curPiece != null && curPiece.getTeamColor() == teamColor){
                    ChessGame copyGame = new ChessGame(this);
                    Collection<ChessMove> pieceMoves = validMoves(curPosition);
                    if(pieceMoves != null && !pieceMoves.isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(gameBoard, chessGame.gameBoard) && curPlayer == chessGame.curPlayer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, curPlayer);
    }
}
