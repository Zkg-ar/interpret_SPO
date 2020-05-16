package com.company;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




    public class Lexer
    {

        private final ArrayList<Token> tokens = new ArrayList<>();


        Lexer(String input)
        {
            ArrayList<String> mas = new ArrayList<>();


            StringTokenizer st = new StringTokenizer(input, " \t\n\r,.");

            while(st.hasMoreTokens())
                mas.add(st.nextToken());


            for(String str : mas)
            {
                TokenType type = check(str);

                if(type == null)
                {
                    System.err.println("Doesn't exist");
                    break;
                }

                tokens.add(new Token(str, type));
            }

            System.out.println();
        }


        private TokenType check(String string)
        {
            Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
            Pattern pattern1 = Pattern.compile("^(0|([1-9][0-9]*))$");
            Pattern pattern2 = Pattern.compile("[+\\-*/]");
            Pattern pattern3 = Pattern.compile("^=$");
            Pattern pattern4 = Pattern.compile(">|<|<=|>=|==|!=");
            Pattern pattern5 = Pattern.compile("\\(");
            Pattern pattern6 = Pattern.compile("\\)");
            Pattern pattern7 = Pattern.compile("\\;");


            Matcher matcher = pattern.matcher(string);
            if(matcher.find())
            {
                switch(string)
                {
                    case "var":
                        return TokenType.VARIABLE_DECLARATION;
                    case "IF":
                        return TokenType.IF;
                    case "ELSE":
                        return TokenType.ELSE;
                    case "WHILE":
                        return TokenType.WHILE;
                    case "DO":
                        return TokenType.DO;
                    case "BEGIN":
                        return TokenType.BEGIN;
                    case "END":
                        return TokenType.END;
                    default:
                        return TokenType.VARIABLE;
                }
            }
            else matcher = pattern1.matcher(string);

            if(matcher.find())
                return TokenType.DIGIT;
            else matcher = pattern2.matcher(string);

            if(matcher.find())
                return TokenType.OP;
            else matcher = pattern3.matcher(string);

            if(matcher.find())
                return TokenType.ASSIGN_OP;
            else matcher = pattern4.matcher(string);

            if(matcher.find())
                return TokenType.OP;
            else matcher = pattern5.matcher(string);

            if(matcher.find())
                return TokenType.LeftRoundBracket;
            else matcher = pattern6.matcher(string);

            if(matcher.find())
                return TokenType.RightRoundBracket;
            else matcher = pattern7.matcher(string);

            if(matcher.find())
                return TokenType.EndOfStr;


            return null;
        }



        public ArrayList<Token> getTokens() { return tokens; }


    }