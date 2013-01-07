package ogo.spec.game.multiplayer.client;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import ogo.spec.game.multiplayer.*;
import ogo.spec.game.multiplayer.client.peer.*;
import ogo.spec.game.multiplayer.ChatProto.*;

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
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public  ConcurrentLinkedQueue<String> getBuffer(){
        return buffer;
    }
}

public class ChatClient implements TokenChangeListener
{

    InputReaderRunnable reader;
    int nextId = 0;

    Token.Builder copyToken(Token token)
    {
        Token.Builder builder = Token.newBuilder();

        builder.mergeFrom(token);

        return builder;
    }

    // print messages
    void printMessages(List<Token.Message> messages) {
        for (Token.Message message : messages) {
            if (message.getId() >= nextId) {
                System.out.println(message.getName() + ": " + message.getMessage());
                nextId = message.getId() + 1;
            }
        }
    }

    /**
     * Get messages from the input reader.
     */
    public Iterable<Token.Message> getMessages()
    {
        LinkedList<Token.Message> messages = new LinkedList<Token.Message>();

        String line;
        while ((line = reader.buffer.poll()) != null) {
            Token.Message message = Token.Message.newBuilder()
                .setId(nextId)
                .setName("kokx")
                .setMessage(line)
                .build();
            messages.add(message);
            nextId++;
        }

        return messages;
    }

    public Token tokenChanged(Token token)
    {
        printMessages(token.getMessageList());

        Token.Builder builder = copyToken(token);

        builder.addAllMessage(getMessages());

        builder.setLastId(nextId);

        return builder.build();
    }

    void run() throws IOException, InterruptedException, UnknownHostException
    {
        Client client = new Client();

        List<PeerInfo> servers = client.findServers();

        Scanner sc = new Scanner(System.in);

        System.out.println("The following servers are available:");

        for (int i = 0; i < servers.size(); i++) {
            System.out.println("IP (" + (i + 1) + "): " + servers.get(i).ip.toString());
        }

        System.out.println("Please type in the server number to which you want to connect:");

        int num = sc.nextInt() - 1;

        // start the input reader
        reader = new InputReaderRunnable(System.in);
        new Thread(reader).start();

        client.setTokenChangeListener(this);

        client.connectToInitServer(servers.get(num));
        client.connectToPeer();
    }
    
    public static void main(String[] a) throws Exception{
        new ChatClient().run();
    }
}
