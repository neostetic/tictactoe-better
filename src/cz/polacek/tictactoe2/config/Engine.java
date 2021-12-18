package cz.polacek.tictactoe2.config;

import cz.polacek.tictactoe2.config.languages.Czech;
import cz.polacek.tictactoe2.config.languages.English;
import cz.polacek.tictactoe2.utils.Colors;
import cz.polacek.tictactoe2.utils.StringBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class Engine {

    private enum GamefieldTypes {
        P1(Colors.ANSI_BLUE + "X" + Colors.ANSI_RESET),
        P2(Colors.ANSI_RED + "O" + Colors.ANSI_RESET),
        EM(" ");

        private String value;

        GamefieldTypes(String s) {
            this.value = s;
        }
    }

    private Scanner scanner = new Scanner(System.in);
    public Language text;
    public int rows;
    public int columns;
    public String[][] gamefield;
    public int player1turns = 0;
    public int player2turns = 0;
    public int playerTurns = 0;

    public void startGame() {
        System.out.println("(Czech, English)");
        System.out.print(text.selectLanguage);
        String language = scanner.next();
        if (language.toLowerCase(Locale.ROOT).trim().equals("czech")) {
            text = new Czech();
        } else {
            text = new English();
        }
        createGamefield();
        showGamefield();
        gameTurn();
    }

    public void createGamefield() {
        System.out.print(text.localized(Language.selectGameFieldRows));
        rows = scanner.nextInt();
        System.out.print(text.localized(Language.selectGameFieldColumns));
        columns = scanner.nextInt();
        gamefield = new String[rows][columns];
        generateGamefield();
    }

    public void generateGamefield() {
        for (String[] strings : gamefield) {
            Arrays.fill(strings, GamefieldTypes.EM.value);
        }
    }

    public void showGamefield() {
        System.out.print("\n ");
        for (int i = 0; i < gamefield[0].length; i++) {
            System.out.print(" - " + Colors.ANSI_PURPLE + i + Colors.ANSI_RESET);
        }
        System.out.println();
        for (int i = 0; i < gamefield.length; i++) {
            System.out.print(Colors.ANSI_CYAN + i + Colors.ANSI_RESET);
            for (int j = 0; j < gamefield[i].length; j++) {
                System.out.print(" - " + gamefield[i][j]);
            }
            System.out.println();
        }
    }

    public void gameTurn() {
        if (ifWin(GamefieldTypes.P1)) {
            System.out.println(text.localized(Language.player1wins));
            writeWin("Player1");
        } else if (ifWin(GamefieldTypes.P2)) {
            System.out.println(text.localized(Language.player2wins));
            writeWin("Player2");
        } else {
            GamefieldTypes gamefieldTypes;
            if (playerTurns % 2 == 0) {
                System.out.println(Colors.ANSI_BLUE + text.localized(Language.player1start) + Colors.ANSI_RESET);
                gamefieldTypes = GamefieldTypes.P1;
                player1turns++;
            } else {
                System.out.println(Colors.ANSI_RED + text.localized(Language.player2start) + Colors.ANSI_RESET);
                gamefieldTypes = GamefieldTypes.P2;
                player2turns++;
            }
            int chooseRows;
            int chooseColumns;
            do {
                System.out.print(Colors.ANSI_CYAN + " - " + text.localized(Language.selectRow) + Colors.ANSI_RESET);
                chooseRows = scanner.nextInt();
                System.out.print(Colors.ANSI_PURPLE + " - " + text.localized(Language.selectColumn) + Colors.ANSI_RESET);
                chooseColumns = scanner.nextInt();
            } while (
                    chooseRows > rows &&
                    chooseColumns > columns ||
                    !gamefield[chooseRows][chooseColumns].equals(GamefieldTypes.EM.value));
            playerTurns++;
            chooseGamefield(gamefieldTypes.value, chooseRows, chooseColumns);
        }
    }

    public void chooseGamefield(String gamefieldTypes, int rows, int columns) {
        gamefield[rows][columns] = gamefieldTypes;
        showGamefield();
        gameTurn();
    }

    public boolean ifWin(GamefieldTypes gamefieldType) {
        String type = gamefieldType.toString();
        for (int i = 0; i < gamefield.length - 5; i++) {
            for (int j = 0; j < gamefield[0].length; j++) {
                if (
                        gamefield[i][j].equals(gamefieldType.value) &&
                        gamefield[i+1][j].equals(gamefieldType.value) &&
                        gamefield[i+2][j].equals(gamefieldType.value) &&
                        gamefield[i+3][j].equals(gamefieldType.value) &&
                        gamefield[i+4][j].equals(gamefieldType.value)
                ) {
                    return true;
                }
            }
            for (int j = 0; j < gamefield[0].length - 5; j++) {
                if (
                        gamefield[i][j].equals(gamefieldType.value) &&
                        gamefield[i+1][j+1].equals(gamefieldType.value) &&
                        gamefield[i+2][j+2].equals(gamefieldType.value) &&
                        gamefield[i+3][j+3].equals(gamefieldType.value) &&
                        gamefield[i+4][j+4].equals(gamefieldType.value)
                ) {
                    return true;
                } else if (
                        gamefield[i+4][j].equals(gamefieldType.value) &&
                        gamefield[i+3][j+1].equals(gamefieldType.value) &&
                        gamefield[i+2][j+2].equals(gamefieldType.value) &&
                        gamefield[i+1][j+3].equals(gamefieldType.value) &&
                        gamefield[i][j+4].equals(gamefieldType.value)
                ) {
                    return true;
                }
            }
        }
        for (int i = 0; i < gamefield.length; i++) {
            for (int j = 0; j < gamefield[0].length; j++) {
                if (
                        gamefield[i][j].equals(gamefieldType.value) &&
                        gamefield[i][j+1].equals(gamefieldType.value) &&
                        gamefield[i][j+2].equals(gamefieldType.value) &&
                        gamefield[i][j+3].equals(gamefieldType.value) &&
                        gamefield[i][j+4].equals(gamefieldType.value)
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    public void writeWin(String string) {
        File file = new File("./src/cz/polacek/tictactoe2/records/local.csv");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.WriteFile(file, new Date() + ";" + string + ";" + playerTurns + ";" + rows + "x" + columns);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
