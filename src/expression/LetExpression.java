package expression;

import java.util.List;

public class LetExpression extends Expression {

    private final List<Value> variables;
    private final Expression body;

    public LetExpression(List<Value> variables, Expression body) {
        super(Type.LET);
        this.variables = variables;
        this.body = body;
    }

    public List<Value> getVariables() {
        return variables;
    }

    public Expression getBody() {
        return body;
    }
}
