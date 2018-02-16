


import com.mysql.cj.xdevapi.JsonArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.omg.PortableInterceptor.SUCCESSFUL;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.HashMap;

public class main {
    private static Connection connect = null;
    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    public static void main(String[] args){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager
                    .getConnection("jdbc:mysql://139.99.8.128/beyonity_albums?useUnicode=true&characterEncoding=utf-8&"
                            + "user=beyonity_admin&password=@Beyonity2017");

            printLinks();
            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(new FileReader("authuser.json"));
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray array = (JSONArray) jsonObject.get("users");
                int count = 0;
               /* for(Object object : array){
                    JSONObject jObject = (JSONObject)object;
                    //System.out.printf("%d \temail:%s \tDisplay Name:%s\n",count,String.valueOf(jObject.get("email")),String.valueOf(jObject.get("displayName")));
                 //   insertIntoDatabase(String.valueOf(jObject.get("email")),String.valueOf(jObject.get("displayName")));
                   /* if(!isAlreadyInDb(String.valueOf(jObject.get("email")))){
                        insertIntoDatabase(String.valueOf(jObject.get("email")),String.valueOf(jObject.get("displayName")));
                        System.out.print("successfully inserted : ");
                        System.out.printf("%d \temail:%s \tDisplay Name:%s\n",count,String.valueOf(jObject.get("email")),String.valueOf(jObject.get("displayName")));
                        count++;
                    }


                }*/

                Object obj2 = parser.parse(new FileReader("firebase.json"));
               // JSONArray jsonObject2 = (JSONArray) obj2;
                HashMap<String,JSONObject> value = (HashMap<String, JSONObject>) obj2;
                for(String key : value.keySet()){
                    //System.out.println(key);
                    for(Object o : array){
                        JSONObject jobject = (JSONObject)o;
                        if(key.equals(jobject.get("localId"))){
                            HashMap<String,JSONObject> fav = value.get(key);
                            HashMap<String,JSONObject> movies = fav.get("Fav Songs");
                            //System.out.println(jobject.get("email")+" : ");
                            int id = getUserId(String.valueOf(jobject.get("email")));
                            if(id>0) {
                                System.out.println(jobject.get("email")+" : ");
                                for (String names : movies.keySet()) {
                                    System.out.print(names + ": ");
                                    HashMap<String, JSONObject> songs = movies.get(names);
                                    for (String s : songs.keySet()) {
                                        int song_id = getSongId(s);
                                        if(song_id>0){
                                            if(checkFavEntry(id,song_id)) {
                                                System.out.printf("user id %d and song id %d already exists ",id,song_id);
                                            }else {
                                                addFav(id, song_id);
                                                System.out.print(s+" ");
                                            }
                                        }

                                    }
                                    System.out.println();
                                }
                                count++;
                            }
                            System.out.println();
                            System.out.println();

                        }

                    }

                }

            System.out.println(count);



            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void insertIntoDatabase(String email,String displayName){
            String query = "insert into users(email,displayName) values(?,?)";
        try {
            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,displayName);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAlreadyInDb(String email){
        String query = "Select id from users where email = '"+email+"'";
        try {
            Statement statement = connect.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()){
               // System.out.println(set.getString("id")+" email "+email);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void printLinks(){
        String query = "Select song_id, song_title from songs where download_link like '%firebase%'";
        if(connect != null){
            try {
                Statement st = connect.createStatement();
                ResultSet set = st.executeQuery(query);
                while (set.next()){
                    System.out.println(set.getString("song_id"));
                    System.out.println(set.getString("song_title"));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getUserId(String email){
        int id = 0;
        String query = "Select id from users where email = '"+email+"'";
        try {
            Statement statement = connect.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()){
                // System.out.println(set.getString("id")+" email "+email);
                return set.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    public static int getSongId(String song){
        int id = 0;
        String query = "Select song_id from songs where song_title = '"+song+"'";
        try {
            Statement statement = connect.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()){
                // System.out.println(set.getString("id")+" email "+email);
                return set.getInt("song_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static void addFav(int user_id,int song_id){
        String query = "insert into rahman_favorites(user_id,song_id) values(?,?)";
        try {
            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setInt(1,user_id);
            preparedStatement.setInt(2,song_id);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkFavEntry(int user_id,int song_id){
        String query = "Select * from rahman_favorites where user_id = '"+user_id+"' AND song_id = '"+song_id+"'";
        try {
            Statement statement = connect.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()){
                // System.out.println(set.getString("id")+" email "+email);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
