package com.ultreon.mods.pixelguns.util;

/**
 * A logical side of the Minecraft game.
 */
public enum LogicalSide {

    /**
     * The logical client of the Minecraft game, which interfaces with the player's inputs and renders the player's
     * viewpoint.
     * <p>
     * The logical client is only shipped with the client distribution of the game.
     */
    CLIENT,
    /**
     * The logical server of the Minecraft game, responsible for connecting to clients and running the simulation logic
     * on the level.
     * <p>
     * The logical server is shipped with both client and dedicated server distributions. The client distribution runs
     * the logical server for singleplayer mode and LAN play.
     */
    SERVER;

    /**
     * {@return if this logical side is the server}
     */
    public boolean isServer() {
        return !isClient();
    }

    /**
     * {@return if the logical side is the client}
     */
    public boolean isClient() {
        return this == CLIENT;
    }
}
