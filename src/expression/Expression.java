package expression;

import parser.Token;

public abstract class Expression {

    public enum Type {
        NUMBER,
        BOOLEAN,
        IDENTIFIER,
        ARITHMETIC,
        RELATIONAL,
        LOGICAL,
        UNARY,
        CONDITIONAL,
        LET,
        APP,
        FUNCTION,
        BEGIN,
        SET,
        OBJECT,
        FIELD,
    }

    protected final Type type;
    protected final String value;

    public Expression(Type type) {
        this.type = type;
        this.value = null;
    }

    public Expression(String value) {
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
