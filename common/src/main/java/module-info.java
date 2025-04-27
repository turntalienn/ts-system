module com.turntalienn.mytrade.common {
    requires java.logging;
    requires java.sql;
    requires cfg4j.core;
    requires org.slf4j;
    exports com.turntalienn.mytrade.common.time;
    exports com.turntalienn.mytrade.common.file;
}