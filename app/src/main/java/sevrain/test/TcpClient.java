package sevrain.test;

import android.util.Log;
import android.view.ViewDebug;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TcpClient {

    public static final String SERVER_IP = "192.168.0.1"; //your computer IP address
    public static final int SERVER_PORT = 50007;

    public static boolean mRun = false;
    private OnMessageReceived mMessageListener = null;

    private int bufferSize = 20000;
    public ByteBuffer bf;
    private BufferedInputStream inFromServer;
    private BufferedOutputStream outFromClient;
    //    private DataOutputStream outFromClient;
    public byte[] Transport = new byte[16];



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

        mRun = false;
        inFromServer = null;
        outFromClient = null;
    }

    public void sendMessage(byte[] message) throws IOException {
        if (outFromClient != null) {

//            Log.i("Debug","sendMessage");
            outFromClient.write(message);
            outFromClient.flush();
        }
    }

    public int[] GetMessage() throws IOException {
        int[] Tab_String = new int[164];
        ByteBuffer bfmess = bf;
        if (mRun) {
            if (inFromServer != null) {
                bfmess.order(ByteOrder.LITTLE_ENDIAN);
                for (int i = 0; i < 164; i++) {
                    Tab_String[i] = inFromServer.read();
                }
            }
        }
        return Tab_String;
    }

    public void run() {

        mRun = true;
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
                outFromClient = new BufferedOutputStream(socket.getOutputStream());
//                outFromClient = new DataOutputStream(socket.getOutputStream());






                while (mRun) {
                    // Log.i("Debug", "inside while mRun");
                    bf = ByteBuffer.allocate(bufferSize);
                    bf.order(ByteOrder.LITTLE_ENDIAN);
                    for (int i=0;i<5000;i++) {
                        int b = inFromServer.read();

                        if (b == -1) {
                            break;
                        }

                        bf.put((byte) b);

                    }

                    if ( bf != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        // Log.i("Debug","Message received !");
                        mMessageListener.messageReceived(bf);
                        mMessageListener.updateBatteryLvl();
                    }
                    bf.clear();
                }
            }
            catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            }
            finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                Log.i("Debug","Socket closed");
                socket.close();
                mMessageListener.connectionClosed();
            }
        }
        catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }

    public interface OnMessageReceived {
        public void messageReceived(ByteBuffer bf) throws IOException;
        public void connectionClosed();
        public void updateBatteryLvl();
    }
}