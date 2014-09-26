package com.example.connectiontest.ui;

import com.example.connectiontest.message.IMessage;

public class ChatMessage extends IMessage
{
    /**
     * 
     */
    private static final long serialVersionUID = -7215517557309613537L;
    
    private String mString ;
    
    public ChatMessage(String string)
    {
        this.mString = string;
    }
    
    public String getString()
    {
        return this.mString;
    }
}
