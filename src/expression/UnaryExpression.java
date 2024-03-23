package expression;

public class UnaryExpression extends Expression {

    private final Expression operand;

    public UnaryExpression(Expression operand) {
        super(Type.UNARY);
        this.operand = operand;
    }

    public Expression getOperand() {
        return operand;
    }
}
