package edu.tcu.cs.hogwartsartifactsonline.system;

public class StatusCode {

    public static final int SUCCESS = 200;

    public static final int INVALID_ARGUMENT = 400;

    public static final int UNAUTHORIZED = 401; //username and password

    public static final int FORBIDDEN = 403; // No permission

    public static final int NOT_FOUND = 404; // Not found

    public static final int INTERNAL_SERVER_ERROR = 500; // Server internal error

}
