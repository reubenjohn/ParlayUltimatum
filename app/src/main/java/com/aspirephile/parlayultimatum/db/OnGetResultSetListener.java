package com.aspirephile.parlayultimatum.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface OnGetResultSetListener {
    void onGetResultSet(ResultSet rs, SQLException e);
}
