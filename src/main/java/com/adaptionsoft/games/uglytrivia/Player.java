package com.adaptionsoft.games.uglytrivia;

class Player {
    private final String name;
    private int place;
    private int coins;
    private boolean isInPenaltyBox;

    Player(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    int getPlace() {
        return place;
    }

    void move(int roll) {
        place += roll;
        if (place >= 12) {
            place -= 12;
        }
    }

    void addCoin() {
        coins++;
    }

    int getCoins() {
        return coins;
    }

    void putInPenaltyBox() {
        isInPenaltyBox = true;
    }

    boolean isInPenaltyBox() {
        return isInPenaltyBox;
    }

    void printNewLocation() {
        System.out.println(getName() + "'s new location is " + getPlace());
    }

    void printCoins() {
        System.out.println(getName()
                + " now has "
                + getCoins()
                + " Gold Coins.");
    }
}
