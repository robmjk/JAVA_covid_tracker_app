package com.example.mycovidtracker.database;

public class CovidDbSchema {
    public static final class CovidTable {
        public static final String NAME = "covid_cases";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE =
                    "title";
            public static final String DATE = "date";
            public static final String SOLVED =
                    "solved";
            public static final String SUSPECT =
                    "suspect";
        }
    }
}
