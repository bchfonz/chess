package dataaccess;

import com.mysql.cj.x.protobuf.MysqlxCrud.Delete;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    public DataAccessException(String message) {
        super(message);
    }
}
