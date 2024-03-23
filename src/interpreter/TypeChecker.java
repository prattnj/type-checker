package interpreter;

import expression.*;
import type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeChecker {

    private final Map<String, Primitive> variables = new HashMap<>();

    public String run(List<Expression> expressions) {
        String result = "";
        for (Expression e : expressions) {
            try {
                result = makeNewType(typeCheck(e));
            } catch (TypeCheckException ex) {
                return makeNewError(ex.getMessage());
            }
        }
        return result;
    }

    private Primitive typeCheck(Expression e) throws TypeCheckException {
        return switch (e.getType()) {
            case NUMBER -> new TruePrimitive(Primitive.Type.NUMBER);
            case BOOLEAN -> new TruePrimitive(Primitive.Type.BOOLEAN);
            case IDENTIFIER -> checkIdentifier(e);
            case ARITHMETIC -> checkArithmeticRelational((BinaryExpression) e, Primitive.Type.NUMBER);
            case RELATIONAL -> checkArithmeticRelational((BinaryExpression) e, Primitive.Type.BOOLEAN);
            case LOGICAL -> checkLogical((BinaryExpression) e);
            case UNARY -> checkUnary((UnaryExpression) e);
            case CONDITIONAL -> checkConditional((ConditionalExpression) e);
            case LET -> checkLet((LetExpression) e);
            case APP -> checkApp((BinaryExpression) e);
            case FUNCTION -> checkFunction((FunctionExpression) e);
            case BEGIN -> checkBegin((BinaryExpression) e);
            case SET -> checkSet((SetExpression) e);
            case OBJECT -> checkObject((ObjectExpression) e);
            case FIELD -> checkField((FieldExpression) e);
        };
    }

    private Primitive checkIdentifier(Expression e) throws TypeCheckException {
        if (variables.containsKey(e.getValue())) {
            return variables.get(e.getValue());
        } else throw new TypeCheckException("unbound identifier");
    }

    private Primitive checkArithmeticRelational(BinaryExpression e, Primitive.Type resultType) throws TypeCheckException {
        Primitive left = typeCheck(e.getLeft());
        Primitive right = typeCheck(e.getRight());

        if (left.getType() != Primitive.Type.NUMBER) {
            throw new TypeCheckException("expected type NUMBER but got " + left.getType());
        } else if (right.getType() != Primitive.Type.NUMBER) {
            throw new TypeCheckException("expected type NUMBER but got " + right.getType());
        }

        return new TruePrimitive(resultType);
    }

    private Primitive checkLogical(BinaryExpression e) throws TypeCheckException {
        Primitive left = typeCheck(e.getLeft());
        Primitive right = typeCheck(e.getRight());

        if (left.getType() != Primitive.Type.BOOLEAN) {
            throw new TypeCheckException("expected type BOOLEAN but got " + left.getType());
        } else if (right.getType() != Primitive.Type.BOOLEAN) {
            throw new TypeCheckException("expected type BOOLEAN but got " + right.getType());
        }

        return left;
    }

    private Primitive checkUnary(UnaryExpression e) throws TypeCheckException {
        Primitive operand = typeCheck(e.getOperand());

        if (operand.getType() != Primitive.Type.BOOLEAN) {
            throw new TypeCheckException("expected type BOOLEAN but got " + operand.getType());
        }

        return operand;
    }

    private Primitive checkConditional(ConditionalExpression e) throws TypeCheckException {
        Primitive condition = typeCheck(e.getCondition());
        Primitive consequent = typeCheck(e.getConsequent());
        Primitive alternate = typeCheck(e.getAlternate());

        if (condition.getType() != Primitive.Type.BOOLEAN) {
            throw new TypeCheckException("first expression in a conditional statement must be a boolean");
        }

        if (!consequent.equals(alternate)) {
            throw new TypeCheckException("type mismatch in conditional branches: " + consequent.getType() + ", " + alternate.getType());
        }

        return consequent;
    }

    private Primitive checkLet(LetExpression e) throws TypeCheckException {
        for (Value v : e.getVariables()) {
            if (variables.containsKey(v.id())) {
                throw new TypeCheckException("variable " + v.id() + " has already been defined");
            }
            variables.put(v.id(), typeCheck(v.value()));
        }

        return typeCheck(e.getBody());
    }

    private Primitive checkApp(BinaryExpression e) throws TypeCheckException {
        Primitive left = typeCheck(e.getLeft());
        Primitive right = typeCheck(e.getRight());

        if (left.getType() != Primitive.Type.ARROW) {
            throw new TypeCheckException("first expression in an application must be a function");
        }

        ArrowType arrow = (ArrowType) left;
        if (!arrow.getArgument().equals(right)) {
            throw new TypeCheckException("type mismatch in function argument and call: " + arrow.getArgument().getType() + ", " + right.getType());
        }

        return arrow.getResult();
    }

    private Primitive checkFunction(FunctionExpression e) throws TypeCheckException {
        variables.put(e.getArgName(), e.getArgType());
        Primitive body = typeCheck(e.getBody());

        return new ArrowType(e.getArgType(), body);
    }

    private Primitive checkBegin(BinaryExpression e) throws TypeCheckException {
        typeCheck(e.getLeft());

        return typeCheck(e.getRight());
    }

    private Primitive checkSet(SetExpression e) throws TypeCheckException {
        if (variables.containsKey(e.getIdentifier())) {
            Primitive newValue = typeCheck(e.getNewValue());
            if (!variables.get(e.getIdentifier()).equals(newValue)) {
                throw new TypeCheckException("type mismatch in assignment: " + variables.get(e.getIdentifier()).getType() + ", " + newValue.getType());
            }
            variables.put(e.getIdentifier(), newValue);
        } else throw new TypeCheckException("unbound identifier");

        return new TruePrimitive(Primitive.Type.VOID);
    }

    private Primitive checkObject(ObjectExpression e) throws TypeCheckException {
        List<Attribute> attributes = new ArrayList<>();
        for (Value v : e.getAttributes()) {
            attributes.add(new Attribute(v.id(), typeCheck(v.value())));
        }

        return new ObjectType(attributes);
    }

    private Primitive checkField(FieldExpression e) throws TypeCheckException {
        Primitive object = typeCheck(e.getObject());

        if (object.getType() != Primitive.Type.OBJECT) {
            throw new TypeCheckException("expected type OBJECT but got " + object.getType());
        }

        ObjectType objectType = (ObjectType) object;
        for (Attribute a : objectType.getAttributes()) {
            if (a.identifier().equals(e.getAttribute())) return a.type();
        }

        throw new TypeCheckException("unknown attribute");
    }

    private String makeNewType(Primitive primitive) {
        return "(type " + stringify(primitive) + ")";
    }

    private String makeNewError(String reason) {
        return "(error \"" + reason + " (banana)\")";
    }

    private String stringify(Primitive primitive) {
        return switch (primitive.getType()) {
            case NUMBER -> "(number)";
            case BOOLEAN -> "(boolean)";
            case VOID -> "(void)";
            case ARROW -> {
                ArrowType arrow = (ArrowType) primitive;
                yield "(-> " + stringify(arrow.getArgument()) + " " + stringify(arrow.getResult()) + ")";
            }
            case OBJECT -> {
                ObjectType object = (ObjectType) primitive;
                StringBuilder sb = new StringBuilder("(object");
                for (Attribute a : object.getAttributes()) {
                    sb.append(" [").append(a.identifier()).append(" ").append(stringify(a.type())).append("]");
                }
                sb.append(")");
                yield sb.toString();
            }
        };
    }
}
