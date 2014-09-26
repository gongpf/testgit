package com.example.connectiontest.remote;

public class RemoteEventType
{
    /**
     * The value of remote message type, handle the remote message by the type.
     */
    static public final int MSG_ACTION_BASE = 10000;

    static public final int MSG_KEY_CLICK = MSG_ACTION_BASE + 1;

    static public final int MSG_MOUSE_CLICK = MSG_ACTION_BASE + 2;

    static public final int MSG_MOUSE_MOVE = MSG_ACTION_BASE + 2;

    static public final int MSG_STATISTIC = MSG_ACTION_BASE + 3;
}
