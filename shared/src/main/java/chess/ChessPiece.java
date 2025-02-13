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

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

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
        int[][] directions;

        if (board.getPiece(myPosition).type == ChessPiece.PieceType.ROOK) {
            directions = new int[][] {
                    { 1, 0 },
                    { -1, 0 },
                    { 0, 1 },
                    { 0, -1 }
            };
        } else if (board.getPiece(myPosition).type == ChessPiece.PieceType.KNIGHT) {
            directions = new int[][] {
                    { 2, 1 },
                    { 2, -1 },
                    { -2, 1 },
                    { -2, -1 },
                    { 1, 2 },
                    { -1, 2 },
                    { 1, -2 },
                    { -1, -2 }
            };
        } else if (board.getPiece(myPosition).type == ChessPiece.PieceType.BISHOP) {
            directions = new int[][] {
                    { 1, 1 },
                    { -1, 1 },
                    { 1, -1 },
                    { -1, -1 }
            };
        } else if (board.getPiece(myPosition).type == ChessPiece.PieceType.QUEEN 
        || board.getPiece(myPosition).type == ChessPiece.PieceType.KING) {
            directions = new int[][] {
                    { 1, 1 },
                    { -1, 1 },
                    { 1, -1 },
                    { -1, -1 },
                    { 1, 0 },
                    { -1, 0 },
                    { 0, 1 },
                    { 0, -1 }
            };
        } else if (board.getPiece(myPosition).type == ChessPiece.PieceType.PAWN) {
            if (board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE) {
                if (myPosition.getRow() == 2) {
                    directions = new int[][] {
                            { 1, 1 },
                            { 1, -1 },
                            { 1, 0 },
                            { 2, 0 }
                    };
                } else {
                    directions = new int[][] {
                            { 1, 1 },
                            { 1, -1 },
                            { 1, 0 }
                    };
                }
            } else if (board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK) {
                if (myPosition.getRow() == 7) {
                    directions = new int[][] {
                            { -1, 1 },
                            { -1, -1 },
                            { -1, 0 },
                            { -2, 0 }
                    };
                } else {
                    directions = new int[][] {
                            { -1, 1 },
                            { -1, -1 },
                            { -1, 0 }
                    };
                }

            } else {
                directions = new int[][] {
                        { 0, 0 }
                };
            }
        } else {
            directions = new int[][] {
                    { 0, 0 }
            };
        }
        if (board.getPiece(myPosition).type != ChessPiece.PieceType.KING
                && board.getPiece(myPosition).type != ChessPiece.PieceType.KNIGHT 
                && board.getPiece(myPosition).type != ChessPiece.PieceType.PAWN) {
                    legalMoves = allOtherMoves(board, directions, myPosition, myPosition);
            
        } else if (board.getPiece(myPosition).type == ChessPiece.PieceType.PAWN) {
            legalMoves = pawnMoves(board, directions, myPosition);
        } else if (board.getPiece(myPosition).type == ChessPiece.PieceType.KING
                || board.getPiece(myPosition).type == ChessPiece.PieceType.KNIGHT) {
            legalMoves = kingAndKnightMoves(board, directions, myPosition);
        }

        return legalMoves;
    }

    public Collection<ChessMove> allOtherMoves(ChessBoard board, int[][] directions, ChessPosition myPosition, ChessPosition startPosition){
        Collection<ChessMove> legalMoves = new ArrayList<>();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        for (int[] direction : directions) {
            int row = startRow + direction[0];
            int col = startCol + direction[1];

            while (row <= 8 && col <= 8 && row >= 1 && col >= 1) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) == null) {
                    legalMoves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (board.getPiece(newPosition).pieceColor == board.getPiece(myPosition).pieceColor) {
                        break;
                    } else {
                        legalMoves.add(new ChessMove(myPosition, newPosition, null));
                        break;
                    }
                }
                row += direction[0];
                col += direction[1];
            }
        }
        return legalMoves;
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, int[][] directions, ChessPosition startPosition) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        
        for (int[] direction : directions) {
            boolean addMoves = false;;
            boolean addPromotionMoves = false;
            int row = startRow + direction[0];
            int col = startCol + direction[1];
            if (row > 8 || col > 8 || row < 1 || col < 1) {
                continue;
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            if (col == startCol) {
                if (board.getPiece(newPosition) == null) {
                    addMoves = true;
                    if (row == 8 || row == 1) {
                        addPromotionMoves = true;
                    } else {
                        addPromotionMoves = false;
                    }
                }
                else{
                    break;
                }

            } else if (col != startCol && board.getPiece(newPosition) != null) {
                if (board.getPiece(newPosition).pieceColor != board.getPiece(startPosition).pieceColor) {
                    addMoves = true;
                    if (row == 8 || row == 1) {
                        addPromotionMoves = true;
                    } else {
                        addPromotionMoves = false;
                    }
                }

            }
            if(addMoves && addPromotionMoves){
                legalMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.BISHOP));
                legalMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                legalMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.QUEEN));
                legalMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.ROOK));
            }else if(addMoves && !addPromotionMoves){
                legalMoves.add(new ChessMove(startPosition, newPosition, null));
            }
        }
        return legalMoves;
    }

    public Collection<ChessMove> kingAndKnightMoves(ChessBoard board, int[][] directions, ChessPosition startPosition) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        for (int[] direction : directions) {
            int row = startRow + direction[0];
            int col = startCol + direction[1];
            ChessPosition newPosition = new ChessPosition(row, col);
            if (row <= 8 && col <= 8 && row >= 1 && col >= 1) {
                if (board.getPiece(newPosition) == null) {
                    legalMoves.add(new ChessMove(startPosition, newPosition, null));
                } else {
                    if (board.getPiece(newPosition).pieceColor != board.getPiece(startPosition).pieceColor) {
                        legalMoves.add(new ChessMove(startPosition, newPosition, null));
                    }
                }
            } else {
                continue;
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

}