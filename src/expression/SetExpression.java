package expression;

public class SetExpression extends Expression {

    private final String identifier;
    private final Expression newValue;

    public SetExpression(String identifier, Expression newValue) {
        super(Type.SET);
        this.identifier = identifier;
        this.newValue = newValue;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Expression getNewValue() {
        return newValue;
    }
}
