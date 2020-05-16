package com.company;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
    {
        try
        {

            ArrayList<Token> tokens = new Lexer(readFile()).getTokens();

            new Parser(tokens);

            new Interpreter(new AST(tokens).getAST(), null);
        }
        catch (LanguageException e)
        {
            if(e.getMessage() != null)
                System.err.println(e.getMessage());
        }
    }


    private static String readFile()
    {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader fr = new FileReader("C:\\Users\\Asus\\Desktop\\уник\\java\\inter2.0\\src\\com\\company\\CODE");
            Scanner scan = new Scanner(fr);

            while (scan.hasNextLine())
                stringBuilder.append(scan.nextLine()).append("\n");

            fr.close();
        } catch (Exception ignored) { }

        return stringBuilder.toString();
    }


}