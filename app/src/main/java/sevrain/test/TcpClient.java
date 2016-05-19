package sevrain.test;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TcpClient {

    public static final String SERVER_IP = "192.168.0.1"; //your computer IP address
    public static final int SERVER_PORT = 50007;
    public static boolean isConnected;

    private boolean mRun = false;
    private OnMessageReceived mMessageListener = null;

    private int bufferSize = 20000000;
    private ByteBuffer bf;
    private BufferedInputStream inFromServer;
    private DataOutputStream outFromClient;



    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(TcpClient.OnMessageReceived listener) {

        mMessageListener = listener;
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {
        Log.i("Debug", "stopClient");

        // send mesage that we are closing the connection
        //sendMessage(Constants.CLOSED_CONNECTION + "Kazy");

        mRun = false;
        isConnected = false;
    }

    public void sendMessage(byte[] message) throws IOException {
        if (outFromClient != null) {

            outFromClient.write(message);
            outFromClient.flush();
        }
    }

    public void run() {

        mRun = true;
        isConnected = true;
        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVER_PORT);

            try {
                Log.i("Debug", "inside try catch");

                //receives the message which the server sends back

                inFromServer = new BufferedInputStream(socket.getInputStream());
                outFromClient = new DataOutputStream(socket.getOutputStream());

                bf = ByteBuffer.allocate(bufferSize);




                while (mRun) {
                   // Log.i("Debug", "inside while mRun");
                    int b = inFromServer.read();
                    if (b == -1){
                        break;
                    }
                    bf.put((byte) b);

                 /*   bf.get(bb);
                    Log.i("Debug", Integer.toString(bb.length));*/

                    if ( bf != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                       // Log.i("Debug","Message received !");
                        mMessageListener.messageReceived(bf);


                    }

                }

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                Log.i("Debug","Socket closed");
                socket.close();
            }

        }
        catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }
    }



    public interface OnMessageReceived {
        public void messageReceived(ByteBuffer bf) throws IOException;
    }
}