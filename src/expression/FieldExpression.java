package expression;

public class FieldExpression extends Expression {

    private final Expression object;
    private final String attribute;

    public FieldExpression(Expression object, String attribute) {
        super(Type.FIELD);
        this.object = object;
        this.attribute = attribute;
    }

    public Expression getObject() {
        return object;
    }

    public String getAttribute() {
        return attribute;
    }
}
