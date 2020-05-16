package com.company;

class Token
{

    private final String value;
    private final TokenType type;


    public Token(String value, TokenType type)
    {
        this.value = value;
        this.type = type;
    }
    public TokenType getType() { return type; }
    public String getValue() { return value; }
}