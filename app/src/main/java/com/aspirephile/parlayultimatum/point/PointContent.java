package com.aspirephile.parlayultimatum.point;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class PointContent {

    /**
     * An array of sample (point) items.
     */
    public static final List<PointItem> ITEMS = new ArrayList<>();


    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(PointItem item) {
        ITEMS.add(item);
    }

    private static PointItem createDummyItem(int position) {
        return new PointItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A point item representing a piece of content.
     */
    public static class PointItem {
        public final String id;
        public final String content;
        public final String details;

        public PointItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
