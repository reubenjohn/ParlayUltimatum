package com.aspirephile.parlayultimatum.db.tables;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ParlayUser {

    public String username;
    public String fName;
    public String lName;
    public String reputation;

    public ParlayUser(ResultSet rs) throws SQLException {
        username = rs.getString("username");
        fName = rs.getString("fName");
        lName = rs.getString("lName");
        reputation = rs.getString("reputation");
    }

    public String toString() {
        return "{username: " + username + ", fName: " + fName
                + ", lName: " + lName + ", reputation: " + reputation
                + "}";
    }

}
