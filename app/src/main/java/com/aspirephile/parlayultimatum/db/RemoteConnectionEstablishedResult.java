package com.aspirephile.parlayultimatum.db;

import java.sql.SQLException;

public class RemoteConnectionEstablishedResult {
    public final ParlayUltimatumConnection remoteConnection;
    public final SQLException sqlException;

    public RemoteConnectionEstablishedResult(ParlayUltimatumConnection remoteConnection, SQLException e) {
        this.remoteConnection = remoteConnection;
        this.sqlException = e;
    }
}
