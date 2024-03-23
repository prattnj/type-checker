package expression;

public class BinaryExpression extends Expression {

    private final Expression left;
    private final Expression right;

    public BinaryExpression(Type type, Expression left, Expression right) {
        super(type);
        this.left = left;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }
}
