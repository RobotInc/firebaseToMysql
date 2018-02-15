


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
                int count = 1;
                for(Object object : array){
                    JSONObject jObject = (JSONObject)object;
                    //System.out.printf("%d \temail:%s \tDisplay Name:%s\n",count,String.valueOf(jObject.get("email")),String.valueOf(jObject.get("displayName")));
                 //   insertIntoDatabase(String.valueOf(jObject.get("email")),String.valueOf(jObject.get("displayName")));
                    if(!isAlreadyInDb(String.valueOf(jObject.get("email")))){
                        insertIntoDatabase(String.valueOf(jObject.get("email")),String.valueOf(jObject.get("displayName")));
                        System.out.print("successfully inserted : ");
                        System.out.printf("%d \temail:%s \tDisplay Name:%s\n",count,String.valueOf(jObject.get("email")),String.valueOf(jObject.get("displayName")));
                        count++;
                    }


                }
                System.out.println("count = "+count);
               /* Object obj2 = parser.parse(new FileReader("firebase.json"));
               // JSONArray jsonObject2 = (JSONArray) obj2;
                HashMap<String,JSONObject> value = (HashMap<String, JSONObject>) obj2;
                for(String key : value.keySet()){
                    System.out.println(key);
                }



               int count2 = 1;
                for(int a = 0;a< obj2. obj2){
                    JSONObject jObject = (JSONObject)object;
                    //System.out.printf("%d \temail:%s \tDisplay Name:%s\n",count,String.valueOf(jObject.get("email")),String.valueOf(jObject.get("displayName")));
                    //   insertIntoDatabase(String.valueOf(jObject.get("email")),String.valueOf(jObject.get("displayName")));
                    System.out.println(jObject);
                    count2++;
                }*/


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
}
