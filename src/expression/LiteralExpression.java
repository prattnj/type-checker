package expression;

public class LiteralExpression extends Expression {
    public LiteralExpression(Type type) {
        super(type);
    }

    public LiteralExpression(String value) {
        super(value);
    }
}
