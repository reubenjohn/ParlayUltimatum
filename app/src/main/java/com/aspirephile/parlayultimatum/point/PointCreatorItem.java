package com.aspirephile.parlayultimatum.point;

import org.kawanfw.sql.api.client.android.execute.update.SQLEntity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PointCreatorItem implements SQLEntity {
    private String title;
    private String description;
    private String username;

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getEntityName() {
        return "Point";
    }

    @Override
    public String[] getAttributeNames() {
        return new String[]{"username", "title", "description"};
    }

    @Override
    public int onPrepareStatement(PreparedStatement preparedStatement, int i) throws SQLException {
        preparedStatement.setString(i++, username);
        preparedStatement.setString(i++, title);
        preparedStatement.setString(i++, description);
        return i;
    }
}
