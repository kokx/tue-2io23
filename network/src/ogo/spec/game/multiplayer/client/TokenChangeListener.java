package ogo.spec.game.multiplayer.client;

import ogo.spec.game.multiplayer.ChatProto.*;

interface TokenChangeListener {

    /**
     * Process the token, and create a new token to be sent.
     */
    public Token tokenChanged(Token token);
}
