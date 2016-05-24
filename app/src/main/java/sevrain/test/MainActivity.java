package sevrain.test;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static int ID_DIALOG = 0;
    private RelativeLayout layout_joystick;
    private TextView PosX, PosY, Vitesse, PosAng;
    private Button Phares, Info, Connexion, Deconnexion, Savebtn, Loadbtn;
    private JoyStickClass js;
    private TcpClient mTcpClient;
    private Reglages reglages;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    EditText textmsg;

    public File root = new File(Environment.getExternalStorageDirectory(), "SettingsRobot");

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


        Savebtn = (Button) findViewById(R.id.button1);
        Savebtn.setOnClickListener(Save);
        Loadbtn = (Button) findViewById(R.id.button2);
        Loadbtn.setOnClickListener(Load);

        textmsg=(EditText)findViewById(R.id.editText1);

        String h = "settings";
        // this will create a new name everytime and unique
        // if external memory exists and folder with name Notes
        if (!root.exists()) {
            root.mkdirs(); // this will create folder.
        }
        File filepath = new File(root, h + ".txt");  // file path to save
        try {
            FileWriter writer = new FileWriter(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }


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

    private OnClickListener Save = new OnClickListener() {
        @Override
        public void onClick(View v) {
           Save();
        }
    };
    private OnClickListener Load = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Load();
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

    public void Save()
    {
        File file = new File(root + "/settings.txt");
        String dataString = textmsg.getText().toString();
        String[] data = new String[1];
        data[0] = dataString;
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (int i = 0; i<data.length; i++)
                {
                    fos.write(data[i].getBytes());
                    if (i < data.length-1)
                    {
                        fos.write("\n".getBytes());
                    }
                }
            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e) {e.printStackTrace();}
        }
        Toast.makeText(getApplicationContext(), dataString,
                Toast.LENGTH_SHORT).show();

    }


    public  String[] Load()
    {
        File file = new File(root + "/settings.txt");
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        int nbr_lignes=0;
        try
        {
            while ((br.readLine()) != null)
            {
                nbr_lignes++;
            }
        }
        catch (IOException e) {e.printStackTrace();}

        try
        {
            fis.getChannel().position(0);
        }
        catch (IOException e) {e.printStackTrace();}

        String[] array = new String[nbr_lignes];

        String line;
        int i = 0;
        try
        {
            while((line=br.readLine())!=null)
            {
                array[i] = line;
                i++;
            }
        }
        catch (IOException e) {e.printStackTrace();}
        Toast.makeText(getApplicationContext(), array[0],
                Toast.LENGTH_SHORT).show();
        return array;
    }


}


