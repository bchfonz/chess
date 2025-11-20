package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.Objects;

public class GameplayUI {

    public void observeGame(){
        ChessGame game = new ChessGame();
        boardWhite(game);
    }

    public void joinGame(ChessGame game, String team){
        if(Objects.equals(team, "WHITE")){
            boardWhite(game);
        }
        else if(Objects.equals(team, "BLACK")){
            boardBlack(game);
        }

    }

    private void boardBlack(ChessGame game){
        ChessBoard board = game.getBoard();
        System.out.print(EscapeSequences.ERASE_SCREEN);

        // --- Print top border with file labels (a-h)
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print("   "); // Left margin before A
        for (char c = 'h'; c >= 'a'; c--) {
            System.out.print(" " + c + " ");
        }
        System.out.print("   ");
        System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);

        // --- Print board rows ---
        for (int row = 1; row <= 8; row++) {
            int borderNumber = 0 + row; // Row label (8 at top, 1 at bottom)

            // Left border with rank label
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print(" " + borderNumber + " " + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);

            // Print board squares
            for (int col = 1; col <= 8; col++) {
                boolean isLightSquare = (row + col) % 2 == 0;

                String backgroundColor = isLightSquare
                        ? EscapeSequences.SET_BG_COLOR_BLUE
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                boolean isWhitePiece = true;
                String pieceString = " ";
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if(piece != null) {
                    isWhitePiece = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
                    pieceString = chessPieceString(piece);
                }



                String textColor = isWhitePiece
                        ? EscapeSequences.SET_TEXT_COLOR_WHITE
                        : EscapeSequences.SET_TEXT_COLOR_BLACK;

                System.out.print(backgroundColor + textColor + " " + pieceString + " " +
                        EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
            }

            // Right border with rank label
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print(" " + borderNumber + " " + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);

            System.out.println();
        }

        // --- Bottom border with file labels again ---
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print("   "); // Left margin
        for (char c = 'h'; c >= 'a'; c--) {
            System.out.print(" " + c + " ");
        }
        System.out.print("   ");
        System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
    }

    private void boardWhite(ChessGame game){
        ChessBoard board = game.getBoard();
        System.out.print(EscapeSequences.ERASE_SCREEN);

// --- Print top border with file labels (h–a for Black’s view)
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print("   "); // Left margin before H
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print(" " + c + " ");
        }
        System.out.print("   ");
        System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);

// --- Print board rows (from 1 at top to 8 at bottom)
        for (int row = 8; row >= 1; row--) {
            int borderNumber = 9 - row; // Will now show 1–8 from top to bottom

            // Left border with rank label
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print(" " + row + " " + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);

            // Print board squares
            for (int col = 8; col >= 1; col--) {
                boolean isLightSquare = (row + col) % 2 == 0;

                String backgroundColor = isLightSquare
                        ? EscapeSequences.SET_BG_COLOR_BLUE
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                boolean isWhitePiece = false;
                String pieceString = " ";
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null) {
                    isWhitePiece = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
                    pieceString = chessPieceString(piece);
                }

                String textColor = isWhitePiece
                        ? EscapeSequences.SET_TEXT_COLOR_WHITE
                        : EscapeSequences.SET_TEXT_COLOR_BLACK;

                System.out.print(backgroundColor + textColor + " " + pieceString + " " +
                        EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
            }

            // Right border with rank label
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.print(" " + row + " " + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);

            System.out.println();
        }

// --- Bottom border with file labels again (h–a)
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.print("   "); // Left margin
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print(" " + c + " ");
        }
        System.out.print("   ");
        System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);

    }

    private String chessPieceString (ChessPiece piece){
        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                return "♔";
            }
            else{
                return "♚";
            }
        }
        else if(piece.getPieceType() == ChessPiece.PieceType.QUEEN){
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                return "♕";
            }
            else{
                return "♛";
            }
        }
        else if(piece.getPieceType() == ChessPiece.PieceType.BISHOP){
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                return "♗";
            }
            else{
                return "♝";
            }
        }
        else if(piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                return "♘";
            }
            else{
                return "♞";
            }
        }
        else if(piece.getPieceType() == ChessPiece.PieceType.ROOK){
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                return "♖";
            }
            else{
                return "♜";
            }
        }
        else if(piece.getPieceType() == ChessPiece.PieceType.PAWN){
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                return "♙";
            }
            else{
                return "♟";
            }
        }
        else{
            return " ";
        }
    }
//        System.out.println("Game toString(): " + game.game().getBoard());
}
