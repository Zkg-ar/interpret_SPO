package com.company;

import java.util.ArrayList;

public class AST
{
    private final ArrayList<Token> tokens;
    private int position = 0;



    private final ArrayList<AST_TYPE> ast_tree = new ArrayList<>();
    private AST_TYPE current_ast;

    public ArrayList<AST_TYPE> getAST() { return  ast_tree; }


    /**
     *
     * Теперь есть массив деревьев, где хранятся все ветки
     */
    public AST(ArrayList<Token> tokens) throws LanguageException
    {
        this.tokens = tokens;


        while(position < tokens.size())
        {
            createAST();
            ast_tree.add(current_ast);
        }
    }


    /**
     *
     * Создание дерева
     */
    private void createAST() throws LanguageException
    {
        Token firstToken = tokens.get(position);
        ++position;


        ///Проверка по 1 токену, var и variable на даный момент доступно
        switch (firstToken.getType())
        {
            case VARIABLE_DECLARATION:
                processVarDeclaration();
                break;

            case VARIABLE:
                processAssignmentState(firstToken);
                break;

            case WHILE:
                processWhile();
                break;

            case DO:
                processDo();
                break;

            case IF:
                processIF();
                break;

            default:
                throw new LanguageException("Ошибка");

        }
    }



    private void processIF() throws LanguageException
    {

        Token currentToken = tokens.get(position);


        current_ast = new AST_IF();


        while(currentToken.getType() != TokenType.BEGIN)
        {
            ++position;
            ((AST_IF)current_ast).add_args(currentToken);
            currentToken = tokens.get(position);

        }

        ++position;
        ast_add_block();


        currentToken = tokens.get(position);
        ++position;


        if(currentToken.getType() == TokenType.END)
        {
            current_ast.process();
            current_ast.tokens = null;
        }
        else
        {
            current_ast.process();
            current_ast.tokens = new ArrayList<>();


            ast_add_block();

            current_ast.process();
            current_ast.tokens = null;

            ++position;
        }
    }


    private void ast_add_block()
    {
        Token currentToken;
        int[] begin_end = {1, 0};

        for(;position < tokens.size(); position++)
        {
            currentToken = tokens.get(position);

            if(currentToken.getType() == TokenType.BEGIN)
            {
                ++begin_end[0];

                current_ast.add(currentToken);
            }
            else if(currentToken.getType() == TokenType.END)
            {
                ++begin_end[1];

                if(begin_end[0] == begin_end[1])
                    break;
                else
                    current_ast.add(currentToken);
            }
            else if(currentToken.getType() == TokenType.ELSE)
            {
                if (begin_end[0] == begin_end[1] + 1)
                    break;
                else
                    current_ast.add(currentToken);
            }
            else
                current_ast.add(currentToken);

        }
    }



    private void processWhile() throws LanguageException
    {

        Token currentToken = tokens.get(position);

        current_ast = new AST_WHILE();


        while(currentToken.getType() != TokenType.BEGIN)
        {
            ++position;
            ((AST_WHILE)current_ast).add_args(currentToken);
            currentToken = tokens.get(position);
        }

        ++position;
        ast_add_block();
        ++position;


        current_ast.process();
    }

    private void processDo() throws LanguageException
    {
        ++position;
        Token currentToken;

        current_ast = new AST_DO();


        ast_add_block();
        position+=2;

        currentToken = tokens.get(position);


        while(currentToken.getType() != TokenType.EndOfStr)
        {

            ++position;
            ((AST_DO)current_ast).add_args(currentToken);
            currentToken = tokens.get(position);
        }

        current_ast.process();
        ++position;
    }


    private void processVarDeclaration() throws LanguageException
    {
        Token currentToken = tokens.get(position);

        current_ast = new AST_VARIABLE_DECLARATION(currentToken);

        ++position;

        currentToken = tokens.get(position);
        if(currentToken.getType() != TokenType.ASSIGN_OP)
        {
            ++position;
            return;
        }

        ++position;

        for(;position < tokens.size(); position++)
        {
            currentToken = tokens.get(position);

            if(currentToken.getType() != TokenType.EndOfStr)
                current_ast.add(currentToken);
            else
            {
                ++position;
                break;
            }

        }

        current_ast.process();

    }


    private void processAssignmentState(Token ID) throws LanguageException
    {
        Token currentToken;

        current_ast = new AST_ASSIGNMENT_STATEMENT(ID);

        ++position;

        for(;position < tokens.size(); position++)
        {
            currentToken = tokens.get(position);

            if(currentToken.getType() != TokenType.EndOfStr)
                current_ast.add(currentToken);
            else
            {
                ++position;
                break;
            }
        }

        current_ast.process();

    }
}




class AST_VARIABLE_DECLARATION extends AST_TYPE
{
    private Token ID_token;

    AST_VARIABLE_DECLARATION(Token ID) { setID_token(ID); }

    private void setID_token(Token ID_token){ this.ID_token = ID_token; }
    public Token getID_token() { return ID_token; }

}


class AST_ASSIGNMENT_STATEMENT extends AST_TYPE
{
    private Token ID_token;

    AST_ASSIGNMENT_STATEMENT(Token ID) { setID_token(ID); }

    private void setID_token(Token ID_token){ this.ID_token = ID_token; }
    public Token getID_token() { return ID_token; }

}


class AST_IF extends AST_TYPE
{
    private final ArrayList<Token> args;


    private ArrayList<AST_TYPE> if_ast = null;
    private ArrayList<AST_TYPE> else_ast = null;


    AST_IF() { args = new ArrayList<>(); }

    public void add_args(Token token) { args.add(token); }

    @Override
    protected void process() throws LanguageException
    {

        if(if_ast == null)
            if_ast = new AST(this.tokens).getAST();
        else
            else_ast = new AST(this.tokens).getAST();

    }

    public ArrayList<Token> getArgs() { return args; }

    public ArrayList<AST_TYPE> get_if_ast() { return if_ast; }

    public ArrayList<AST_TYPE> get_else_ast() { return else_ast; }

}


class  AST_WHILE extends AST_TYPE
{
    private final ArrayList<Token> args;

    private ArrayList<AST_TYPE> while_ast;

    AST_WHILE() { args = new ArrayList<>(); }

    public void add_args(Token token) { args.add(token); }

    @Override
    protected void process() throws LanguageException
    {
        while_ast = new AST(this.tokens).getAST();
        tokens = null;
    }

    public ArrayList<Token> getArgs() { return args; }
    public ArrayList<AST_TYPE> get_while_ast() { return while_ast; }

}

class AST_DO extends AST_TYPE
{
    private final ArrayList<Token> args;

    private ArrayList<AST_TYPE> do_ast;

    AST_DO()
    { args = new ArrayList<>(); }

    public void add_args(Token token) { args.add(token); }

    @Override
    protected void process() throws LanguageException
    {
        do_ast = new AST(this.tokens).getAST();
        tokens = null;
    }

    public ArrayList<Token> getArgs() { return args; }
    public ArrayList<AST_TYPE> get_do_ast() { return do_ast; }
}





class AST_TYPE
{
    AST_TYPE() { tokens = new ArrayList<>(); }

    protected ArrayList<Token> tokens;


    protected void add(Token token){ this.tokens.add(token); }
    protected void process() throws LanguageException { }
}