package sevrain.test;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Process.killProcess;

//https://github.com/PhilJay/MPAndroidChart/wiki/YAxis
public class MainActivity extends AppCompatActivity
                implements NavigationView.OnNavigationItemSelectedListener {
    private final static int ID_DIALOG = 0;
    private RelativeLayout layout_joystick, layout_Carto;
    public GraphView graph;
    private FloatingActionButton Connexion, Btn_TEST, Carto;
    private Switch SwitchPhares, SwitchLidar;
    private JoyStickClass js;
    private TextView CardTextA, CardTextB, CardTextC;
    public TcpClient mTcpClient;
    public File root = new File(Environment.getExternalStorageDirectory(), "SettingsRobot");
    public File file = new File(root + "/settingsDEV.csv");
    public File fileL = new File(root + "/DataLidar.csv");
    public File fileRG = new File(root + "/ReferencesReg.csv");
    private int k_phare =0,lidar,count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        graph = (GraphView) findViewById(R.id.graph);

        //Création du joystick + Position X/Y + Boutons
        layout_joystick = (RelativeLayout) findViewById(R.id.layout_joystick);
        layout_Carto = (RelativeLayout) findViewById(R.id.layout_Carto);
        CardTextA = (TextView) findViewById(R.id.CardTextA);
        CardTextB = (TextView) findViewById(R.id.CardTextB);
        CardTextC = (TextView) findViewById(R.id.CardTextC);

        Btn_TEST = (FloatingActionButton) findViewById(R.id.Btn_TEST);
        Carto = (FloatingActionButton) findViewById(R.id.Info);
        Connexion = (FloatingActionButton) findViewById(R.id.Connexion);
        SwitchPhares = (Switch) findViewById(R.id.Switch_phare) ;
        SwitchLidar = (Switch) findViewById(R.id.Switch_Lidar) ;

        Btn_TEST.setOnClickListener(Action_Btn_TEST);
        Connexion.setOnClickListener(ConnexionRobot);
        SwitchPhares.setOnClickListener(AllumerPhares);
        SwitchLidar.setOnClickListener(OnOffLidar);
        Carto.setOnClickListener(AffichageCarto);

        Carto.setImageResource(R.drawable.ic_explore_white_36dp);
        Btn_TEST.setImageResource(R.drawable.ic_info_outline_white_48dp);
        Connexion.setImageResource(R.drawable.ic_cloud_white_24dp);

        js = new JoyStickClass(getApplicationContext()
                , layout_joystick, R.drawable.stick_fait_maisonItem);
        js.setStickSize(100, 100);
        js.setLayoutSize(300, 300);
//        js.setStickAlpha(150);
        js.setOffset(32);
        js.setMinimumDistance(25);

        final int[] lock = {0,0};

        layout_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                float Y;
                float X;
                if (TcpClient.mRun) {
                    if (arg1.getAction() == MotionEvent.ACTION_DOWN
                            || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                        Y = js.getY();
                        X = js.getX();
                        int direction = js.get8Direction();
                        if (direction == JoyStickClass.STICK_UP) {
                            if (lock[0] == 0 || lock[1] == 1) {
                                if (lock[1] == 0) {
                                    lock[1] = 1;
                                }
                                SendMessage(Modif2ParametrePrecis("00000000000000000000000000000000", ConvertInvertData(Y,0), 27, 26));
                                lock[0]++;
                            }
                        } else if (direction == JoyStickClass.STICK_UPRIGHT) {
                            if (lock[0] == 0 || lock[1] == 1) {
                                if (lock[1] == 0) {
                                    lock[1] = 1;
                                }

                                SendMessage(Modif2ParametrePrecis(ConvertInvertData(X,1), ConvertInvertData(Y,0), 27, 26));

                                lock[0]++;
                            }
                        } else if (direction == JoyStickClass.STICK_RIGHT) {
                            if (lock[0] == 0 || lock[1] == 1) {
                                if (lock[1] == 0) {
                                    lock[1] = 1;
                                }
                                SendMessage(Modif2ParametrePrecis(ConvertInvertData(X,1), "00000000000000000000000000000000", 27, 26));
                                lock[0]++;
                            }
                        } else if (direction == JoyStickClass.STICK_DOWNRIGHT) {
                            if (lock[0] == 0 || lock[1] == 1) {
                                if (lock[1] == 0) {
                                    lock[1] = 1;
                                }
                                if (Y <=-150) Y=-150;
                                X = -X;
                                SendMessage(Modif2ParametrePrecis(ConvertInvertData(X,1), ConvertInvertData(Y,0), 27, 26));
                                lock[0]++;
                            }
                        } else if (direction == JoyStickClass.STICK_DOWN) {
                            if (lock[1] == 0) {
                                lock[1] = 1;
                            }
                            if (lock[0] == 0 || lock[1] == 1) {
                                if (Y <=-150) Y=-150;
                                SendMessage(Modif2ParametrePrecis("00000000000000000000000000000000", ConvertInvertData(Y,0), 27, 26));
                                lock[0]++;
                            }
                        } else if (direction == JoyStickClass.STICK_DOWNLEFT) {
                            if (lock[0] == 0 || lock[1] == 1) {
                                if (lock[1] == 0) {
                                    lock[1] = 1;
                                }
                                if (Y <=-150) Y=-150;
                                X = -X;
                                SendMessage(Modif2ParametrePrecis(ConvertInvertData(X,1), ConvertInvertData(Y,0), 27, 26));

                                lock[0]++;
                            }
                        } else if (direction == JoyStickClass.STICK_LEFT) {
                            if (lock[0] == 0 || lock[1] == 1) {
                                if (lock[1] == 0) {
                                    lock[1] = 1;
                                }
//                                SendMessage(Modif2ParametrePrecis("00000000000000000011010001000010", "00000000000000000000000000000000", 27, 26));
                                SendMessage(Modif2ParametrePrecis(ConvertInvertData(X,1), "00000000000000000000000000000000", 27, 26));
                                lock[0]++;
                            }
                        } else if (direction == JoyStickClass.STICK_UPLEFT) {
                            if (lock[0] == 0 || lock[1] == 1) {
                                if (lock[1] == 0) {
                                    lock[1] = 1;
                                }

                                SendMessage(Modif2ParametrePrecis(ConvertInvertData(X,1), ConvertInvertData(Y,0), 27, 26));
                                lock[0]++;
                            }
                        } else if (direction == JoyStickClass.STICK_NONE) {
                            if (lock[0] != 1) {
                                SendMessage(Modif2ParametrePrecis("00000000000000000000000000000000", "00000000000000000000000000000000", 27, 26));
                                Log.i("Debug", "Stop");
                                lock[0] = 0;
                                lock[1] = 0;
                            }
                        }
                    } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                        if (lock[0] != 1) {
                            SendMessage(Modif2ParametrePrecis("00000000000000000000000000000000", "00000000000000000000000000000000", 27, 26));
                            Log.i("Debug", "Stop");
                            lock[0] = 0;
                            lock[1] = 0;
                        }
                    }
                }
                    return true;
                }

        });

        if (!root.exists()) {
             root.mkdirs(); // this will create folder.
        }

        try {
            if(!file.exists()) {
                FileWriter writer = new FileWriter(file);
            }
            if(!fileL.exists()) {
                FileWriter writerL = new FileWriter(fileL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onRadioButtonClickedMode(View view) {
        if (TcpClient.mRun) {
        // Is the button now checked?
            boolean checked = ((RadioButton) view).isChecked();

            // Check which radio button was clicked
            switch (view.getId()) {
                case R.id.rad_manuel:
                    if (checked)
                        SendMessage(ModifParametrePrecis("00000010", 34));
                    break;
                case R.id.rad_assiste:
                    if (checked)
                        SendMessage(ModifParametrePrecis("00000101", 34));
                    break;
                case R.id.rad_auto:
                    if (checked)
                        SendMessage(ModifParametrePrecis("00000100", 34));
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_param_Robot) {

        } else if (id == R.id.nav_param_app) {

        } else if (id == R.id.nav_Contact) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private OnClickListener Action_Btn_TEST = new OnClickListener() {
        @Override
        public void onClick(View v) {

            String[] Data = LoadFile(file);

            CardTextA.setText(getString(R.string.vitesse) + "\n"+"valeur");
            CardTextB.setText(getString(R.string.batterie) + "\n"+"valeur");
            CardTextC.setText(getString(R.string.distance) + "\n"+"valeur");
        }
    };

    private OnClickListener AllumerPhares = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TcpClient.mRun) {
                if (k_phare==0){
                    SendMessage(ModifParametrePrecis("1110100000000011", 30));
                    k_phare=1;
                }
                else{
                    SendMessage(ModifParametrePrecis("0000000000000000", 30));
                    k_phare=0;
                }

            }

        }
    };

    private OnClickListener AffichageCarto = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String[] DataLidar = LoadFile(fileL);
            int[][] Tab = DecoupeInverseDataLidar(DataLidar);
            if(count <270)
            CreaCarto(Tab);
        }
    };

    private int[][] PassageCartesien(int[][] angledirec){
        for (int i = 0; i < angledirec[0].length ;i++) { //Pour chaque ligne
            double CoordX = angledirec[1][i] * Math.sin(Math.toRadians(angledirec[0][i]));
            double CoordY = angledirec[1][i] * Math.cos(Math.toRadians(angledirec[0][i]));
            angledirec[0][i] = ((int) CoordX);
            angledirec[1][i] = ((int) CoordY);
        }
        return angledirec;
    }
    private int getValeurMax(DataPoint[] tab_data){
        double[] Tab_max = new double[2];
        for (DataPoint data_unit :tab_data) {
            if(Math.abs(data_unit.getX()) > Tab_max[0]) {
                Tab_max[0] = Math.abs(data_unit.getX());
            }
            if(Math.abs(data_unit.getY()) > Tab_max[1]) {
                Tab_max[1] = Math.abs(data_unit.getY());
            }
        }
        int[] tab_max_int = new int[2];
        tab_max_int[0] = ((int) Math.abs(Tab_max[0]));
        tab_max_int[1] = ((int) Math.abs(Tab_max[1]));
        if(tab_max_int[0] > tab_max_int[1] ){
            return tab_max_int[0];
        }
        else{
            return tab_max_int[1];
        }

    }

    public LineGraphSeries<DataPoint> MajDonneeCarto(DataPoint[] data){
        graph.getViewport().setScrollable(true);
        //get max valeur absolue de Xmin et Xmax
        int valeur_max = getValeurMax(data);
        int k = 1000;
        if(valeur_max < 3000 ){
            valeur_max = 3000;
        }
        if(valeur_max > 9000 ){
            valeur_max = 9000;
        }
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(- valeur_max - k);
        graph.getViewport().setMaxX(valeur_max + k);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-valeur_max - k);
        graph.getViewport().setMaxY(valeur_max + k);

        graph.setTitle("Robot Cartographieur");


        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(data);
        return series;
    }

    private int getIndiceAngle1(int[] tab_angle){
        int indice_need = 0;
        int k = 0;
        for (int angle :tab_angle) {
            if (angle > 0 ) {
                if (angle < 5) {
                    indice_need = k;
                }
            }
            k++;
        }
        return indice_need;
    }

    public void CreaCarto(int[][] angledirection){

        int indice0 = getIndiceAngle1(angledirection[1]);
        int[][] CoordXY = PassageCartesien(angledirection);
        int[] Xvalues = CoordXY[1];
        int[] Yvalues = CoordXY[0];
        DataPoint[] data = new DataPoint[Xvalues.length];

        for (int i = 0; i < Xvalues.length;i++) {
            int valeur_modif = 0;
            if (Xvalues[i] == 0) {
                data[i] = new DataPoint(data[i - 1].getX(), Yvalues[i]);
                valeur_modif++;
            }
            else if((Xvalues[i] < -4000) ^ (Xvalues[i] > 4000)) {
                data[i] = new DataPoint(data[i - 1].getX(), Yvalues[i]);
                valeur_modif++;
            }

            if (Yvalues[i] == 0){
                if(valeur_modif == 1) {
                    double temp = data[i].getX();
                    data[i] = new DataPoint(temp, data[i - 1].getY());
                }else{
                    data[i] = new DataPoint(Xvalues[i], data[i - 1].getY());
                }
                valeur_modif++;
            }
            else if ((Yvalues[i] < -4000) ^ (Yvalues[i] > 4000)){
                if(valeur_modif == 1) {
                    double temp = data[i].getX();
                    data[i] = new DataPoint(temp,data[i - 1].getY());
                }else{
                    data[i] = new DataPoint(Xvalues[i],data[i - 1].getY());
                }
                valeur_modif++;
            }

            if(valeur_modif == 0) {
                data[i] = new DataPoint(Xvalues[i], Yvalues[i]);
            }
        }

        LineGraphSeries<DataPoint> series = MajDonneeCarto(data);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series_tab, DataPointInterface dataPoint) {

                graph.setTitle("Robot Cartographieur : " +dataPoint);

            }
        });
        DataPoint[] Angle0 = new DataPoint[2];
        for (int i  = 0; i  < 2 ;  i++) {
            Angle0[i]  =  data[indice0 + i] ;
        }
//        Angle0[0]  =  data[indice0] ;
        LineGraphSeries<DataPoint> serieAngle0 = MajDonneeCarto(Angle0);
        serieAngle0.setColor(Color.RED);
        serieAngle0.setBackgroundColor(Color.RED);
        series.setDataPointsRadius(2);
        serieAngle0.setDrawDataPoints(true);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(1);
        series.setThickness(2);
        graph.removeAllSeries();
        graph.addSeries(serieAngle0);
        graph.addSeries(series);
//        if (series.equals(graph.getSeries())){
//
//        }else{
//
//        }



    }
       private OnClickListener OnOffLidar = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TcpClient.mRun) {
                if (k_phare==0){
                    String[] test =  LoadFile(file);
                    test[2] = "0000000100000000";
                    test[5] = "0001100000000000";
                    test[6] = "0010100100000000";
                    test[7] = "0010000000000000";
                    test[8] = "00000001000000000000000000000000";
                    byte[] Tab_Envoi = new byte[24];
                    int j=0;
                    for (int i=0;i<9;i++){
                        byte[] test1 = new byte[fromBinaryString(test[i]).length];
                        test1 = fromBinaryString(test[i]);
                        System.arraycopy(test1,0,Tab_Envoi,j,test1.length);
                        j = j+test1.length;
                    }
                    lidar=1;
                    SendMessage(Tab_Envoi);
                    k_phare=1;
                }
                else{
                    String[] test =  LoadFile(file);
                    test[2] = "0000000100000000";
                    test[5] = "0001100000000000";
                    test[6] = "0010100100000000";
                    test[7] = "0010010100000000";
                    test[8] = "00000001000000000000000000000000";
                    byte[] Tab_Envoi = new byte[24];
                    int j=0;
                    for (int i=0;i<9;i++){
                        byte[] test1 = new byte[fromBinaryString(test[i]).length];
                        test1 = fromBinaryString(test[i]);
                        System.arraycopy(test1,0,Tab_Envoi,j,test1.length);
                        j = j+test1.length;
                    }
                    lidar=0;
                    SendMessage(Tab_Envoi);
                    k_phare=0;
                }

            }

        }
    };


    private OnClickListener ConnexionRobot = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!mTcpClient.mRun) {
                new ConnectTask().startBackgroundPerform();
                Connexion.setImageResource(R.drawable.ic_cloud_done_white_24dp);

            }
            else if (mTcpClient.mRun) {
                Log.i("Debug", "Connexion");
                if (TcpClient.mRun) {
                    mTcpClient.stopClient();
                    Connexion.setImageResource(R.drawable.ic_cloud_off_white_24dp);
                }

            }
        }
    };

    private OnClickListener AfficheInfo = new OnClickListener() {
        @Override
        public void onClick(View v) {
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

        private Runnable r;

        public void startBackgroundPerform() {
            Timer timerAsync;
            TimerTask timerTaskAsync;
            final Handler handler = new Handler();
            timerAsync = new Timer();
            timerTaskAsync = new TimerTask() {
                @Override
                public void run() {
                    handler.post(r = new Runnable() {
                        public void run() {
                            ConnectTask performBackgroundTask = new ConnectTask();
                            try {
                                if(mTcpClient == null) {
                                    performBackgroundTask.execute();
                                }
                                else if (!TcpClient.mRun){
                                    killProcess();
                                }
                                else{
                                    updateData(mTcpClient.bf);
                                }
         //                           Log.i("Debug","execute");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                public void killProcess(){
                    handler.removeCallbacks(r);
                }
            };
            timerAsync.schedule(timerTaskAsync, 0, 500);

        }



        @Override
        protected TcpClient doInBackground(Void... params) {
            //we create a TCPClient object and
            Log.i("Debug","doInBackGround");
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
                        DonneeTabPropre = InverseMessageT_Transp(resultat,DonneeTabPropre,0);
                        SaveDataInFile(DonneeTabPropre,file);
                        Log.i("Debug","MAJ");
                        String[] test =  LoadFile(file);
                        byte[] Tab_Envoi = new byte[164];
                        int j=0;

                        for (int i=0;i<64;i++){
                            byte[] test1 = new byte[fromBinaryString(test[i]).length];
                            test1 = fromBinaryString(test[i]);
                            System.arraycopy(test1,0,Tab_Envoi,j,test1.length);
                            j = j+test1.length;

                        }
                    }
//                    message.clear();
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

    protected void updateData(ByteBuffer message){
        byte[] resultat = new byte[164];
        //Remplissage avec les valeurs du message recu
        if (message != null) {
            resultat = message.array();
            //Creation du Tableau de string
            String[] T_Transport = new String[16];
            int i = 0,n=0;
            int k = 0;
            if (lidar == 1) {
                while (resultat[i] != -86 && i<resultat.length-9 && n !=50) {
                    i++;
                    if(resultat[i+8] == 2){
                        i+=163;
                        n++;
                    }
                }
            } else {
                while (resultat[i] != -86 && i < resultat.length) {
                    i++;
                }
                k = i;
            }
                //Remplissage du tableau de string avec les valeurs en Hexa de resultat
                for (int j = 0; j < 16; j++) {
                    T_Transport[j] = String.format(Integer.toHexString(resultat[i] & 0xFF)).replace(' ', '0');
                    i++;
                }
                //Inversion des données du tableau selon methode spécial Header
                T_Transport = InverseData(T_Transport);
                byte[] resultat2 = new byte[(resultat.length) - k];
                System.arraycopy(resultat, k, resultat2, 0, (resultat.length) - k);
                graph.setTitle("Robot Cartographieur : Att");
                //Vérification du header, afin d'etre sur de son intégrité
                if (VerifIntegriteHeader(T_Transport)) {
                    //Header OK
                    if (DevinTypeMessage(T_Transport) == 2) {
                        String[] DonneeTabPropre = new String[64];
                        DonneeTabPropre = InverseMessageT_Transp(resultat2, DonneeTabPropre, 0);
                        SaveDataInFile(DonneeTabPropre, file);
                        Log.i("Debug", "MAJ Data");
                    }
                    if (DevinTypeMessage(T_Transport) == 41) {
                        String[] DonneeTabPropre = new String[406];
                        DonneeTabPropre = InverseMessageT_Transp(resultat2, DonneeTabPropre, 1);
                        SaveDataInFile(DonneeTabPropre, fileL);
                        Log.i("Debug", "MAJ Lidar");
                        graph.setTitle("Robot Cartographieur : MAJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ");
                        Carto.performClick();
                    }
                }
            }
        }


    private String ConvertInvertData (float Pos,int mode){

        float data;
        String ResultatS,ResultatS_inv;
        char[] Resultat_char = new char[32];
        int a;

        if(mode == 0) {
            data = -2 * Pos;
        }
        else {
            data = -Pos/3;
        }
            ResultatS = Integer.toBinaryString(Float.floatToIntBits(data));
            a = ResultatS.length();
            if (a != 32) {
                for (int b = 32 - a; b != 0; b--) {
                    ResultatS = "0".concat(ResultatS);
                }
            }

            ResultatS.getChars(24, 32, Resultat_char, 0);
            ResultatS.getChars(16, 24, Resultat_char, 8);
            ResultatS.getChars(8, 16, Resultat_char, 16);
            ResultatS.getChars(0, 8, Resultat_char, 24);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 32; i++) {
                sb.append(Resultat_char[i]);
            }
            ResultatS_inv = sb.toString();

            return ResultatS_inv;
    }

    private byte[] Modif2ParametrePrecis(String valeur,String valeur2,Integer index,Integer index2){
        String[] test =  LoadFile(file);
        byte[] Tab_Envoi = new byte[164];
        int j=0;
        test[index] = valeur;
        test[index2] = valeur2;
        for (int i = 0; i < 64; i++) {
            if(fromBinaryString(test[i])!= null) {
                byte[] test1 = new byte[fromBinaryString(test[i]).length];
                test1 = fromBinaryString(test[i]);
                System.arraycopy(test1, 0, Tab_Envoi, j, test1.length);
                j = j + test1.length;
            }
        }
        return Tab_Envoi;
    }

    private byte[] ModifParametrePrecis(String valeur, Integer index){
        String[] test =  LoadFile(file);
        byte[] Tab_Envoi = new byte[164];
        int j=0;
        test[index] = valeur;
        for (int i=0;i<64;i++){
            byte[] test1 = new byte[fromBinaryString(test[i]).length];
            test1 = fromBinaryString(test[i]);
            System.arraycopy(test1,0,Tab_Envoi,j,test1.length);
            j = j+test1.length;
        }
        return Tab_Envoi;
    }
    private void SendMessage(byte[] tabBEnvoi){
        try {
            mTcpClient.sendMessage(tabBEnvoi);
            Log.i("Debug","Envoi");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] fromBinaryString (final String s) {
        if(s != null) {
            if (s.length() == 32) {
                byte[] b2 = new byte[4];
                byte[] b = new BigInteger(s, 2).toByteArray();
                if (b.length == b2.length) {
                    return b;
                } else if (b.length == 1) {
                    return b2;
                } else if (b.length == 3) {
                    b2[0] = b[0];
                    b2[1] = b[0];
                    b2[2] = b[1];
                    b2[3] = b[2];
                    return b2;
                } else if (b.length == 2) {
                    b2[0] = b[0];
                    b2[1] = b[0];
                    b2[2] = b[0];
                    b2[3] = b[1];
                    return b2;
                } else {
                    b2[0] = b[1];
                    b2[1] = b[2];
                    b2[2] = b[3];
                    b2[3] = b[4];
                    return b2;
                }
            } else if (s.length() == 16) {
                byte[] b2 = new byte[2];
                byte[] b = new BigInteger(s, 2).toByteArray();
                if (b.length == b2.length) {
                    return b;
                } else if (b.length == 1) {
                    return b2;
                } else {
                    b2[0] = b[1];
                    b2[1] = b[2];
                    return b2;
                }
            } else if (s.length() == 8) {
                byte[] b2 = new byte[1];
                byte[] b = new BigInteger(s, 2).toByteArray();
                if (b.length == b2.length) {
                    return b;
                } else {
                    b2[0] = b[1];
                    return b2;
                }
            }
        }
        return null;
    }

    public String[] InverseMessageT_Transp(byte[] DonneeByte, String[] DonneeString,int mode){
        int j = 0;
        int k = 0;
        String[] tab = new String[3];
        String [] tab2 = new String[2406];
        switch (mode){
            case(0) :
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
                    DonneeString[k] = String.format("%8s", Integer.toBinaryString(DonneeByte[j] & 0xFF)).replace(' ', '0');
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
                    DonneeString[k] = String.format("%8s", Integer.toBinaryString(DonneeByte[j] & 0xFF)).replace(' ', '0');
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
                break;
            case(1) :
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

                int l;
                for (l=0;l<400;l++){
                    for (int m=0;m<3;m++) {
                        tab[m] = DecoupeInverseData2(DonneeByte, tab2, j, k);
                        j = j + 2;
                    }
                    DonneeString[k] = tab[0].concat(";").concat(tab[1]).concat(";").concat(tab[2]);
                    k++;

                }

                break;
        }
        return DonneeString;
    }

    public int [][] DecoupeInverseDataLidar(String[] Data){
        String[] s,sI;
        int a,b;
        sI = new String[3];
        char[] Resultat_char = new char[16];
        int [] Qua = new int[400], Ang = new int[400],Dis = new int[400];
        int m=0,sup=0;
        boolean Ok = false;

        count=0;
            for(int i=6;i<406;i++){
                    for (int k=0;k<3;k++) {
                        s = Data[i].split(";");
                        s[k].getChars(0, 8, Resultat_char, 8);
                        s[k].getChars(8, 16, Resultat_char, 0);
                        StringBuilder sb = new StringBuilder();
                        sb.append(Resultat_char[0]);
                        sb.append(Resultat_char[1]);
                        sb.append(Resultat_char[2]);
                        sb.append(Resultat_char[3]);
                        sb.append(Resultat_char[4]);
                        sb.append(Resultat_char[5]);
                        sb.append(Resultat_char[6]);
                        sb.append(Resultat_char[7]);
                        sb.append(Resultat_char[8]);
                        sb.append(Resultat_char[9]);
                        sb.append(Resultat_char[10]);
                        sb.append(Resultat_char[11]);
                        sb.append(Resultat_char[12]);
                        sb.append(Resultat_char[13]);
                        sb.append(Resultat_char[14]);
                        sb.append(Resultat_char[15]);
                        if(k==0){
                            Ok = false;
                            a= Integer.parseInt(sb.toString(), 2);
                            if(a!=0){
                                Qua[i-6-sup] = a;
                                Ok = true ;
                            }
                            else{
                                sup++;
                            }
                        }
                        else{
                            if(Ok == true){
                                if(k == 1){
                                    b = Integer.parseInt(sb.toString(), 2)/64;
                                    Ang[i-6-sup]= b;
                                    if(b==0){
                                        count++;
                                    }
                                    }
                                else {
                                   Dis[i-6-sup]= Integer.parseInt(sb.toString(), 2)/4;
                                }
                            }
                        }
                    }
                }
        int[][] Ang_Dis = new int[2][400-sup];

        for (int i=0;i<400-sup;i++) {
            Ang_Dis[0][i] = Ang[i];
            Ang_Dis[1][i] = Dis[i];
        }

        return Ang_Dis;
    }

    public String DecoupeInverseData2(byte [] DonneeByte,String[] Donnees,int Indice,int k){
        String[] Tab2o_inverse = new String[2];
        String Mot2o;
        int j = 0;

        for (int i=Indice; i<=Indice+1; i++){
            Tab2o_inverse[j]= String.format("%8s", Integer.toBinaryString(DonneeByte[i] & 0xFF)).replace(' ', '0');
            if(Tab2o_inverse[j].length() == 1) {
                Tab2o_inverse[j] = "0".concat(Tab2o_inverse[j]);
            }
            j++;
        }

        Mot2o = Tab2o_inverse[0].concat(Tab2o_inverse[1]);
        Donnees[k]=Mot2o;
        return Mot2o;
    }

    public String DecoupeInverseData4(byte [] DonneeByte,String[] Donnees,int Indice,int k){
        String[] Tab4o_inverse = new String[4];
        int j = 0;
        String Mot4o;

        for (int i=Indice; i<=Indice+3; i++){
            Tab4o_inverse[j]= String.format("%8s", Integer.toBinaryString(DonneeByte[i] & 0xFF)).replace(' ', '0');
            if(Tab4o_inverse[j].length() == 1) {
                Tab4o_inverse[j] = "0".concat(Tab4o_inverse[j]);
            }
            j++;
        }
        Mot4o = Tab4o_inverse[0].concat(Tab4o_inverse[1]).concat(Tab4o_inverse[2]).concat(Tab4o_inverse[3]);
        Donnees[k]= Mot4o;
        return Mot4o;
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

    public int DevinTypeMessage(String[] tabStringADecoup)
    {
        int Msg=0;
        String TypeMessage = new String();
        TypeMessage = tabStringADecoup[8].concat(tabStringADecoup[9]);
        switch(TypeMessage) {
            //TYPE_MSG_DE_SERVICE
            case "01":
                Msg = 1;
                break;

            //TYPE_MSG_REGLAGE
            case "02" :
                Msg = 2;
                break;

            //TYPE_MSG_LONG
            case "028" :
                Msg = 40;
                break;

            //TYPE_MSG_SCAN
            case "029" :
                Msg = 41;
                break;

            //TYPE_MSG_COMPAS_XY
            case "030" :
                Msg = 42;
                break;

            //Other
            default:
                Msg = 0;
                break;
        }
        return Msg;
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

    public void SaveDataInFileUNIT(String ligneAEcrire, FileOutputStream  fos)
    {
        final PrintStream printStream = new PrintStream(fos);
        try
        {
            printStream.print(ligneAEcrire);
            fos.write("\n".getBytes());
        }
        catch (IOException e) {e.printStackTrace();
            printStream.close();
        }
    }

    public void SaveDataInFile(String[] data,File file)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e){e.printStackTrace();}

        for (String ligneTab : data) {
            SaveDataInFileUNIT(ligneTab, fos); //Ecriture de chaque ligne dans le fichier settings
        }
        try
        {
            fos.close();
        }
        catch (IOException e) {e.printStackTrace();}

    }

    public String[] LoadFile(File file_a_ouvrir)
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file_a_ouvrir);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        int nbr_lignes=0;
        String[] array = new String[406];
        String line;
        int i = 0;
        try
        {
            while (((line = br.readLine()) != null) && ((i) != 406))
            {
                nbr_lignes++;
                array[i] = line;
                i++;
            }
            fis.getChannel().position(0);
            isr.close();
        }
        catch (IOException e) {e.printStackTrace();}
        return array;
    }
}