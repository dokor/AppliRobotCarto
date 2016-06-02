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
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

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
    public File file = new File(root + "/settingsDEV.csv");

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
            if(!file.exists()) {
                FileWriter writer = new FileWriter(file);
            }
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
            if (TcpClient.mRun) {
                mTcpClient.stopClient();
                reglages = null;
            }
        }
    };

    private OnClickListener Save = new OnClickListener() {
        @Override
        public void onClick(View v) {
           String[] test =  Load();
            byte[] Tab_Envoi = new byte[164];
            int j=0;

/*            Toast.makeText(getApplicationContext(),"Données savegardées",
                    Toast.LENGTH_SHORT).show();*/
            for (int i=0;i<64;i++){
                byte[] test1 = new byte[fromHexString(test[i]).length];
                test1 = fromHexString(test[i]);
                System.arraycopy(test1,0,Tab_Envoi,j,test1.length);
                j = j+test1.length;

            }
            try {
                mTcpClient.sendMessage(Tab_Envoi);
            } catch (IOException e) {
                e.printStackTrace();
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

    //Class pour connexion tcp
    public class ConnectTask extends AsyncTask<Void, ByteBuffer, TcpClient> {
        @Override
        protected TcpClient doInBackground(Void... params) {
            //we create a TCPClient object and
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(ByteBuffer message) throws IOException {
                    //Creation du Tableau
                    byte[] resultat = new byte[164];
                    //Remplissage avec les valeurs du message recu
                    resultat = message.array();
                    //Creation du Tableau de string
                    String[] T_Transport = new String[16];
                    String[] DonneeTabPropre = new String[64];

                    //Remplissage du tableau de string avec les valeurs en Hexa de resultat
                    for (int i=0;i<16;i++){
                        T_Transport[i]= String.format(Integer.toHexString(resultat[i] & 0xFF)).replace(' ', '0');
                    }
                    //Inversion des données du tableau selon methode spécial Header
                    T_Transport = InverseData(T_Transport);

                    //Vérification du header, afin d'etre sur de son intégrité
                    if(VerifIntegriteHeader(T_Transport)){
                        //Header OK
                        DevinTypeMessage(T_Transport);
                        DonneeTabPropre = InverseMessageT_Transp(resultat,DonneeTabPropre);
                        DonneeTabPropre[30]="e803";
                        DonneeTabPropre[6]="0700";
                        DonneeTabPropre[2]="0200";
                        InitSaveSettingsInFile(DonneeTabPropre);

                        String[] test =  Load();
                        byte[] Tab_Envoi = new byte[164];
                        int j=0;

/*            Toast.makeText(getApplicationContext(),"Données savegardées",
                    Toast.LENGTH_SHORT).show();*/
                        for (int i=0;i<64;i++){
                            byte[] test1 = new byte[fromHexString(test[i]).length];
                            test1 = fromHexString(test[i]);
                            System.arraycopy(test1,0,Tab_Envoi,j,test1.length);
                            j = j+test1.length;

                        }
                        try {
                            mTcpClient.sendMessage(Tab_Envoi);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    message.clear();
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

    private static byte[] fromHexString(final String encoded) {

        if ((encoded.length() % 2) != 0)
            throw new IllegalArgumentException("Input string must contain an even number of characters");

        final byte[] result = new byte[encoded.length()/2];
        final char enc[] = encoded.toCharArray();
        for (int i = 0; i < enc.length; i += 2) {
            StringBuilder curr = new StringBuilder(2);
            curr.append(enc[i]).append(enc[i + 1]);
            result[i/2] = (byte) Integer.parseInt(curr.toString(), 16);
        }
        return result;
    }

    public String[] InverseMessageT_Transp(byte[] DonneeByte, String[] DonneeString){
        int j = 0;
        int k = 0;

        for (int i=0;i<2;i++){
            DecoupeInverseData4(DonneeByte, DonneeString, j,k);
            j=j+4;
            k++;
        }
        for (int i=0;i<4;i++) {
            DecoupeInverseData2(DonneeByte, DonneeString,j,k);
            j=j+2;
            k++;
        }
        for (int i=0;i<20;i++) {
            DecoupeInverseData2(DonneeByte, DonneeString, j,k);
            j=j+2;
            k++;
        }
        for (int i=0;i<4;i++){
            DecoupeInverseData4(DonneeByte, DonneeString, j,k);
            j=j+4;
            k++;
        }
        for (int i=0;i<3;i++) {
            DecoupeInverseData2(DonneeByte, DonneeString, j,k);
            j=j+2;
            k++;
        }
        for (int i=0;i<2;i++){
            DonneeString[k] = String.format(Integer.toHexString(DonneeByte[j] & 0xFF)).replace(' ', '0');
            if(DonneeString[k].length() == 1) {
                DonneeString[k] = "0".concat(DonneeString[k]);
            }
            j++;
            k++;
        }
        for (int i=0;i<4;i++) {
            DecoupeInverseData2(DonneeByte, DonneeString, j,k);
            j=j+2;
            k++;
        }
        for (int i=0;i<2;i++){
            DonneeString[k] = String.format(Integer.toHexString(DonneeByte[j] & 0xFF)).replace(' ', '0');
            if(DonneeString[k].length() == 1) {
                DonneeString[k] = "0".concat(DonneeString[k]);
            }
            j++;
            k++;
        }
        for (int i=0;i<3;i++){
            DecoupeInverseData2(DonneeByte, DonneeString, j,k);
            j=j+2;
            k++;
        }
        for (int i=0;i<2;i++){
            DecoupeInverseData4(DonneeByte, DonneeString, j,k);
            j=j+4;
            k++;
        }
        for (int i=0;i<6;i++) {
            DecoupeInverseData2(DonneeByte, DonneeString, j,k);
            j=j+2;
            k++;
        }
        for (int i=0;i<12;i++){
            DecoupeInverseData4(DonneeByte, DonneeString, j,k);
            j=j+4;
            k++;
        }
        return DonneeString;
    }

    public String[] DecoupeInverseData2(byte [] DonneeByte,String[] Donnees,int Indice,int k){
        String[] Tab2o_inverse = new String[2];
        String Mot2o;
        int j = 0;

        for (int i=Indice; i<=Indice+1; i++){
            Tab2o_inverse[j]= String.format(Integer.toHexString(DonneeByte[i] & 0xFF)).replace(' ', '0');
            if(Tab2o_inverse[j].length() == 1) {
                Tab2o_inverse[j] = "0".concat(Tab2o_inverse[j]);
            }
            j++;
        }
        Tab2o_inverse = InverseData(Tab2o_inverse);
        Mot2o = Tab2o_inverse[0].concat(Tab2o_inverse[1]);
        Donnees[k]=Mot2o;
        return Tab2o_inverse;
    }

    public String[] DecoupeInverseData4(byte [] DonneeByte,String[] Donnees,int Indice,int k){
        String[] Tab4o_inverse = new String[4];
        int j = 0;
        String Mot4o;

        for (int i=Indice; i<=Indice+3; i++){
            Tab4o_inverse[j]= String.format(Integer.toHexString(DonneeByte[i] & 0xFF)).replace(' ', '0');
            if(Tab4o_inverse[j].length() == 1) {
                Tab4o_inverse[j] = "0".concat(Tab4o_inverse[j]);
            }
            j++;
        }
        Tab4o_inverse = InverseData(Tab4o_inverse);
        Mot4o = Tab4o_inverse[0].concat(Tab4o_inverse[1]).concat(Tab4o_inverse[2]).concat(Tab4o_inverse[3]);
        Donnees[k]= Mot4o;

        return Tab4o_inverse;
    }
    public boolean VerifIntegriteHeader(String[] TabAAnalyser){
        String HeaderAttendu1 = "55aa55aa";
        String HeaderAttendu2 = "65ba65ba";
        String Header1 = ConcateneGroupe(TabAAnalyser, 0,3);
        String Header2 = ConcateneGroupe(TabAAnalyser, 4,7);
        if(Header1.equals(HeaderAttendu1)){
            if(Header2.equals(HeaderAttendu2)){
                //Intégrité OK
                return true;
            }
            else {
                //Intégrité BAD ou BUG
                return false;
            }
        }
        else{
            //Intégrité BAD ou BUG
            return false;
        }
    }

    public String ConcateneGroupe(String[] Tab, int indexDepart, int indexArriv){
       String StringConcat;
        int k = indexArriv - indexDepart + 1;
        int kcompt=0;
       String[] TabaConcatene = new String[k];
       for(int i = indexDepart; i< indexArriv+1;i++){
           TabaConcatene[kcompt] = Tab[i];
           kcompt++;
       }
        StringConcat =  Concatene(TabaConcatene);
        return StringConcat;
    }

    public String Concatene(String[] TabAConcatene){
        String Stringcomp = new String();
        for(String ligne : TabAConcatene) {
            Stringcomp += ligne;
        }
        return Stringcomp;
    }

    public String DevinTypeMessage(String[] tabStringADecoup)
    {
        String TabHeader = new String();
        for(int i = 0; i<4; i++){
            TabHeader += tabStringADecoup[i];
        }
        String TabHeaderNum = TabHeader.toString();
        switch(TabHeaderNum) {
            //TYPE_MSG_DE_SERVICE
            case "1":
                TabHeader = "TYPE_MSG_DE_SERVICE";
                break;
            //TYPE_MSG_REGLAGE
            case "2" :
                TabHeader = "TYPE_MSG_REGLAGE";
                break;
            //TYPE_MSG_LONG
            case "40" :
                TabHeader = "TYPE_MSG_LONG";
                break;
            //TYPE_MSG_SCAN
            case "41" :
                TabHeader = "TYPE_MSG_SCAN";
                break;
            //TYPE_MSG_COMPAS_XY
            case "42" :
                TabHeader = "TYPE_MSG_COMPAS_XY";
                break;
            //Other
            default:
                TabHeader = "ERROR DEFAULT";
                break;
        }
        return TabHeader;
    }

    public String[] InverseData(String[] Tab){
        String[] Tab_Inverse = new String[Tab.length];

        switch(Tab.length){
            case 2 :
                Tab_Inverse[0] = Tab[1];
                Tab_Inverse[1] = Tab[0];
                break;
            case 4 :
                Tab_Inverse[0] = Tab[3];
                Tab_Inverse[1] = Tab[2];
                Tab_Inverse[2] = Tab[1];
                Tab_Inverse[3] = Tab[0];
                break;
            //T_Transport
            case 16 :
                //Header1
                Tab_Inverse[0] = Tab[3];
                Tab_Inverse[1] = Tab[2];
                Tab_Inverse[2] = Tab[1];
                Tab_Inverse[3] = Tab[0];
                //Header2
                Tab_Inverse[4] = Tab[7];
                Tab_Inverse[5] = Tab[6];
                Tab_Inverse[6] = Tab[5];
                Tab_Inverse[7] = Tab[4];
                //TypeMessage
                Tab_Inverse[8] = Tab[9];
                Tab_Inverse[9] = Tab[8];
                //Total_Octet
                Tab_Inverse[10] = Tab[11];
                Tab_Inverse[11] = Tab[10];
                //Position_Premier_Octet
                Tab_Inverse[12] = Tab[13];
                Tab_Inverse[13] = Tab[12];
                //Nombre_Doctets
                Tab_Inverse[14] = Tab[15];
                Tab_Inverse[15] = Tab[14];
                break;
            default :
                break;
        }
        return Tab_Inverse;
    }

    public void SaveSettingsInFileUNIT(String ligneAEcrire, FileOutputStream  fos)
    {
        final PrintStream printStream = new PrintStream(fos);
        try
        {
            printStream.print(ligneAEcrire);
//          fos.write(ligneAEcrire.getBytes(Charset.forName("ISO-8859-1")));
            //fos.write(ligneAEcrire.getBytes());
            fos.write("\n".getBytes());
        }
        catch (IOException e) {e.printStackTrace();
            printStream.close();
        }
    }
    public void SaveSettingsInFileUNITByte(byte ligneAEcrire, FileOutputStream  fos)
    {
        try
        {
            fos.write(ligneAEcrire);
            fos.write("\n".getBytes());
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void InitSaveSettingsInFile(String[] data)
    {
        String[] DataTEST;
        String[] dataNull = null;
        if(data == dataNull){
            DataTEST = new String[]{"test1,savesettings", "test ligne 2", "test ligne 3"};
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

    public String[] Load()
    {
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
        String[] array = new String[164];
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
//            Toast.makeText(getApplicationContext(), ligneTab,
//                    Toast.LENGTH_SHORT).show();
        }
        return array;
    }
}