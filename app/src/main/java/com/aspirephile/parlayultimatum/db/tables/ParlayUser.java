package com.aspirephile.parlayultimatum.db.tables;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ParlayUser {

    private String username;
    private String fName;
    private String lName;
    private String reputation;

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
