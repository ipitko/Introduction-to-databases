/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Iiris
 */
import java.sql.*;
import java.util.*;
import org.sqlite.jdbc3.JDBC3ResultSet;

public class Tietokanta {
    public static void main(String[] args) throws SQLException {
        Scanner lukija = new Scanner(System.in);
        Connection db = DriverManager.getConnection("jdbc:sqlite:kurssit.db");

        while(true){
            System.out.println("Valitse toiminto: ");
            String komento = lukija.nextLine();
            if(komento.equals("5")){
                break;
            }
            if(komento.equals("1")){
                System.out.println("Anna vuosi: ");
                String vuosi = lukija.nextLine();
                vuosi="%"+vuosi+"%";
                PreparedStatement p = db.prepareStatement("SELECT SUM(K.laajuus) summa FROM Kurssit K, Suoritukset S "
                        + "WHERE S.kurssi_id=K.id AND S.paivays LIKE ?");
                p.setString(1,vuosi);
                ResultSet r = p.executeQuery();
                while (r.next()) {
                    System.out.println("Opintopisteiden määrä: "+r.getInt("summa"));
                }
            }
            if(komento.equals("2")){
                System.out.println("Anna opiskelijan nimi: ");
                String nimi = lukija.nextLine();
                PreparedStatement p = db.prepareStatement("SELECT K.nimi nimi, K.laajuus op,"
                        + " S.paivays paivays, S.arvosana arvosana "
                        + "FROM Kurssit K, Suoritukset S, Opiskelijat O "
                        + "WHERE K.id=S.kurssi_id AND S.opiskelija_id="
                        + "O.id AND O.nimi=? ORDER BY S.paivays");
                p.setString(1,nimi);
                ResultSet r = p.executeQuery();
                JDBC3ResultSet x = (JDBC3ResultSet) p.executeQuery();
                
                if(r.next()){
                  for (int i = 0; i < x.getColumnCount(); i++) {
                        System.out.print(x.getColumnName(i+1)+"   ");
                    }
                    System.out.println("");
                    while (r.next()) {
                        System.out.println(r.getString("nimi")+"   "+r.getInt("op")+"   "+r.getString("paivays")
                        +"   "+r.getInt("arvosana"));
                    }  
                }else{
                    System.out.println("Opiskelijaa ei löytynyt");
                }
            }
            if(komento.equals("3")){
                System.out.println("Anna kurssin nimi: ");
                String kurssi = lukija.nextLine();
                PreparedStatement p = db.prepareStatement("SELECT ROUND(AVG(S.arvosana),2) keskiarvo "
                        + "FROM Suoritukset S, Kurssit K"
                        + " WHERE kurssi_id=K.id AND K.nimi=?");
                p.setString(1,kurssi);
                ResultSet r = p.executeQuery();
                if(r.next()){
                    System.out.println("Keskiarvo: "+r.getDouble("keskiarvo"));
                }else{
                    System.out.println("Kurssia ei löytynyt");
                }
            }
            if(komento.equals("4")){
                System.out.println("Anna opettajien määrä: ");
                String lkm = lukija.nextLine();
                PreparedStatement p = db.prepareStatement("SELECT O.nimi nimi, SUM(K.laajuus) op "
                        + "FROM Opettajat O, Kurssit K, Suoritukset S "
                        + "WHERE K.id=S.kurssi_id AND O.id=K.opettaja_id "
                        + "GROUP BY K.opettaja_id "
                        + "ORDER BY SUM(K.laajuus) DESC LIMIT ?");
                p.setString(1,lkm);
                ResultSet r = p.executeQuery();
                JDBC3ResultSet x = (JDBC3ResultSet) p.executeQuery();
                for (int i = 0; i < x.getColumnCount(); i++) {
                        System.out.print(x.getColumnName(i+1)+"             ");
                    }
                    System.out.println("");
                    while (r.next()) {
                        System.out.println(r.getString("nimi")+"   "+r.getInt("op"));
                    }
            }
        }
    }
}
