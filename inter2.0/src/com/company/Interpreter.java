package com.company;

import java.util.ArrayList;

public class Interpreter
{
    public final ArrayList<Variable> variablesTable;
    public final Interpreter parent;

    Interpreter(ArrayList<AST_TYPE> AST, Interpreter parent) throws LanguageException
    {
        this.variablesTable = new ArrayList<>();
        this.parent = parent;


        /// Выполнение всех веток дерева
        for(AST_TYPE ast : AST)
        {
            if(ast instanceof AST_VARIABLE_DECLARATION)
                process_AST_VARIABLE_DECLARATION((AST_VARIABLE_DECLARATION) ast);
            else if(ast instanceof AST_ASSIGNMENT_STATEMENT)
                process_AST_ASSIGNMENT_STATEMENT((AST_ASSIGNMENT_STATEMENT) ast);
            else if(ast instanceof AST_IF)
                process_AST_IF((AST_IF) ast);
            else if(ast instanceof AST_WHILE)
                process_AST_WHILE((AST_WHILE) ast);
            else if(ast instanceof AST_DO)
                process_AST_DO((AST_DO) ast);
            else
                throw new LanguageException("Неизвестное дерево");
        }


        /// Вывод переменных
        for(Variable var : variablesTable)
        {
            System.out.print(var.getID() + " = ");
            if(var.getValue() != null)
                System.out.println(var.getValue().getValue());
            else
                System.out.println("NULL");
        }
    }



    /**
     * Обработка Деревьев
     */
    private void process_AST_VARIABLE_DECLARATION(AST_VARIABLE_DECLARATION ast_variable_declaration) throws LanguageException
    {
        if(checkInVariablesTable(ast_variable_declaration.getID_token().getValue()) == null)
            if(ast_variable_declaration.tokens.size() != 0)
                addVariable(ast_variable_declaration.getID_token().getValue(), new RPN(ast_variable_declaration.tokens, this).getResult());
            else
                addVariable(ast_variable_declaration.getID_token().getValue(), null);
        else
            throw new LanguageException("Переменная уже существует");
    }




    private void process_AST_ASSIGNMENT_STATEMENT(AST_ASSIGNMENT_STATEMENT ast_assignment_statement) throws LanguageException
    {
        Variable var = checkInVariablesTable(ast_assignment_statement.getID_token().getValue());

        if(var != null)
            var.setValue(new RPN(ast_assignment_statement.tokens, this).getResult());
        else
            throw new LanguageException("Переменной не существует");
    }

    private void process_AST_WHILE(AST_WHILE ast_while) throws LanguageException
    {

        Token res = new RPN(ast_while.getArgs(), this).getResult();

        if(res.getType() == TokenType.BOOLEAN)
        {
            while (res.getValue().compareTo("true") == 0)
            {
                new Interpreter(ast_while.get_while_ast(), this);
                res = new RPN(ast_while.getArgs(), this).getResult();

                if(res.getType() != TokenType.BOOLEAN)
                    throw new LanguageException("Некорректный тип, ожидалось : BOOLEAN");
            }
        }
        else
            throw new LanguageException("Некорректный тип, ожидалось : BOOLEAN");

    }


    private void process_AST_IF(AST_IF ast_if) throws LanguageException
    {

        Token res = new RPN(ast_if.getArgs(), this).getResult();

        if(res.getType() == TokenType.BOOLEAN)
        {
            if(res.getValue().compareTo("true") == 0)
                new Interpreter(ast_if.get_if_ast(), this);
            else
            if(ast_if.get_else_ast() != null)
                new Interpreter(ast_if.get_else_ast(), this);
        }
        else
            throw new LanguageException("Некорректный тип, ожидалось : BOOLEAN");


    }


    private void process_AST_DO(AST_DO ast_do) throws LanguageException
    {
        Token res;

        do
        {
            new Interpreter(ast_do.get_do_ast(), this);
            res = new RPN(ast_do.getArgs(), this).getResult();

            if(res.getType() != TokenType.BOOLEAN)
                throw new LanguageException("Некорректный тип, ожидалось : BOOLEAN");
        }
        while (res.getValue().compareTo("true") == 0);

    }

    /**
     * Добавление переменной
     */
    private void addVariable(String ID, Token value)
    {
        variablesTable.add(new Variable(ID, value));
    }


    /**
     * Проверка есть ли такая переменная
     */
    private Variable checkInVariablesTable(String ID)
    {
        Interpreter currentInterpreter = this;
        while (currentInterpreter != null)
        {
            for(Variable var : currentInterpreter.variablesTable)
                if(var.getID().compareTo(ID) == 0)
                    return var;

            currentInterpreter = currentInterpreter.parent;
        }


        return null;
    }
}



/**
 * Класс переменая
 */
class Variable
{
    private final String ID;
    private Token value;

    Variable(String ID, Token value)
    {
        this.ID = ID;
        this.value = value;
    }

    public Token getValue() { return value; }
    public String getID() { return ID; }
    public void setValue(Token value) { this.value = value; }
}