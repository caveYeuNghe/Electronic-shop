package Java.Dao;

import Java.Model.User;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Login {
    public static User login(String name, String pass) throws SQLException {
        Conn conn = new Conn();
        User user = null;
        String query = "select * from user where Ten='"+name+"' and MatKhau='"+pass+"'";
        ResultSet rs = conn.s.executeQuery(query);
        if(rs.next())
        {
            int isAdmin = rs.getInt("isAdmin");
            if(isAdmin == 0)
                user = new User(rs.getInt("id"),name, pass, false);
            else
                user = new User(rs.getInt("id"),name,pass,true);
        }
        return user;
    }

    public static boolean register(String name, String pass) throws SQLException {
        Conn conn = new Conn();
        String query = "select * from user where Ten='"+name+"' and MatKhau='"+pass+"'";
        ResultSet rs = conn.s.executeQuery(query);
        if(rs.next())
            return false;
        String q = "insert into user(Ten, MatKhau, isAdmin) values('"+name+"','"+pass+"',0)";
        conn.s.executeUpdate(q);
        return true;
    }
}