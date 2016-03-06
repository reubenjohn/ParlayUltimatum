package com.aspirephile.parlayultimatum.db;

import java.sql.PreparedStatement;

public interface OnGetPreparedStatement {
    PreparedStatement onGetPreparedStatement(ParlayUltimatumConnection remoteConnection);
}
