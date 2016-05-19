package sevrain.test;

/*
Equivalent à la structure T_Buf_Reglage
 */

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;


public class Reglages {
    //Transport
    public final int Header1 = 0x55AA55AA;
    public final int Header2 = 0x65BA65BA;
    public short TypeMessage;
    public short Total_octet;
    public short Position_Premier_Octet = 0;
    public short Nombre_Doctets;

    //Etat_Moteur_Droit
    public short CoefP_D = 0;
    public short CoefI_D = 0;
    public short CoefD_D = 0;
    public short Commande_Moteur_D = 0;
    public short ErreurPrecedente_D = 0;
    public short ErreurSomme_D = 0;
    public short Consigne_D = 0;
    public short Encodeur_Delta_D = 0;
    public short Encodeur_Precedent_D = 0;
    public short Commande_D = 0;

    //Etat_Moteur_Gauche
    public short CoefP_G = 0;
    public short CoefI_G = 0;
    public short CoefD_G = 0;
    public short Commande_Moteur_G = 0;
    public short ErreurPrecedente_G = 0;
    public short ErreurSomme_G = 0;
    public short Consigne_G = 0;
    public short Encodeur_Delta_G = 0;
    public short Encodeur_Precedent_G = 0;
    public short Commande_G = 0;

    //T_ConsigneManuelle
    public float Vitesse = 0;
    public float ConsigneAngulaire = 0;
    public float VitesseMesure = 0;

    public short Phare_Luminosite = 1000;
    public short Temperature_Exterieure = 0;
    public short V_Batterie = 0;
    public byte Etat_Sauvegarde = 0;
    public byte Mode_Commande = 0;

    //ConsigneBoucle_Ouverte_Moteur_Droite
    public short Commande_Consigne_D = 0;
    public short Vitesse_D = 0;

    //ConsigneBoucle_Ouverte_Moteur_Gauche
    public short Commande_Consigne_G = 0;
    public short Vitesse_G = 0;

    public byte Status_Lidar = 0;
    public byte Status_Moteur_Lidar = 0;
    public short Erreur_Code_Lidar = 0;

    public float Compas = 0;
    public float ConsigneAngulaireNulle = 0;

    //T_Compensasion_Ligne_Droite
    //T_CoefAB Marche Avant
    public float CoefA_Av = 0;
    public float CoefB_Av = 0;
    //T_CoefAB Marche Arrière
    public float CoefA_Ar = 0;
    public float CoefB_Ar = 0;

    public int Crc;

    //METHODES
    public byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
    public byte[] shortToByteArray(short value) {
        return new byte[]{
                (byte) (value >> 8),
                (byte) value};
    }
    public byte [] floatToByteArray(float value) {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    public byte[] concatenateByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        outputStream.write(intToByteArray(Header1));
        outputStream.write(intToByteArray(Header2));
        outputStream.write(shortToByteArray(TypeMessage));
        outputStream.write(shortToByteArray(Total_octet));
        outputStream.write(shortToByteArray(Position_Premier_Octet));
        outputStream.write(shortToByteArray(Nombre_Doctets));
        outputStream.write(shortToByteArray(CoefP_D));
        outputStream.write(shortToByteArray(CoefI_D));
        outputStream.write(shortToByteArray(CoefD_D));
        outputStream.write(shortToByteArray(Commande_Moteur_D));
        outputStream.write(shortToByteArray(ErreurPrecedente_D));
        outputStream.write(shortToByteArray(ErreurSomme_D));
        outputStream.write(shortToByteArray(Consigne_D));
        outputStream.write(shortToByteArray(Encodeur_Delta_D));
        outputStream.write(shortToByteArray(Encodeur_Precedent_D));
        outputStream.write(shortToByteArray(Commande_D));
        outputStream.write(shortToByteArray(CoefP_G));
        outputStream.write(shortToByteArray(CoefI_G));
        outputStream.write(shortToByteArray(CoefD_G));
        outputStream.write(shortToByteArray(Commande_Moteur_G));
        outputStream.write(shortToByteArray(ErreurPrecedente_G));
        outputStream.write(shortToByteArray(ErreurSomme_G));
        outputStream.write(shortToByteArray(Consigne_G));
        outputStream.write(shortToByteArray(Encodeur_Delta_G));
        outputStream.write(shortToByteArray(Encodeur_Precedent_G));
        outputStream.write(shortToByteArray(Commande_G));
        outputStream.write(floatToByteArray(Vitesse));
        outputStream.write(floatToByteArray(ConsigneAngulaire));
        outputStream.write(floatToByteArray(VitesseMesure));
        outputStream.write(shortToByteArray(Phare_Luminosite));
        outputStream.write(shortToByteArray(Temperature_Exterieure));
        outputStream.write(shortToByteArray(V_Batterie));
        outputStream.write(Etat_Sauvegarde);
        outputStream.write(Mode_Commande);
        outputStream.write(shortToByteArray(Commande_Consigne_D));
        outputStream.write(shortToByteArray(Vitesse_D));
        outputStream.write(shortToByteArray(Commande_Consigne_G));
        outputStream.write(shortToByteArray(Vitesse_G));
        outputStream.write(Status_Lidar);
        outputStream.write(Status_Moteur_Lidar);
        outputStream.write(shortToByteArray(Erreur_Code_Lidar));
        outputStream.write(floatToByteArray(Compas));
        outputStream.write(floatToByteArray(ConsigneAngulaireNulle));
        outputStream.write(floatToByteArray(CoefA_Av));
        outputStream.write(floatToByteArray(CoefB_Av));
        outputStream.write(floatToByteArray(CoefA_Ar));
        outputStream.write(floatToByteArray(CoefB_Ar));

        byte result[] = outputStream.toByteArray( );
        return result;
    }

}


/*
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

      */
/*  byte[] b = new byte[buf.remaining()];
        buf.get(b);*//*


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
        */
/*
        for(int i = 0; i <= 1; i++) {
            this.V_Batterie[i] = buf.get();
        }*//*

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
*/
/*
    public static int byteArrayToInt(byte[] b)
    {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
*//*

    public static int byteArrayToInt(byte[] b)
    {
        return   b[1] & 0xFF |
                (b[0] & 0xFF) << 8;
    }

}
*/

