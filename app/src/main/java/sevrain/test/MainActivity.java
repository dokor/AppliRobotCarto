package sevrain.test;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import java.io.IOException;
import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {
    private final static int ID_DIALOG = 0;
    private RelativeLayout layout_joystick;
    private TextView PosX, PosY,Vitesse,PosAng;
    private Button Phares,Info,Connexion,Deconnexion;
    private JoyStickClass js;
    private TcpClient mTcpClient;
    private Reglages reglages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Création du joystick + Position X/Y + Boutons
        layout_joystick = (RelativeLayout) findViewById(R.id.layout_joystick);
        PosX = (TextView) findViewById(R.id.PosX);
        PosY = (TextView) findViewById(R.id.PosY);
        Phares =(Button) findViewById(R.id.Phares);
        Info =(Button) findViewById(R.id.Info);
        Connexion=(Button) findViewById(R.id.Connexion);
        Deconnexion=(Button) findViewById(R.id.Deconnexion);

        Phares.setOnClickListener(AllumerPhares);
        Info.setOnClickListener(MontrerInfo);
        Connexion.setOnClickListener(ConnexionRobot);
        Deconnexion.setOnClickListener(DeconnexionRobot);

        js = new JoyStickClass(getApplicationContext()
                , layout_joystick, R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    PosX.setText("X: " + String.valueOf(js.getX()));
                    PosY.setText("Y: " + String.valueOf(js.getY()));

                    int direction = js.get8Direction();
                    if(direction == JoyStickClass.STICK_UP) {
                        //
                    } else if(direction == JoyStickClass.STICK_UPRIGHT) {
                        //
                    } else if(direction == JoyStickClass.STICK_RIGHT) {
                        //
                    } else if(direction == JoyStickClass.STICK_DOWNRIGHT) {
                        //
                    } else if(direction == JoyStickClass.STICK_DOWN) {
                        //
                    } else if(direction == JoyStickClass.STICK_DOWNLEFT) {
                        //
                    } else if(direction == JoyStickClass.STICK_LEFT) {
                        //
                    } else if(direction == JoyStickClass.STICK_UPLEFT) {
                        //
                    } else if(direction == JoyStickClass.STICK_NONE) {
                        //
                    }
                }
                else if (arg1.getAction() == MotionEvent.ACTION_UP){
                PosX.setText("X:");
                PosY.setText("Y:");}
                return true;
            }
        });


    }

    private OnClickListener MontrerInfo = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialog(ID_DIALOG);
            //Montrer Info
        }
    };
    private OnClickListener AllumerPhares = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                int len = reglages.concatenateByteArray().length;
                mTcpClient.sendMessage(reglages.concatenateByteArray());
                Log.i("Debug",String.valueOf(len));
            } catch (IOException e) {
                Log.e("Debug","ERREUR sendMessage");
            }

        }
    };

    private OnClickListener ConnexionRobot = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i("Debug","Connexion");
            new ConnectTask().execute();
            /*
            if(mTcpClient.mRun != true){
                mTcpClient.stopClient();
            }*/

        }
    };

    private OnClickListener DeconnexionRobot = new OnClickListener() {
        @Override
        public void onClick(View v) {


            if (TcpClient.isConnected) {
                mTcpClient.stopClient();
                reglages = null;
            }

        }
    };

    @Override
    public Dialog onCreateDialog(int id){
        Dialog box = null;
        box = new Dialog(this);
        box.setContentView(R.layout.dialog);
        box.setTitle("Informations");
        return box;
    }



    //Class pour connexion tcp
    private class ConnectTask extends AsyncTask <Void, ByteBuffer, TcpClient>{

        @Override
        protected TcpClient doInBackground(Void... params) {
            //we create a TCPClient object and
            reglages = new Reglages();
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(ByteBuffer bf) throws IOException {
                    //this method calls the onProgressUpdate

                    publishProgress(bf);
                //    Log.i("Debug","Input message: " + message);
                    /*
                    if(reglages == null){
                        Log.i("Debug","Création de réglages");
                        reglages = new Reglages();
                        reglages.createFromBytes(bf);

                    }*/
/*
                    else if (reglages != null){
                           Log.i("Debug","MAJ de réglages");
                        reglages.createFromBytes(message);
                    }*/

                }
            });
            mTcpClient.run();


            return null;
        }
        @Override
        protected void onProgressUpdate(ByteBuffer... values) {
            super.onProgressUpdate();

        //    Log.i("onProgressUpdate","" + values);

        }
    }

}


