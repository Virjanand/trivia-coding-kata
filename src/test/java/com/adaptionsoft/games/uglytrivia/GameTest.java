package com.adaptionsoft.games.uglytrivia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.util.stream.IntStream.*;
import static org.assertj.core.api.Assertions.assertThat;

class GameTest {

    Game game;

    @BeforeEach
    void createGame() {
        game = new Game();
    }

    @Test
    void testCreateGame() {

        assertThat(game.getPopQuestionsSize()).isEqualTo(50);
        assertThat(game.getScienceQuestionsSize()).isEqualTo(50);
        assertThat(game.getSportsQuestionsSize()).isEqualTo(50);
        assertThat(game.getRockQuestionsSize()).isEqualTo(50);
    }

    @Test
    void testAddPlayerPrinting() {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintStream inmemory = new PrintStream(outputStream)) {
            System.setOut(inmemory);

            game.add("player1");
        }

        assertThat(outputStream.toString()).isEqualTo(
                "player1 was added\r\n" +
                        "They are player number 1\r\n"
        );
    }

    @Test
    void testAddPlayerNumberOfPlayers() {
        game.add("player1");
        game.add("player2");

        assertThat(game.howManyPlayers()).isEqualTo(2);
    }

    @Test
    void testRollQuestionPrinting() {
        game.add("player1");

        ByteArrayOutputStream outputStream = getGameOutput(1);

        assertThat(outputStream.toString()).isEqualTo(
                "player1 is the current player\r\n" +
                        "They have rolled a 1\r\n" +
                        "player1's new location is 1\r\n" +
                        "The category is Science\r\n" +
                        "Science Question 0\r\n"
        );
    }

    @Test
    void testRollEvenNumberFromPenaltyBox() {
        game.add("player1");
        game.wrongAnswer();

        ByteArrayOutputStream outputStream = getGameOutput(2);

        assertThat(outputStream.toString()).isEqualTo(
                "player1 is the current player\r\n" +
                        "They have rolled a 2\r\n" +
                        "player1 is not getting out of the penalty box\r\n"
        );
    }

    @Test
    void testRollOddNumberFromPenaltyBox() {
        game.add("player1");
        game.wrongAnswer();

        ByteArrayOutputStream outputStream = getGameOutput(1);

        assertThat(outputStream.toString()).isEqualTo(
                "player1 is the current player\r\n" +
                        "They have rolled a 1\r\n" +
                        "player1 is getting out of the penalty box\r\n" +
                        "player1's new location is 1\r\n" +
                        "The category is Science\r\n" +
                        "Science Question 0\r\n"
        );
    }

    @Test
    void testRollOddNumberOver12FromPenaltyBoxLoopsBackOver0() {
        game.add("player1");
        game.wrongAnswer();

        ByteArrayOutputStream outputStream = getGameOutput(13);

        assertThat(outputStream.toString()).isEqualTo(
                "player1 is the current player\r\n" +
                        "They have rolled a 13\r\n" +
                        "player1 is getting out of the penalty box\r\n" +
                        "player1's new location is 1\r\n" +
                        "The category is Science\r\n" +
                        "Science Question 0\r\n"
        );
    }

    @Test
    void testRoll12PlaceLoopsBackTo0() {
        game.add("player1");

        ByteArrayOutputStream outputStream = getGameOutput(12);

        assertThat(outputStream.toString()).isEqualTo(
                "player1 is the current player\r\n" +
                        "They have rolled a 12\r\n" +
                        "player1's new location is 0\r\n" +
                        "The category is Pop\r\n" +
                        "Pop Question 0\r\n"
        );
    }

    @ParameterizedTest(name = "Location {0} -> {1}")
    @CsvSource({
            "0, Pop Question 0",
            "4, Pop Question 0",
            "8, Pop Question 0",
            "1, Science Question 0",
            "5, Science Question 0",
            "9, Science Question 0",
            "2, Sports Question 0",
            "6, Sports Question 0",
            "10, Sports Question 0",
            "11, Rock Question 0"
    })
    void testRollNumberGivesCategory(int roll, String question) {
        game.add("player1");

        ByteArrayOutputStream outputStream = getGameOutput(roll);

        assertThat(outputStream.toString()).contains(question);
    }

    @Test
    void testWrongAnswer() {
        game.add("player1");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintStream inmemory = new PrintStream(outputStream)) {
            System.setOut(inmemory);

            game.wrongAnswer();
        }

        assertThat(outputStream.toString()).isEqualTo(
                "Question was incorrectly answered\r\n" +
                        "player1 was sent to the penalty box\r\n"
        );
    }

    @Test
    void testWrongAnswerNextPlayer() {
        game.add("player1");
        game.add("player2");
        assertThat(game.getCurrentPlayerNumber()).isEqualTo(0);

        game.wrongAnswer();

        assertThat(game.getCurrentPlayerNumber()).isEqualTo(1);
    }

    @Test
    void testWasCorrectlyAnsweredOutput() {
        game.add("player1");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintStream inmemory = new PrintStream(outputStream)) {
            System.setOut(inmemory);

            game.wasCorrectlyAnswered();
        }

        assertThat(outputStream.toString()).isEqualTo(
                "Answer was correct!!!!\r\n" +
                        "player1 now has 1 Gold Coins.\r\n"
        );
    }

    @Test
    void testWasCorrectlyAnsweredNextPlayer() {
        game.add("player1");
        game.add("player2");
        assertThat(game.getCurrentPlayerNumber()).isEqualTo(0);

        game.wasCorrectlyAnswered();

        assertThat(game.getCurrentPlayerNumber()).isEqualTo(1);
    }

    @Test
    void testWasCorrectlyAnsweredDidNotWin() {
        game.add("player1");

        boolean didPlayer1Win = game.wasCorrectlyAnswered();

        assertThat(didPlayer1Win).isTrue();
    }

    @Test
    void testWasCorrectlyAnsweredDidWin() {
        game.add("player1");
        range(0, 5).forEach(i -> game.wasCorrectlyAnswered());

        boolean didPlayer1Win = game.wasCorrectlyAnswered();

        assertThat(didPlayer1Win).isFalse();
    }

    @Test
    void testWasCorrectlyAnsweredGettingOutFromPenaltyBoxOutput() {
        game.add("player1");
        game.wrongAnswer();
        game.roll(1);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintStream inmemory = new PrintStream(outputStream)) {
            System.setOut(inmemory);

            game.wasCorrectlyAnswered();
        }

        assertThat(outputStream.toString()).isEqualTo(
                "Answer was correct!!!!\r\n" +
                        "player1 now has 1 Gold Coins.\r\n"
        );
    }

    @Test
    void testWasCorrectlyAnsweredStayingInPenaltyBoxOutput() {
        game.add("player1");
        game.wrongAnswer();
        game.roll(2);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintStream inmemory = new PrintStream(outputStream)) {
            System.setOut(inmemory);

            game.wasCorrectlyAnswered();
        }

        assertThat(outputStream.toString()).isEqualTo("");
    }

    @Test
    void testWasCorrectlyAnsweredStayingInPenaltyBoxOutputNextPlayer() {
        game.add("player1");
        game.add("player2");
        game.wrongAnswer(); // player1 gets in penalty box
        game.wrongAnswer(); // player2 turn
        game.roll(2); // player1 stays in penalty box
        assertThat(game.getCurrentPlayerNumber()).isEqualTo(0);

        game.wasCorrectlyAnswered(); //player1 answers correctly

        assertThat(game.getCurrentPlayerNumber()).isEqualTo(1);
    }

    @Test
    void testWasCorrectlyAnsweredGettingOutFromPenaltyBoxOutputNextPlayer() {
        game.add("player1");
        game.add("player2");
        game.wrongAnswer(); // player1 gets in penalty box
        game.wrongAnswer(); // player2 turn
        game.roll(1); // player1 gets out from penalty box
        assertThat(game.getCurrentPlayerNumber()).isEqualTo(0);

        game.wasCorrectlyAnswered(); //player1 answers correctly

        assertThat(game.getCurrentPlayerNumber()).isEqualTo(1);
    }

    private ByteArrayOutputStream getGameOutput(int roll) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintStream inmemory = new PrintStream(outputStream)) {
            System.setOut(inmemory);

            game.roll(roll);
        }
        return outputStream;
    }
}
