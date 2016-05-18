package sevrain.test;

/*
Equivalent à la structure T_Buf_Reglage
 */

import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Reglages {
    public byte Transport[];//16 octets
    public byte Etat_Moteur_Droit[];//20
    public byte Etat_Moteur_Gauche[];//20
    public byte ConsigneManuelle[];//8
    public byte VitesseMesure[];//8
    public byte Phare_Luminosite[];//2
    public byte Temperature_Exterieure[];//2
    public short V_Batterie; //2
    public byte Etat_Sauvegarde[];//1
    public byte Mode_Commande[];//1
    public byte ConsigneBoucle_Ouverte_Moteur_Droit[];//4
    public byte ConsigneBoucle_Ouverte_Moteur_Gauche[];//4
    public byte Status_Lidar[];//1
    public byte Status_Moteur_Lidar[];//1
    public byte Erreur_Code_Lidar[];//2
    public byte Compas[];//4
    public byte ConsigneAngulaireNulle[];//4
    public byte Compensation_Ligne_Droite[];//32



    public void createFromBytes(ByteBuffer buf) throws IOException{

      /*  byte[] b = new byte[buf.remaining()];
        buf.get(b);*/

        buf.flip();
        buf.compact();

        Transport = new byte[16];
        Etat_Moteur_Droit = new byte[20];
        Etat_Moteur_Gauche = new byte[20];
        ConsigneManuelle = new byte[8];
        VitesseMesure = new byte[8];
        Phare_Luminosite = new byte[2];
        Temperature_Exterieure = new byte[2];

        Etat_Sauvegarde = new byte[1];
        Mode_Commande = new byte[1];
        ConsigneBoucle_Ouverte_Moteur_Droit = new byte[4];
        ConsigneBoucle_Ouverte_Moteur_Gauche = new byte[4];
        Status_Lidar = new byte[1];
        Status_Moteur_Lidar = new byte[1];
        Erreur_Code_Lidar = new byte[2];
        Compas = new byte[4];
        ConsigneAngulaireNulle = new byte[4];
        Compensation_Ligne_Droite = new byte[32];


        for(int i = 0; i <= 15; i++) {
            this.Transport[i] = buf.get();
        }
        for(int i = 0; i <= 19; i++) {
            this.Etat_Moteur_Droit[i] = buf.get();
        }
        for(int i = 0; i <= 19; i++) {
            this.Etat_Moteur_Gauche[i] = buf.get();
        }
        for(int i = 0; i <= 7; i++) {
            this.ConsigneManuelle[i] = buf.get();
        }
        for(int i = 0; i <= 7; i++) {
            this.VitesseMesure[i] = buf.get();
        }
        for(int i = 0; i <= 1; i++) {
            this.Phare_Luminosite[i] = buf.get();
        }
        for(int i = 0; i <= 1; i++) {
            this.Temperature_Exterieure[i] = buf.get();
        }
        /*
        for(int i = 0; i <= 1; i++) {
            this.V_Batterie[i] = buf.get();
        }*/
        this.V_Batterie = buf.getShort();

        this.Etat_Sauvegarde[0] = buf.get();

        this.Mode_Commande[0] = buf.get();

        for(int i = 0; i <= 3; i++) {
            this.ConsigneBoucle_Ouverte_Moteur_Droit[i] = buf.get();
        }
        for(int i = 0; i <= 3; i++) {
            this.ConsigneBoucle_Ouverte_Moteur_Gauche[i] = buf.get();
        }
        this.Status_Lidar[0] = buf.get();

        this.Status_Moteur_Lidar[0] = buf.get();

        for(int i = 0; i <= 1; i++) {
            this.Erreur_Code_Lidar[i] = buf.get();
        }
        for(int i = 0; i <= 3; i++) {
            this.Compas[i] = buf.get();
        }
        for(int i = 0; i <= 3; i++) {
            this.ConsigneAngulaireNulle[i] = buf.get();
        }
        for(int i = 0; i <= 31; i++) {
            this.Compensation_Ligne_Droite[i] = buf.get();
        }
    }
public short getPhare_Luminosite(){
    short value;
    Log.i("Debug","getPhare");
    value = this.V_Batterie;
//            value = byteArrayToInt(this.V_Batterie);
            return value;
}
/*
    public static int byteArrayToInt(byte[] b)
    {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
*/
    public static int byteArrayToInt(byte[] b)
    {
        return   b[1] & 0xFF |
                (b[0] & 0xFF) << 8;
    }

}

