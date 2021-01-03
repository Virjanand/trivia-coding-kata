package com.adaptionsoft.games.uglytrivia;

enum QuestionCategory {
    POP(0), SCIENCE(1), SPORTS(2), ROCK(3);

    private final int place;

    QuestionCategory(int place) {
        this.place = place;
    }

    public int getPlace() {
        return place;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
