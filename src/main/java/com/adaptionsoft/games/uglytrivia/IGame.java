package com.adaptionsoft.games.uglytrivia;

public interface IGame {
    void add(String chet);

    void roll(int i);

    boolean wrongAnswer();

    boolean wasCorrectlyAnswered();
}
