package com.company;

import java.util.ArrayList;

public class Parser
{

    private final ArrayList<Token> tokens;
    private int counter = 0;


    Parser(ArrayList<Token> tokens) throws LanguageException
    {
        this.tokens = tokens;
        startLanguageAnalysis();
    }


    private void startLanguageAnalysis() throws LanguageException
    {
        while (counter < tokens.size())
            if (!assign_expression())
                if (!if_expression())
                    if (!while_expression())
                        if (!do_expression())
                            if(!linked_list_expression())
                                throw new LanguageException("Ошибка в выражении");
    }

    private boolean linked_list_expression() throws LanguageException
    {
        if(LINKED_LIST())
            if(VARIABLE())
                if(ASSIGN_OPERAND())
                    if (NEW())
                        if (END_OF_STRING())
                            return true;
                        else
                            throw new LanguageException("Ошибка : EndOfStr не найдено");
                    else
                        throw new LanguageException("Ошибка : new пропущено");
                else
                    throw new LanguageException("Ошибка : = пропущено");

        return false;
    }


    private boolean assign_expression() throws LanguageException
    {
        if(VARIABLE_DECLARATION())
        {
            if(VARIABLE())
                if(END_OF_STRING())
                    return true;
                else if(ASSIGN_OPERAND())
                {
                    expression();
                    if(END_OF_STRING())
                        return true;
                    else
                        throw new LanguageException("Ошибка : EndOfStr пропущено");
                }
                else
                    throw new LanguageException("Ошибка в обработке выражения");
        }
        else
        {
            if(VARIABLE())
                if(ASSIGN_OPERAND())
                {
                    expression();
                    if(END_OF_STRING())
                        return true;
                    else
                        throw new LanguageException("Ошибка : EndOfStr пропущено");
                }
                else if(SHARP())
                {
                    if(FUNCTION_CALL())
                        if(END_OF_STRING())
                            return true;
                        else
                            throw new LanguageException("Ошибка : Неизвестная функция или неверные аргументы");
                }
                else
                    throw new LanguageException("Ошибка в обработке выражения");
        }

        return false;
    }

    private boolean FUNCTION_CALL() throws LanguageException
    {
        if(ADD_FORWARD() || ADD_BACKWARD())
        {
            if(LEFT_ROUNDED_BRACKET())
            {
                expression();
                return true;
            }

            return false;
        }
        else if(ADD() || SET())
        {
            if(LEFT_ROUNDED_BRACKET())
            {
                expression();
                if (COMMA())
                {
                    expression();
                    return true;
                }
            }

            return false;
        }
        else if(GET() || REMOVE())
        {
            if(LEFT_ROUNDED_BRACKET())
            {
                expression();
                return true;
            }

            return false;
        }
        else if(GET_SIZE())
        {
            if(LEFT_ROUNDED_BRACKET())
                if (RIGHT_ROUNDED_BRACKET())
                    return END_OF_STRING();

            return false;
        }

        return true;
    }


    private void expression() throws LanguageException
    {
        if (DIGIT() || VARIABLE())
            value(false);
        else if (LEFT_ROUNDED_BRACKET())
            value(true);
        else
            throw new LanguageException("Ошибка : некорректный токен");
    }


    private void value(boolean flag) throws LanguageException
    {
        while(!END_OF_STRING() && !BEGIN())
        {
            if(!flag)
            {
                if(!OPERATION())
                    if(RIGHT_ROUNDED_BRACKET())
                        flag = false;
                    else if(COMMA())
                    {
                        --counter;
                        return;
                    }
                    else if(SHARP())
                    {
                        if(!FUNCTION_CALL())
                            throw new LanguageException("Ошибка в обработке функции");
                        --counter;
                    }
                    else
                        throw new LanguageException("Пропущен знак операции");
                else
                    flag = true;
            }
            else
            {
                if(!DIGIT() && !VARIABLE())
                    if(LEFT_ROUNDED_BRACKET())
                        flag = true;
                    else
                        throw new LanguageException("Пропущено значение");
                else
                    flag = false;
            }

        }

        --counter;

        if(flag)
            throw new LanguageException("Пропущено значение");
    }


    private boolean if_expression() throws LanguageException
    {
        if(IF())
        {
            expression();
            if(BEGIN())
            {
                if_while_do_body();

                if(END())
                    return true;
                else if(ELSE())
                {
                    if_while_do_body();
                    if(END())
                        return true;
                    else
                        throw new LanguageException("Ошибка в IF : пропущен END");
                }
                else
                    throw new LanguageException("Ошибка в IF : пропущен END");
            }
            else
                throw new LanguageException("Ошибка в IF : пропущен BEGIN");
        }

        return false;
    }


    private boolean while_expression() throws LanguageException
    {
        if(WHILE())
        {
            expression();
            if(BEGIN())
            {
                if_while_do_body();
                if(END())
                    return true;
                else
                    throw new LanguageException("Ошибка в WHILE : пропущен END");
            }
            else
                throw new LanguageException("Ошибка в WHILE : пропущен BEGIN");
        }

        return false;
    }


    private boolean do_expression() throws LanguageException
    {
        if(DO())
        {
            if(BEGIN())
            {
                if_while_do_body();
                if(END())
                {
                    if (WHILE())
                    {
                        expression();
                        if (END_OF_STRING())
                            return true;
                        else
                            throw new LanguageException("Ошибка : EndOfStr пропущено");
                    }
                    else
                        throw new LanguageException("Ошибка в DO : пропущен WHILE");
                }
                else
                    throw new LanguageException("Ошибка в DO : пропущен END");
            }
            else
                throw new LanguageException("Ошибка в DO : пропущен BEGIN");
        }

        return false;
    }

    private void if_while_do_body() throws LanguageException {
        Token currentToken;
        int[] begin_end = {1, 0};

        ArrayList<Token> body = new ArrayList<>();

        for (; counter < tokens.size(); counter++) {
            currentToken = tokens.get(counter);

            if (BEGIN()) {
                ++begin_end[0];
                --counter;

                body.add(currentToken);
            } else if (END()) {
                ++begin_end[1];

                if (begin_end[0] == begin_end[1])
                    break;
                else
                    body.add(currentToken);
                --counter;
            } else if (ELSE()) {
                if (begin_end[0] == begin_end[1] + 1)
                    break;
                else
                    body.add(currentToken);
                --counter;
            } else
                body.add(currentToken);
        }

        new Parser(body);

        --counter;
    }


    private boolean LINKED_LIST() throws LanguageException { return match(TokenType.LINKED_LIST); }
    private boolean NEW() throws LanguageException { return match(TokenType.NEW); }

    private boolean VARIABLE_DECLARATION() throws LanguageException { return match(TokenType.VARIABLE_DECLARATION); }
    private boolean WHILE() throws LanguageException { return match(TokenType.WHILE); }
    private boolean DO() throws LanguageException { return match(TokenType.DO); }
    private boolean IF() throws LanguageException { return match(TokenType.IF); }


    private boolean BEGIN() throws LanguageException { return match(TokenType.BEGIN); }
    private boolean END() throws LanguageException { return match(TokenType.END); }
    private boolean ELSE() throws LanguageException { return match(TokenType.ELSE); }


    private boolean VARIABLE() throws LanguageException { return match(TokenType.VARIABLE); }
    private boolean DIGIT() throws LanguageException { return match(TokenType.DIGIT); }


    private boolean ASSIGN_OPERAND() throws LanguageException { return match(TokenType.ASSIGN_OP); }
    private boolean OPERATION() throws LanguageException { return match( TokenType.OP); }
    private boolean SHARP() throws LanguageException { return match( TokenType.SHARP); }
    private boolean COMMA() throws LanguageException { return match( TokenType.COMMA); }

    private boolean LEFT_ROUNDED_BRACKET() throws LanguageException { return match( TokenType.LeftRoundBracket); }
    private boolean RIGHT_ROUNDED_BRACKET() throws LanguageException { return match( TokenType.RightRoundBracket); }

    private boolean END_OF_STRING() throws LanguageException { return match(TokenType.EndOfStr); }


    private boolean ADD_FORWARD() throws LanguageException { return match(TokenType.ADD_FORWARD); }
    private boolean ADD_BACKWARD() throws LanguageException { return match(TokenType.ADD_BACKWARD); }
    private boolean ADD() throws LanguageException { return match(TokenType.ADD); }
    private boolean REMOVE() throws LanguageException { return match(TokenType.REMOVE); }
    private boolean GET() throws LanguageException { return match(TokenType.GET); }
    private boolean SET() throws LanguageException { return match(TokenType.SET); }
    private boolean GET_SIZE() throws LanguageException { return match(TokenType.GET_SIZE); }



    private boolean match(TokenType necessaryType) throws LanguageException
    {
        if(counter != tokens.size())
        {
            TokenType tokenType = tokens.get(counter).getType();

            if(tokenType == necessaryType)
            {
                ++counter;
                return true;
            }
            else
                return false;
        }
        else
            throw new LanguageException("Ошибка, ожидалось - " + necessaryType + "");

    }
}
