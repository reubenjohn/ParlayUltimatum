package com.aspirephile.parlayultimatum.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExecuteQueryResult {
    final ResultSet resultSet;
    final SQLException sqlException;

    public ExecuteQueryResult(ResultSet resultSet, SQLException e) {
        this.resultSet = resultSet;
        this.sqlException = e;
    }
}
