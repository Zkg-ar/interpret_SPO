package com.company;

import java.util.ArrayList;
import java.util.Objects;

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
            else if(ast instanceof AST_LINKED_LIST)
                process_AST_LINKED_LIST((AST_LINKED_LIST) ast);
            else if(ast instanceof AST_FUNCTION_CALL_STATEMENT)
                process_AST_FUNCTION_CALL_STATEMENT((AST_FUNCTION_CALL_STATEMENT) ast);
            else
                throw new LanguageException("Неизвестное дерево");
        }


        /// Вывод переменных
        for(Variable var : variablesTable)
        {
            System.out.print(var.getID() + " = ");
            if(var.getValue() != null)
                if(var.getValue() instanceof Token)
                    System.out.println(((Token)var.getValue()).getValue());
                else if(var.getValue() instanceof LinkedList)
                    System.out.println("#LinkedList");
                else
                    System.out.println("NULL");
        }
    }


    private Token process_AST_FUNCTION_CALL_STATEMENT(AST_FUNCTION_CALL_STATEMENT ast_function_call_statement) throws LanguageException
    {
        Object ll = checkInVariablesTable(ast_function_call_statement.getID_token().getValue()).getValue();

        if(ll != null && ll instanceof LinkedList)
        {
            switch (ast_function_call_statement.functionID.getType())
            {
                case ADD_FORWARD:
                    ((LinkedList) ll).addForward(new RPN(ast_function_call_statement.params.get(0), this).getResult());
                    break;

                case ADD_BACKWARD:
                    ((LinkedList) ll).addBackward( new RPN(ast_function_call_statement.params.get(0), this).getResult());
                    break;

                case ADD:
                    ((LinkedList) ll).add( new RPN(ast_function_call_statement.params.get(0), this).getResult(),
                            Integer.parseInt(new RPN(ast_function_call_statement.params.get(1), this).getResult().getValue()));
                    break;

                case SET:
                    ((LinkedList) ll).set( new RPN(ast_function_call_statement.params.get(0), this).getResult(),
                            Integer.parseInt(new RPN(ast_function_call_statement.params.get(1), this).getResult().getValue()));
                    break;

                case GET:
                    return (Token) ((LinkedList) ll).get(Integer.parseInt(new RPN(ast_function_call_statement.params.get(0), this).getResult().getValue()));

                case REMOVE:
                    ((LinkedList) ll).remove(Integer.parseInt(new RPN(ast_function_call_statement.params.get(0), this).getResult().getValue()));
                    break;

                case GET_SIZE:
                    return new Token(Integer.toString(((LinkedList) ll).getSize()), TokenType.DIGIT);
            }
        }

        return null;
    }

    private void process_AST_LINKED_LIST(AST_LINKED_LIST ast_linked_list) throws LanguageException
    {
        if(checkInVariablesTable(ast_linked_list.getID_token().getValue()) == null)
            addVariable(ast_linked_list.getID_token().getValue(), new LinkedList<Token>());
        else
            throw new LanguageException("Переменная уже существует");
    }

    /**
     * Обработка Деревьев
     */
    private void process_AST_VARIABLE_DECLARATION(AST_VARIABLE_DECLARATION ast_variable_declaration) throws LanguageException
    {
        Variable var = checkInVariablesTable(ast_variable_declaration.getID_token().getValue());


        if(var == null)
            if(ast_variable_declaration.tokens.size() != 0)
            {
                try
                {
                    addVariable(ast_variable_declaration.getID_token().getValue(), new RPN(ast_variable_declaration.tokens, this).getResult());
                }
                catch (Exception ignored)
                {
                    Token res = process_AST_FUNCTION_CALL_STATEMENT((AST_FUNCTION_CALL_STATEMENT) new AST(ast_variable_declaration.tokens).getAST().get(0));
                    if (res != null)
                        addVariable(ast_variable_declaration.getID_token().getValue(), res);
                    else
                        throw new LanguageException("Невозможно обработать функцию");
                }

            }
            else
                addVariable(ast_variable_declaration.getID_token().getValue(), null);
        else
            throw new LanguageException("Переменная уже существует");
    }




    private void process_AST_ASSIGNMENT_STATEMENT(AST_ASSIGNMENT_STATEMENT ast_assignment_statement) throws LanguageException
    {
        Variable var = checkInVariablesTable(ast_assignment_statement.getID_token().getValue());

        if(var != null)
        {
            try
            {
                var.setValue(new RPN(ast_assignment_statement.tokens, this).getResult());
            }
            catch (Exception ignored)
            {
                Token res = process_AST_FUNCTION_CALL_STATEMENT((AST_FUNCTION_CALL_STATEMENT) new AST(ast_assignment_statement.tokens).getAST().get(0));

                if (res != null)
                    var.setValue(res);
                else
                    throw new LanguageException("Невозможно обработать функцию");
            }
        }
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
    private void addVariable(String ID, Object value)
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
    private Object value;

    Variable(String ID, Object value)
    {
        this.ID = ID;
        this.value = value;
    }

    public Object getValue() { return value; }

    public String getID() { return ID; }
    public void setValue(Token value) { this.value = value; }
}