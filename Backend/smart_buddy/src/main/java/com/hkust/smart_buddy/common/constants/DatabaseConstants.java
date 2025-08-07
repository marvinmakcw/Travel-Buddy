package com.hkust.smart_buddy.common.constants;

import lombok.experimental.UtilityClass;

/**
 * Common database column names and related constants
 */
@UtilityClass
public class DatabaseConstants {

    // Common table column names
    public static final String RECORD_ID = "RECORD_ID";
    public static final String CREATED_BY = "CREATED_BY";
    public static final String CREATED_DT = "CREATED_DT";
    public static final String LAST_MODIFY_BY = "LAST_MODIFY_BY";
    public static final String LAST_MODIFY_DT = "LAST_MODIFY_DT";
    public static final String RECORD_VERSION = "RECORD_VERSION";

    // User table columns
    public static final String USER_ID = "USERID";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String EMAIL = "EMAIL";

    // Message table columns
    public static final String MESSAGE_ID = "MESSAGEID";
    public static final String CONTENT = "CONTENT";
    public static final String SENDER = "SENDER";

    // Table names
    public static final String USER_TABLE = "user";
    public static final String MESSAGE_TABLE = "MESSAGE";

    // Default values
    public static final String DEFAULT_CREATED_BY = "Admin";
    public static final Long DEFAULT_RECORD_VERSION = 0L;
}
