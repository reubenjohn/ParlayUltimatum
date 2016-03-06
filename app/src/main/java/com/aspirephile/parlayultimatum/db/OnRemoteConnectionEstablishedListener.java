package com.aspirephile.parlayultimatum.db;

import java.sql.SQLException;

public interface OnRemoteConnectionEstablishedListener {

    void onRemoteConnectionEstablishedListener(ParlayUltimatumConnection remoteConnection, SQLException e);
}
