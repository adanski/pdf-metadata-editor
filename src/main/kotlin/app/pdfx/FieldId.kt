package app.pdfx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface FieldID {
    String value();

    enum FieldType {
        STRING,
        TEXT,
        LONG,
        INT,
        DATE,
        BOOL
    }

    FieldType type() default FieldType.STRING;
}
