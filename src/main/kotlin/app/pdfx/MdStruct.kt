package app.pdfx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface MdStruct {
    String name() default "";

    enum StructType {
        MD_STRUCT,
        MD_ENABLE_STRUCT,
    }

    StructType type() default StructType.MD_STRUCT;

    enum Access {
        READ_ONLY,
        READ_WRITE,
    }

    Access access() default Access.READ_WRITE;
}
