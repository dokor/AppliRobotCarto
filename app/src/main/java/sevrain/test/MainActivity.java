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

    EditText textmsg;

    public File root = new File(Environment.getExternalStorageDirectory(), "SettingsRobot");
    public File file = new File(root + "/settings.txt");

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

        Savebtn = (Button) findViewById(R.id.LoadFichier);
        Savebtn.setOnClickListener(Save);

        if (!root.exists()) {
            root.mkdirs(); // this will create folder.
        }
        try {
            FileWriter writer = new FileWriter(file);
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
            Byte[] Bdatarecu = GetDataRobot();
            String[] SdataAEcrire = TransBtoS(Bdatarecu);
            InitSaveSettingsInFile(SdataAEcrire);
            /*
            if(mTcpClient.mRun != true){
                mTcpClient.stopClient();
            }*/

        }
    };

    private String[] TransBtoS(Byte[] ByteData)
    {
        String[] StringArray = new String[2];
        return StringArray;
    }
    private Byte[] GetDataRobot()
    {
        Byte[] bytearray = new Byte[2];
        return bytearray;
    }

    private OnClickListener DeconnexionRobot = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TcpClient.mRun) {
                mTcpClient.stopClient();
                reglages = null;
            }

        }
    };

    private OnClickListener Save = new OnClickListener() {
        @Override
        public void onClick(View v) {

            Toast.makeText(getApplicationContext(),"Données savegardées",
                    Toast.LENGTH_SHORT).show();
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
                    byte[] resultat = new byte[164];
                    resultat = message.array();
                    for (int i=0;i<164;i++){
                        String s = String.format("%8s", Integer.toBinaryString(resultat[i] & 0xFF)).replace(' ', '0');
                    }
                    InitSaveSettingsInFileByte(resultat);
                    updateBatteryLvl();
                    message.clear();


                    //publishProgress(message);
                    //    Log.i("Debug","Input message: " + message);
                    /*if (reglages == null) {
                        Log.i("Debug", "Création de réglages");
                        reglages = new Reglages();
                        reglages.createFromBytes(message);
                    }*/
/*
                    else if (reglages != null){
                           Log.i("Debug","MAJ de réglages");
                        reglages.createFromBytes(message);
                    }*/

                }


                public void connectionClosed() {

                }


                public void updateBatteryLvl() {




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

    public void SaveSettingsInFileUNIT(String ligneAEcrire, FileOutputStream  fos)
    {
        try
        {
//            for (int i = 0; i<data.length; i++)
//            {
//                fos.
//                fos.write(data[i].getBytes());
//                if (i < data.length-1)
//                {
//                    fos.write("\n".getBytes());
//                }
//            }
            fos.write(ligneAEcrire.getBytes());
        }
        catch (IOException e) {e.printStackTrace();}
    }
    public void SaveSettingsInFileUNITByte(byte ligneAEcrire, FileOutputStream  fos)
    {
        try
        {
//            for (int i = 0; i<data.length; i++)
//            {
//                fos.
//                fos.write(data[i].getBytes());
//                if (i < data.length-1)
//                {
//                    fos.write("\n".getBytes());
//                }
//            }
            fos.write(ligneAEcrire);
            fos.write("/n".getBytes());
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void InitSaveSettingsInFile(String[] data)
    {
        String[] DataTEST;
        String[] dataNull = null;
        if(data == dataNull){
            DataTEST = new String[]{"test1,savesettings", "test ligne 2", "test ligne 3", "test ligne 4", "test ligne 5"};
            data = DataTEST;
        }

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e){e.printStackTrace();}

        for (String ligneTab : data) {
            SaveSettingsInFileUNIT(ligneTab, fos); //Ecriture de chaque ligne dans le fichier settings
        }
        try
        {
            fos.close();
        }
        catch (IOException e) {e.printStackTrace();}

    }
    public void InitSaveSettingsInFileByte(byte[] data)
    {
        Byte[] dataNull = null;


        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e){e.printStackTrace();}

        for (Byte ligneTab : data) {
            SaveSettingsInFileUNITByte(ligneTab, fos); //Ecriture de chaque ligne dans le fichier settings
        }
        try
        {
            fos.close();
        }
        catch (IOException e) {e.printStackTrace();}

    }

    public void Load()
    {
        String[] datatab = new String[]{"test1,AutoLoad", "test ligne 2", "test ligne 3", "test ligne 4", "test ligne 5"};
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
        String[] array = new String[5];
        String line;
        int i = 0;
        try
        {
            while ((line = br.readLine()) != null)
            {
                nbr_lignes++;
                array[i] = line;
                i++;
            }
            fis.getChannel().position(0);
            isr.close();
        }
        catch (IOException e) {e.printStackTrace();}

        for (String ligneTab : array)
        {
            Toast.makeText(getApplicationContext(), ligneTab,
                    Toast.LENGTH_SHORT).show();
        }
//        return array;
    }
}


/*
 +        0 = 0xAA
 +        1 = 0x55
 +        2 = 0xAA
 +        3 = 0x55
 +        4 = 0xBA
 +        5 = 0x65
 +        6 = 0xBA
 +        7 = 0x65
 +        8 = 2
 +        9 = 0
 +        10 = 0
 +        11 = 0
 +        12 = 0
 +        13 = 0
 +        14 = -92
 +        15 = 0
 +        16 = 6
 +        17 = 0
 +        18 = 4
 +        19 = 0
 +        20 = 0*/