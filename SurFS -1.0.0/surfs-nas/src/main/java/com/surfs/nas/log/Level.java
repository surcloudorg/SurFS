/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;


public class Level {

    public static final Level DEBUG = new Level(0);
    public static final Level INFO = new Level(1);
    public static final Level WARN = new Level(2);
    public static final Level ERROR = new Level(3);
    public static final Level FATAL = new Level(4);
    public static final Level TRACE = new Level(-3);
    private static final String DEBUG_S = "DEBUG";
    private static final String INFO_S = "INFO";
    private static final String WARN_S = "WARN";
    private static final String ERROR_S = "ERROR";
    private static final String FATAL_S = "FATAL";
    private static final String TRACE_S = "TRACE";

    /**
     * String
     *
     * @param level String
     * @return int
     */
    public static Level parseLevel(String level) {
        if (level.equalsIgnoreCase(DEBUG_S)) {
            return DEBUG;
        } else if (level.equalsIgnoreCase(INFO_S)) {
            return INFO;
        } else if (level.equalsIgnoreCase(WARN_S)) {
            return WARN;
        } else if (level.equalsIgnoreCase(ERROR_S)) {
            return ERROR;
        } else if (level.equalsIgnoreCase(FATAL_S)) {
            return FATAL;
        } else if (level.equalsIgnoreCase(TRACE_S)) {
            return TRACE;
        } else {
            return INFO;
        }
    }

    /**
     * int
     *
     * @param level
     * @return Level
     */
    public static Level newstance(int level) {
        switch (level) {
            case 1:
                return INFO;
            case 2:
                return WARN;
            case 3:
                return ERROR;
            case -3:
                return TRACE;
            case 4:
                return FATAL;
            default:
                return DEBUG;
        }
    }
    private int level = 0;

    private Level(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        if (level == 0) {
            return DEBUG_S;
        } else if (level == 1) {
            return INFO_S;
        } else if (level == 2) {
            return WARN_S;
        } else if (level == 3) {
            return ERROR_S;
        } else if (level == -3) {
            return TRACE_S;
        } else {
            return FATAL_S;
        }
    }

    /**
     * @return the level
     */
    public int intValue() {
        return level;
    }
}
