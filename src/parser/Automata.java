package parser;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

public class Automata {

    public static SortedMap<Token.Type, Function<String, Integer>> getAutomata() {
        SortedMap<Token.Type, Function<String, Integer>> automata = new TreeMap<>();

        automata.put(Token.Type.IDENTIFIER, Automata::identifier);
        automata.put(Token.Type.TYPE_NUM, Automata::typeNum);
        automata.put(Token.Type.TYPE_BOOL, Automata::typeBool);
        automata.put(Token.Type.TYPE_VOID, Automata::typeVoid);
        automata.put(Token.Type.PAREN_OPEN, Automata::parenOpen);
        automata.put(Token.Type.PAREN_CLOSE, Automata::parenClosed);
        automata.put(Token.Type.BRACKET_OPEN, Automata::bracketOpen);
        automata.put(Token.Type.BRACKET_CLOSE, Automata::bracketClosed);
        automata.put(Token.Type.COLON, Automata::colon);
        automata.put(Token.Type.NUMBER, Automata::number);
        automata.put(Token.Type.BOOL, Automata::bool);
        automata.put(Token.Type.PLUS, Automata::plus);
        automata.put(Token.Type.MINUS, Automata::minus);
        automata.put(Token.Type.MULTIPLY, Automata::multiply);
        automata.put(Token.Type.DIVIDE, Automata::divide);
        automata.put(Token.Type.EQUALS, Automata::equals);
        automata.put(Token.Type.LESS_THAN, Automata::lessThan);
        automata.put(Token.Type.GREATER_THAN, Automata::greaterThan);
        automata.put(Token.Type.AND, Automata::and);
        automata.put(Token.Type.OR, Automata::or);
        automata.put(Token.Type.NOT, Automata::not);
        automata.put(Token.Type.IF, Automata::ifX);
        automata.put(Token.Type.LET, Automata::let);
        automata.put(Token.Type.APP, Automata::app);
        automata.put(Token.Type.FUN, Automata::fun);
        automata.put(Token.Type.ARROW, Automata::arrow);
        automata.put(Token.Type.BEGIN, Automata::begin);
        automata.put(Token.Type.SET, Automata::set);
        automata.put(Token.Type.OBJECT, Automata::object);
        automata.put(Token.Type.FIELD, Automata::field);

        return automata;
    }

    private static int identifier(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isAlphabetic(s.charAt(i))) return i;
        }
        return s.length();
    }

    private static int typeNum(String s) {
        return parseLiteral(s, "num");
    }

    private static int typeBool(String s) {
        return parseLiteral(s, "bool");
    }

    private static int typeVoid(String s) {
        return parseLiteral(s, "void");
    }

    private static int parenOpen(String s) {
        return parseLiteral(s, "(");
    }

    private static int parenClosed(String s) {
        return parseLiteral(s, ")");
    }

    private static int bracketOpen(String s) {
        return parseLiteral(s, "[");
    }

    private static int bracketClosed(String s) {
        return parseLiteral(s, "]");
    }

    private static int colon(String s) {
        return parseLiteral(s, ":");
    }

    private static int number(String s) {
        for (int i = 0; i < s.length(); i++) {
            String sub = s.substring(0, i + 1);
            try {
                Integer.parseInt(sub);
            } catch (NumberFormatException e) {
                return i;
            }
        }
        return s.length();
    }

    private static int bool(String s) {
        int t = parseLiteral(s, "#true");
        int f = parseLiteral(s, "#false");
        return Math.max(t, f);
    }

    private static int plus(String s) {
        return parseLiteral(s, "+");
    }

    private static int minus(String s) {
        return parseLiteral(s, "-");
    }

    private static int multiply(String s) {
        return parseLiteral(s, "*");
    }

    private static int divide(String s) {
        return parseLiteral(s, "/");
    }

    private static int equals(String s) {
        return parseLiteral(s, "=");
    }

    private static int lessThan(String s) {
        return parseLiteral(s, "<");
    }

    private static int greaterThan(String s) {
        return parseLiteral(s, ">");
    }

    private static int and(String s) {
        return parseLiteral(s, "and");
    }

    private static int or(String s) {
        return parseLiteral(s, "or");
    }

    private static int not(String s) {
        return parseLiteral(s, "not");
    }

    private static int ifX(String s) {
        return parseLiteral(s, "if");
    }

    private static int let(String s) {
        return parseLiteral(s, "let");
    }

    private static int app(String s) {
        return parseLiteral(s, "app");
    }

    private static int fun(String s) {
        return parseLiteral(s, "fun");
    }

    private static int arrow(String s) {
        return parseLiteral(s, "->");
    }

    private static int begin(String s) {
        return parseLiteral(s, "begin");
    }

    private static int set(String s) {
        return parseLiteral(s, "set!");
    }

    private static int object(String s) {
        return parseLiteral(s, "object");
    }

    private static int field(String s) {
        return parseLiteral(s, "field");
    }

    private static int parseLiteral(String input, String literal) {
        return input.startsWith(literal) ? literal.length() : 0;
    }
}
