package com.company;

import java.util.ArrayList;
import java.util.Stack;

public class RPN
{

  

    private final Token result;

    private final Interpreter interpreter;




    public RPN(ArrayList<Token> tokens, Interpreter interpreter) throws LanguageException
    {

        this.interpreter = interpreter;
   


        this.result = calculateRPN(createRPN(tokens));
    }

    /**
     * Создание Обратной польской нотации
     */
    public ArrayList<Token> createRPN(ArrayList<Token> tokens) throws LanguageException
    {
        ArrayList<Token> OutputList = new ArrayList<>();
        Stack<Token> stack = new Stack<>();


        for(Token token : tokens)
        {

            if(token.getType().equals(TokenType.VARIABLE_DECLARATION)
                    || token.getType().equals(TokenType.VARIABLE )
                    || token.getType().equals(TokenType.ASSIGN_OP)
                    || token.getType().equals(TokenType.DIGIT))
            {

                OutputList.add(token);

            }
            else if(token.getType().equals(TokenType.OP)
                    || token.getType().equals(TokenType.LeftRoundBracket)
                    || token.getType().equals(TokenType.RightRoundBracket))
            {
                if ((!stack.empty()) && (!token.getType().equals((TokenType.LeftRoundBracket))))
                {
                    if (token.getType() == (TokenType.RightRoundBracket))
                    {
                        Token opInStack = stack.pop();

                        while(!opInStack.getType().equals((TokenType.LeftRoundBracket)))
                        {
                            OutputList.add(opInStack);

                            try
                            {
                                opInStack = stack.pop();
                            }
                            catch (Exception exception)
                            { throw new LanguageException("Непредвиденная ошибка в вычислениях"); }

                        }
                    }
                    else
                    {

                        if (priority(token) <= priority(stack.peek()))
                            while (!stack.empty() && priority(token) <= priority(stack.peek()))
                                OutputList.add(stack.pop());

                        stack.add(token);
                    }
                }
                else
                    stack.add(token);

            }
        }


        if (!stack.empty())
            OutputList.addAll(stack);


        return OutputList;
    }

    /**
     *
     * Вычисление обратной польской нотации
     */
    public Token calculateRPN(ArrayList<Token> RPN)  throws LanguageException
    {
        Stack<Token> st_calculate = new Stack<>();

        String operation;

        for (Token token : RPN)
        {
            if (token.getType().equals(TokenType.DIGIT))
            {
                st_calculate.push(token);
            }
            else if (token.getType().equals(TokenType.OP))
            {
                operation = token.getValue();

                try
                {
                    if(operation.compareTo("+") == 0)
                    {
                        int operand2 = Integer.parseInt(st_calculate.pop().getValue());
                        int operand1 = Integer.parseInt(st_calculate.pop().getValue());

                        st_calculate.push(new Token(Integer.toString(operand1 + operand2), TokenType.DIGIT));
                    }
                    else if(operation.compareTo("-") == 0)
                    {
                        int operand2 = Integer.parseInt(st_calculate.pop().getValue());
                        int operand1 = Integer.parseInt(st_calculate.pop().getValue());

                        st_calculate.push(new Token(Integer.toString(operand1 - operand2), TokenType.DIGIT));
                    }
                    else if(operation.compareTo("*") == 0)
                    {
                        int operand2 = Integer.parseInt(st_calculate.pop().getValue());
                        int operand1 = Integer.parseInt(st_calculate.pop().getValue());

                        st_calculate.push(new Token(Integer.toString(operand1 * operand2), TokenType.DIGIT));
                    }
                    else if(operation.compareTo("/") == 0)
                    {
                        int operand2 = Integer.parseInt(st_calculate.pop().getValue());
                        int operand1 = Integer.parseInt(st_calculate.pop().getValue());

                        st_calculate.push(new Token(Integer.toString(operand1 / operand2), TokenType.DIGIT));
                    }
                    else if(operation.compareTo(">") == 0)
                    {
                        int operand2 = Integer.parseInt(st_calculate.pop().getValue());
                        int operand1 = Integer.parseInt(st_calculate.pop().getValue());

                        st_calculate.push(new Token(Boolean.toString(operand1 > operand2), TokenType.BOOLEAN));
                    }
                    else if(operation.compareTo("<") == 0)
                    {
                        int operand2 = Integer.parseInt(st_calculate.pop().getValue());
                        int operand1 = Integer.parseInt(st_calculate.pop().getValue());

                        st_calculate.push(new Token(Boolean.toString(operand1 < operand2), TokenType.BOOLEAN));
                    }
                    else if(operation.compareTo("<=") == 0)
                    {
                        int operand2 = Integer.parseInt(st_calculate.pop().getValue());
                        int operand1 = Integer.parseInt(st_calculate.pop().getValue());

                        st_calculate.push(new Token(Boolean.toString(operand1 <= operand2), TokenType.BOOLEAN));
                    }
                    else if(operation.compareTo(">=") == 0)
                    {
                        int operand2 = Integer.parseInt(st_calculate.pop().getValue());
                        int operand1 = Integer.parseInt(st_calculate.pop().getValue());

                        st_calculate.push(new Token(Boolean.toString(operand1 >= operand2), TokenType.BOOLEAN));
                    }
                    else if(operation.compareTo("==") == 0)
                    {
                        Token token2 = st_calculate.pop();
                        Token token1 = st_calculate.pop();

                        if(token1.getType() == token2.getType())
                        {
                            if(token1.getType() == TokenType.DIGIT)
                            {
                                int operand2 = Integer.parseInt(token2.getValue());
                                int operand1 = Integer.parseInt(token1.getValue());

                                if(operand1 == operand2)
                                    st_calculate.push(new Token("true", TokenType.BOOLEAN));
                                else
                                    st_calculate.push(new Token("false", TokenType.BOOLEAN));
                            }
                            else
                            {
                                boolean operand2 = Boolean.parseBoolean(token2.getValue());
                                boolean operand1 = Boolean.parseBoolean(token1.getValue());

                                if(operand1 != operand2)
                                    st_calculate.push(new Token("true", TokenType.BOOLEAN));
                                else
                                    st_calculate.push(new Token("false", TokenType.BOOLEAN));
                            }
                        }
                        else
                            throw new Exception("Ошибка в вычислениях, несовместимость типов");

                    }
                    else if(operation.compareTo("!=") == 0)
                    {

                        Token token2 = st_calculate.pop();
                        Token token1 = st_calculate.pop();

                        if(token1.getType() == token2.getType())
                        {
                            if(token1.getType() == TokenType.DIGIT)
                            {
                                int operand2 = Integer.parseInt(token2.getValue());
                                int operand1 = Integer.parseInt(token1.getValue());

                                st_calculate.push(new Token(Boolean.toString(operand1 != operand2), TokenType.BOOLEAN));
                            }
                            else
                            {
                                boolean operand2 = Boolean.parseBoolean(token2.getValue());
                                boolean operand1 = Boolean.parseBoolean(token1.getValue());

                                st_calculate.push(new Token(Boolean.toString(operand1 != operand2), TokenType.BOOLEAN));
                            }
                        }
                        else
                            throw new Exception("Ошибка в вычислениях, несовместимость типов");

                    }
                }
                catch (Exception ignored) { throw new LanguageException("Непредвиденная ошибка в вычислениях"); }
            }
            else if(token.getType().equals(TokenType.VARIABLE))
            {
                st_calculate.push(findVariable(token));
            }
            else
                throw new LanguageException("Непредвиденный токен");

        }

        return st_calculate.pop();
    }

    /**
     *
     * Поиск переменной в такбоице переменных, попадаем сюда например : var a = b - t + 8
     * Для b и t нужно взять значение, смотрим по таблице переменных из интерпретатора
     */
    private Token findVariable(Token variable) throws LanguageException
    {

        Interpreter currentInterpreter = interpreter;
        while (currentInterpreter != null)
        {
            for(Variable var : currentInterpreter.variablesTable)
                if(var.getID().compareTo(variable.getValue()) == 0)
                    if(var.getValue() != null)
                        if(var.getValue() != null)
                            return var.getValue();
                        else
                            throw new LanguageException("Ошибка в вычислениях, переменная не инициализиварована");

            currentInterpreter = currentInterpreter.parent;
        }

        throw new LanguageException("Нет такой переменной");
    }


    /**
     *
     * Приоритет операций
     */
    public int priority(Token token)
    {
        if(token.getValue().equals("*")) return 3;
        if(token.getValue().equals("/")) return 3;
        if(token.getValue().equals("+")) return 2;
        if(token.getValue().equals("-")) return 2;

        return 0;
    }

    public Token getResult() { return result; }



}
