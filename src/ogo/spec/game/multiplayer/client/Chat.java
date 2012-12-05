package ogo.spec.game.multiplayer.client;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import ogo.spec.game.multiplayer.*;
import ogo.spec.game.multiplayer.client.peer.*;

/**
 * Read input into a buffer.
 */
class InputReaderRunnable implements Runnable
{
    BufferedReader in;

    ConcurrentLinkedQueue<String> buffer = new ConcurrentLinkedQueue<String>();

    InputReaderRunnable(InputStream in)
    {
        this.in = new BufferedReader(new InputStreamReader(in));
    }

    public void run()
    {
        String line;
        try {
            while ((line = in.readLine()) != null) {
                buffer.add(line);
            }
        } catch (IOException e) {
            System.err.println("I/O Error");
            System.exit(1);
        }
    }
}

/**
 * Chat.
 */
public class Chat
{
    Peer prev;
    Peer next;

    InputReaderRunnable inputReader;

    int nextId = 0;

    Chat(Peer prev, Peer next)
    {
        this.prev = prev;
        this.next = next;
        inputReader = new InputReaderRunnable(System.in);
        new Thread(inputReader).start();
    }

    ChatProto.Token getToken() throws IOException
    {
        return ChatProto.Token.parseFrom(prev.read());
    }

    /**
     * Copy the token.
     */
    ChatProto.Token.Builder copyToken(ChatProto.Token token)
    {
        ChatProto.Token.Builder builder = ChatProto.Token.newBuilder();

        builder.mergeFrom(token);

        return builder;
    }

    public void init()
    {
        ChatProto.Token token = ChatProto.Token.newBuilder()
            .setLastId(0)
            .build();
        next.write(token);
    }

    /**
     * Get messages from the input reader.
     */
    public Iterable<ChatProto.Token.Message> getMessages()
    {
        LinkedList<ChatProto.Token.Message> messages = new LinkedList<ChatProto.Token.Message>();

        String line;
        while ((line = inputReader.buffer.poll()) != null) {
            ChatProto.Token.Message message = ChatProto.Token.Message.newBuilder()
                .setId(nextId)
                .setName("kokx")
                .setMessage(line)
                .build();
            messages.add(message);
            nextId++;
        }

        return messages;
    }

    public void chat() throws IOException
    {
        ChatProto.Token token = getToken();

        // get all messages
        List<ChatProto.Token.Message> messages = token.getMessageList();

        for (ChatProto.Token.Message message : messages) {
            if (message.getId() >= nextId) {
                System.out.println(message.getName() + ": " + message.getMessage());
                nextId = message.getId() + 1;
            }
        }

        // build a new token, and send it
        ChatProto.Token.Builder builder = copyToken(token);

        builder.addAllMessage(getMessages());

        builder.setLastId(nextId);

        // write the message
        next.write(builder.build());
    }
}
