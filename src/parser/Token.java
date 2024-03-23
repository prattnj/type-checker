package parser;

public class Token {

    public enum Type {
        IDENTIFIER,
        TYPE_NUM,
        TYPE_BOOL,
        TYPE_VOID,
        PAREN_OPEN,
        PAREN_CLOSE,
        BRACKET_OPEN,
        BRACKET_CLOSE,
        COLON,
        NUMBER,
        BOOL,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        EQUALS,
        LESS_THAN,
        GREATER_THAN,
        AND,
        OR,
        NOT,
        IF,
        LET,
        APP,
        FUN,
        ARROW,
        BEGIN,
        SET,
        OBJECT,
        FIELD,
    }

    private final Type type;
    private final String value;

    public Token(Type type) {
        this.type = type;
        this.value = null;
    }

    public Token(String value) {
        this.type = Type.IDENTIFIER;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
