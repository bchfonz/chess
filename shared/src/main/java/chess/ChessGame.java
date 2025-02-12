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
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard currentBoard = new ChessBoard();
    private Collection<ChessMove> legalMoves = new ArrayList<>();
    public ChessGame() {
        currentBoard.resetBoard();
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
        teamTurn = team;
        
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
        Collection<ChessMove> newLegalMoves = new ArrayList<>();
        ChessPiece curPiece = new ChessPiece(null, null);
        curPiece = currentBoard.getPiece(startPosition);
        System.out.println("curPiece: " + curPiece);
        legalMoves = curPiece.pieceMoves(currentBoard, startPosition);
        
        
        
        for(ChessMove curMove : legalMoves){
            ChessBoard tempBoard = new ChessBoard(currentBoard);
            if(!deleteMove(tempBoard, curMove, curPiece)){
                newLegalMoves.add(curMove);
            }
        }
        return newLegalMoves;
    }

    public boolean deleteMove(ChessBoard tempBoard, ChessMove move, ChessPiece curPiece){
        boolean shouldDelete = false;
        ChessPiece tempPiece = new ChessPiece(null, null);
        Collection<ChessMove> tempPieceMoves = new ArrayList<>();
        tempBoard.getBoard()[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = null;
        tempBoard.getBoard()[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = curPiece;
        //Loops to check the moves of each major enemy piece
        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++){
                if(tempBoard.getBoard()[row][col] != null 
                && tempBoard.getBoard()[row][col].getTeamColor() != curPiece.getTeamColor()){
                    //Gets all the possile enemy moves
                    ChessPosition tempPosition = new ChessPosition(row + 1, col + 1);
                    tempPiece = tempBoard.getBoard()[row][col];
                    tempPieceMoves = tempPiece.pieceMoves(tempBoard, tempPosition);
                    for(ChessMove moves : tempPieceMoves){
                        if(tempBoard.getBoard()[moves.getEndPosition().getRow() - 1][moves.getEndPosition().getColumn() - 1] == null){
                            continue;
                        }
                        else if(tempBoard.getBoard()[moves.getEndPosition().getRow() - 1][moves.getEndPosition().getColumn() - 1].getPieceType() == ChessPiece.PieceType.KING){
                            
                            shouldDelete = true;
                            System.out.println("Checking if I need to delete move. shouldDelete = " + shouldDelete);
                        }
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
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece tempPiece = currentBoard.getPiece(startPosition); 
        if(tempPiece == null || tempPiece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("Out of turn");
        }
        
        legalMoves = validMoves(move.getStartPosition());
        System.out.println("?");
        if(!legalMoves.contains(move)){
            throw new InvalidMoveException("Invalid move");
        }
        
        currentBoard.getBoard()[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = null;
        if(move.getPromotionPiece() != null){
            currentBoard.addPiece(endPosition, new ChessPiece(teamTurn, move.getPromotionPiece()));
        }else{
            currentBoard.addPiece(endPosition, tempPiece);
        }

        // currentBoard.getBoard()[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = tempPiece;
        
        System.out.println("Move made. Start Postition (" + move.getStartPosition().getRow() + ", " + move.getStartPosition().getColumn() + ") = " + currentBoard.getBoard()[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1]);
        System.out.println("End Postition (" + move.getEndPosition().getRow() + ", " + move.getEndPosition().getColumn() + ") = " + currentBoard.getBoard()[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1]);
        if(teamTurn == TeamColor.WHITE){    
            teamTurn = TeamColor.BLACK;
        }else{
            teamTurn = TeamColor.WHITE;
        }
        // isInCheck(teamTurn);

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
                if(currentBoard.getPiece(new ChessPosition(i + 1, j + 1)) != null){
                    if(currentBoard.getBoard()[i][j].getPieceType() == ChessPiece.PieceType.KING && currentBoard.getBoard()[i][j].getTeamColor() == teamColor){
                        kingPosition = new ChessPosition(i + 1, j + 1);
                        System.out.println("King position: (" + kingPosition.getRow() + ", " + kingPosition.getColumn() + ")");
                    }
                }
                
                
            }
        }
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(currentBoard.getBoard()[i][j] != null && currentBoard.getBoard()[i][j].getTeamColor() != teamColor){
                        enemyPiece = currentBoard.getBoard()[i][j];
                        enemyPosition = new ChessPosition(i + 1, j + 1);
                        System.out.println("Enemy position: (" + enemyPosition.getRow() + ", " + enemyPosition.getColumn() + ")");
                        if(isInCheckHelper(kingPosition, enemyPosition, enemyPiece)){
                            System.out.println("Is in check");
                            isInCheck = true;
                        }
    
                }
                
                
            }
        }
        return isInCheck;
    }
    public boolean isInCheckHelper(ChessPosition kingPosition, ChessPosition enemyPosition, ChessPiece enemyPiece){
        System.out.println("In check helper. Enemy piece = " + enemyPiece);
        boolean check = false;
        // Collection<ChessMove> enemyMoves = new ArrayList<>();
        // enemyMoves = enemyPiece.pieceMoves(currentBoard, enemyPosition);
        for(ChessMove moves : enemyPiece.pieceMoves(currentBoard, enemyPosition)){
            System.out.println("King position: (" + kingPosition.getRow() + ", " + kingPosition.getColumn() + ")");
            System.out.println("moves.getEndPosition() = (" + moves.getEndPosition().getRow() + ", " + moves.getEndPosition().getColumn() + ")");
            if(moves.getEndPosition().getRow() == kingPosition.getRow() && moves.getEndPosition().getColumn() == kingPosition.getColumn()){
                System.out.println("Is in check from check helper");
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
        System.out.println("In setBoard");
        this.currentBoard = board;
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
