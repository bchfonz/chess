package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard currentBoard = new ChessBoard();
    private Collection<ChessMove> legalMoves;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     * 
     * @param team the team whose turn it is
     * The function will change teamTurn from whatever color team is to the other color
     */
    public void setTeamTurn(TeamColor team) {
        if(team == TeamColor.WHITE){    
            teamTurn = TeamColor.BLACK;
        }else{
            teamTurn = TeamColor.WHITE;
        }
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
        ChessPiece curPiece = currentBoard.getPiece(startPosition);
        if(curPiece == null){
            return null;
        }
        legalMoves = curPiece.pieceMoves(currentBoard, startPosition);
        ChessBoard tempBoard = currentBoard;
        
        
        for(ChessMove curMove : legalMoves){
            if(deleteMove(tempBoard, curMove, curPiece)){
                legalMoves.remove(curMove);
            }
            
            //If deleteMove returns true, remove that move from the collection
        }
        return legalMoves;
    }
    public boolean deleteMove(ChessBoard tempBoard, ChessMove move, ChessPiece curPiece){
        boolean shouldDelete = false;
        Collection<ChessMove> tempPieceMoves = null;
        tempBoard.getBoard()[move.getStartPosition().getRow()][move.getStartPosition().getColumn()] = null;
        tempBoard.getBoard()[move.getEndPosition().getRow()][move.getEndPosition().getColumn()] = curPiece;
        //Loops to check the moves of each non-pawn enemy piece
        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++){
                if(tempBoard.getBoard()[row][col] != null 
                && tempBoard.getBoard()[row][col].getPieceType() != ChessPiece.PieceType.PAWN 
                && tempBoard.getBoard()[row][col].getTeamColor() != curPiece.getTeamColor()){
                    //Gets all the possile enemy moves
                    tempPieceMoves = tempBoard.getBoard()[row][col].pieceMoves(tempBoard, new ChessPosition(row, col));
                }
                for(ChessMove moves : tempPieceMoves){
                    if(tempBoard.getBoard()[moves.getEndPosition().getRow()][moves.getEndPosition().getColumn()].getPieceType() == ChessPiece.PieceType.KING){
                        shouldDelete = true;
                    }
                }
            }
        }
        
        return shouldDelete;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean isValid = false;
        for(ChessMove tempMove : legalMoves){
            if(move == tempMove){
                isValid = true;
            }
        }
        if(isValid){
            ChessPiece tempPiece = currentBoard.getBoard()[move.getStartPosition().getRow()][move.getStartPosition().getColumn()];
            currentBoard.getBoard()[move.getStartPosition().getRow()][move.getStartPosition().getColumn()] = null;
            currentBoard.getBoard()[move.getStartPosition().getRow()][move.getStartPosition().getColumn()] = tempPiece;
            setTeamTurn(teamTurn);
            isInCheck(teamTurn);
        }else{
            throw new InvalidMoveException("Invalid move");
        }  
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean isInCheck = false;
        ChessPosition kingPosition = null;
        ChessPiece enemyPiece;
        ChessPosition enemyPosition;
        //Finds king piece
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(currentBoard.getBoard()[i][j].getPieceType() == ChessPiece.PieceType.KING && currentBoard.getBoard()[i][j].getTeamColor() == teamColor){
                    kingPosition = new ChessPosition(i, j);
                }
                
            }
        }
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(currentBoard.getBoard()[i][j].getPieceType() != null && currentBoard.getBoard()[i][j].getTeamColor() != teamColor){
                    enemyPiece = currentBoard.getBoard()[i][j];
                    enemyPosition = new ChessPosition(i, j);
                    if(isInCheckHelper(kingPosition, enemyPosition, enemyPiece)){
                        isInCheck = true;
                    }

                }
                
            }
        }
        return isInCheck;
    }
    public boolean isInCheckHelper(ChessPosition kingPosition, ChessPosition enemyPosition, ChessPiece enemyPiece){
        boolean check = false;
        Collection<ChessMove> enemyMoves = enemyPiece.pieceMoves(currentBoard, enemyPosition);
        for(ChessMove moves : enemyMoves){
            if(moves.getEndPosition() == kingPosition){
                check = true;
            }
        }
        return check;
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
     * no valid moves
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
        currentBoard.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }
}
