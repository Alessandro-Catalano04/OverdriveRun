package controller;
 
/**
 * MenuCommand: Typed enumeration of every action that can be triggered from a menu.
 *
 * Centralises all command names, eliminating magic strings throughout the codebase.
 * Benefits:
 *   The compiler catches missing switch cases and typos at build time.
 *   MenuController can use MenuCommand.valueOf(cmd) instead of
 *       fragile String.equals comparisons.
 *
 */
public enum MenuCommand {

    // Navigate to the level-selection panel.
    SELECT_LEVEL,
 
    // Highlight level 1 in the level-selection panel and loads level.json.
    SELECT_LVL1,
 
    // Highlight level 2 in the level-selection panel and loads level2.json.
    SELECT_LVL2,
 
    // Start a run on whichever level is currently selected in the level-selection panel.
    START_SELECTED,
 
    // Return to the main game menu. 
    BACK_TO_MENU,
 
    // Resume the current run after a pause. 
    RESUME,
 
    // Restart the current level from the beginning. 
    RETRY
}