package controller;

/**
 * Typed enumeration of every action that can be triggered from a menu.
 *
 * Buttons carry MenuCommand.X.name() as their action command, so a
 * rename is caught by the compiler everywhere instead of silently breaking a
 * string comparison at runtime.
 */
public enum MenuCommand {

    /** Navigate to the level-selection panel. */
    SELECT_LEVEL,

    /** Highlight level 1 and pre-load it into the engine. */
    SELECT_LVL1,

    /** Highlight level 2 and pre-load it into the engine. */
    SELECT_LVL2,

    /** Start a run on the level currently selected. */
    START_SELECTED,

    /** Return to the main game menu. */
    BACK_TO_MENU,

    /** Resume the current run after a pause. */
    RESUME,

    /** Restart the current level from the beginning. */
    RETRY
}