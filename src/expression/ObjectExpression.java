package expression;

import java.util.List;

public class ObjectExpression extends Expression {

    private final List<Value> attributes;

    public ObjectExpression(List<Value> attributes) {
        super(Type.OBJECT);
        this.attributes = attributes;
    }

    public List<Value> getAttributes() {
        return attributes;
    }
}
