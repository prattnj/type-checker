package expression;

import type.Primitive;

public class FunctionExpression extends Expression {

    private final String argName;
    private final Primitive argType;
    private final Expression body;

    public FunctionExpression(String argName, Primitive argType, Expression body) {
        super(Type.FUNCTION);
        this.argName = argName;
        this.argType = argType;
        this.body = body;
    }

    public String getArgName() {
        return argName;
    }

    public Primitive getArgType() {
        return argType;
    }

    public Expression getBody() {
        return body;
    }
}
