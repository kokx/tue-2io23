/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ogo.spec.game.lobby;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import ogo.spec.game.multiplayer.GameProto.Token;
import ogo.spec.game.multiplayer.client.TokenChangeListener;

/**
 *
 * @author florian
 */
public class tempGame extends JFrame implements TokenChangeListener{

    int nextId;
    public tempGame(){
        super("This is the AWESOMEST GAME EVUURRR!!!");

        setSize(400, 100);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void saveInfo(Token token){
        // Ja.. hier doe je dus zooi..
    }

    public Token.Builder addInfo(Token.Builder token){
        // Ja.. hier doe je dus ook zooi..
        return token;
    }

    Token.Builder copyToken(Token token)
    {
        Token.Builder builder = Token.newBuilder();

        builder.mergeFrom(token);

        return builder;
    }

    long lastMessage = -1;
    int counter = 0;
    public Token tokenChanged(Token token)
    {
        counter++;
        long time = System.currentTimeMillis();
        if(lastMessage == -1 || time - lastMessage >  1000){
            long diff = time - lastMessage;
            System.out.println("TPS: " + counter + "/" + diff + " = " + 1000.0*counter/diff);
            lastMessage = time;
            counter = 0;
        }
        nextId = token.getLastId();

        saveInfo(token);

        Token.Builder builder = copyToken(token);

        builder.setLastId(nextId);

        return builder.build();
    }
}
