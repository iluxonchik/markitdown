package iluxonchik.github.io.markitdown;


import android.provider.BaseColumns;

public final class MarkItDownDbContract {
    public static final String DB_NAME = "MarkItDown.db";
    public static final int DB_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    // Prevent someone from accidentally instantiating the class
    private MarkItDownDbContract() {}

    public static abstract class Colors implements BaseColumns {

        public static final String TABLE_NAME = "Colors";
        public static final String COLUMN_NAME_COLOR_HEX = "ColorHex";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
                +  COLUMN_NAME_COLOR_HEX + TEXT_TYPE
                + ");";
    }

    public static abstract class Tags implements BaseColumns {

        public static final String TABLE_NAME = "Tags";
        public static final String COLUMN_NAME_TITLE = "Title";
        public static final String COLUMN_NAME_COLOR = "Color";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
                +  COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP
                + COLUMN_NAME_COLOR + INTEGER_TYPE + " REFERENCES " + Colors.TABLE_NAME + "(_id)"
                + ");";
    }

    public static abstract class Notebooks implements BaseColumns {

        public static final String TABLE_NAME = "Notebooks";
        public static final String COLUMN_NAME_TITLE = "Title";
        public static final String COLUMN_NAME_COLOR = "Color";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
                +  COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP
                + COLUMN_NAME_COLOR + INTEGER_TYPE + " REFERENCES " + Colors.TABLE_NAME + "(_id)"
                + ");";
    }

    public static abstract class Notes implements BaseColumns {

        public static final String TABLE_NAME = "Notes";
        public static final String COLUMN_NAME_TITLE = "Title";
        public static final String COLUMN_NAME_TEXT_MARKDOWN = "TextMD";
        public static final String COLUMN_NAME_TEXT_HTML = "TextHTML";
        public static final String COLUMN_NAME_NOTEBOOK = "Notebook";
        public static final String COLUMN_NAME_TAGS = "Tags";
        public static final String COLUMN_NAME_EDITED = "Edited";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
                +  COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP
                + COLUMN_NAME_TEXT_MARKDOWN + TEXT_TYPE
                + COLUMN_NAME_TEXT_HTML + COMMA_SEP
                + COLUMN_NAME_NOTEBOOK + INTEGER_TYPE +" REFERENCES " + Notebooks.TABLE_NAME + "(_id)" + COMMA_SEP
                + COLUMN_NAME_TAGS + TEXT_TYPE + COMMA_SEP
                + COLUMN_NAME_EDITED + INTEGER_TYPE
                + ");";
    }

}
