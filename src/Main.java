import expression.Expression;
import interpreter.TypeChecker;
import parser.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        boolean devMode = false;

        Scanner scanner = devMode ? new Scanner(new File("source.txt")) : new Scanner(System.in);
        ArrayList<String> input = new ArrayList<>();
        while (scanner.hasNext()) {
            input.add(scanner.nextLine());
        }

        List<Token> tokens = Tokenizer.run(input);
        if (tokens == null) {
            System.out.println("Invalid tokens.");
            return;
        }

        List<Expression> expressions = Parser.run(tokens);
        if (expressions == null) {
            System.out.println("Invalid syntax.");
            return;
        }

        String result = new TypeChecker().run(expressions);

        System.out.println(result);
    }
}
