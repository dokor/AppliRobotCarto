package sevrain.test;

import android.app.Dialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import java.io.IOException;
import java.nio.ByteBuffer;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import android.widget.Toast;
import android.content.Context;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends AppCompatActivity {
    private final static int ID_DIALOG = 0;
    private RelativeLayout layout_joystick;
    private TextView PosX, PosY, Vitesse, PosAng;
    private Button Phares, Info, Connexion, Deconnexion;
    private JoyStickClass js;
    private TcpClient mTcpClient;
    private Reglages reglages;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public Context lecontext = getBaseContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Création du joystick + Position X/Y + Boutons
        layout_joystick = (RelativeLayout) findViewById(R.id.layout_joystick);
        PosX = (TextView) findViewById(R.id.PosX);
        PosY = (TextView) findViewById(R.id.PosY);
        Phares = (Button) findViewById(R.id.Phares);
        Info = (Button) findViewById(R.id.Info);
        Connexion = (Button) findViewById(R.id.Connexion);
        Deconnexion = (Button) findViewById(R.id.Deconnexion);

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
                if (arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    PosX.setText("X: " + String.valueOf(js.getX()));
                    PosY.setText("Y: " + String.valueOf(js.getY()));

                    int direction = js.get8Direction();
                    if (direction == JoyStickClass.STICK_UP) {
                        //
                    } else if (direction == JoyStickClass.STICK_UPRIGHT) {
                        //
                    } else if (direction == JoyStickClass.STICK_RIGHT) {
                        //
                    } else if (direction == JoyStickClass.STICK_DOWNRIGHT) {
                        //
                    } else if (direction == JoyStickClass.STICK_DOWN) {
                        //
                    } else if (direction == JoyStickClass.STICK_DOWNLEFT) {
                        //
                    } else if (direction == JoyStickClass.STICK_LEFT) {
                        //
                    } else if (direction == JoyStickClass.STICK_UPLEFT) {
                        //
                    } else if (direction == JoyStickClass.STICK_NONE) {
                        //
                    }
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    PosX.setText("X:");
                    PosY.setText("Y:");
                }
                return true;
            }
        });

        setContentView(R.layout.test);

        Button btvoir = (Button) findViewById(R.id.btvoir);
        Button btecrire = (Button) findViewById(R.id.btecrire);
        btvoir.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                ReadSettings(lecontext);
            }
        });

        btecrire.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextView datatext = (TextView) findViewById(R.id.text);
                String sQuantite = datatext.getText() + "\n";
                WriteSettings(lecontext, sQuantite);
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

            Log.i("Test", "" + Short.toString(reglages.getPhare_Luminosite()));
            Log.i("Test", "" + reglages.getPhare_Luminosite());
        }
    };

    private OnClickListener ConnexionRobot = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i("Debug", "Connexion");
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
    public Dialog onCreateDialog(int id) {
        Dialog box = null;
        box = new Dialog(this);
        box.setContentView(R.layout.dialog);
        box.setTitle("Informations");
        return box;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://sevrain.test/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://sevrain.test/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    //Class pour connexion tcp
    public class ConnectTask extends AsyncTask<Void, ByteBuffer, TcpClient> {

        @Override
        protected TcpClient doInBackground(Void... params) {
            //we create a TCPClient object and
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(ByteBuffer message) throws IOException {
                    //this method calls the onProgressUpdate

                    publishProgress(message);
                    //    Log.i("Debug","Input message: " + message);
                    if (reglages == null) {
                        Log.i("Debug", "Création de réglages");
                        reglages = new Reglages();
                        reglages.createFromBytes(message);

                    }
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


    public void WriteSettings(Context context, String data) {
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        try {
            fOut = context.openFileOutput("settings.dat", MODE_APPEND);
            osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            //popup surgissant pour le résultat
            Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Settings not saved", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                osw.close();
                fOut.close();
            } catch (IOException e) {
                Toast.makeText(context, "Settings not saved", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public String ReadSettings(Context context) {
        FileInputStream fIn = null;
        InputStreamReader isr = null;

        char[] inputBuffer = new char[255];
        String data = null;

        try {
            fIn = context.openFileInput("settings.dat");
            isr = new InputStreamReader(fIn);
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            //affiche le contenu de mon fichier dans un popup surgissant
            Toast.makeText(context, " " + data, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Settings not read", Toast.LENGTH_SHORT).show();
        }
            /*finally {
               try {
                      isr.close();
                      fIn.close();
                      } catch (IOException e) {
                        Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
                      }
            } */
        return data;
    }

}


