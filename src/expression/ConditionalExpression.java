package expression;

public class ConditionalExpression extends Expression {

    private final Expression condition;
    private final Expression consequent;
    private final Expression alternate;

    public ConditionalExpression(Expression condition, Expression consequent, Expression alternate) {
        super(Type.CONDITIONAL);
        this.condition = condition;
        this.consequent = consequent;
        this.alternate = alternate;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getConsequent() {
        return consequent;
    }

    public Expression getAlternate() {
        return alternate;
    }
}
