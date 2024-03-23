package parser;

import expression.*;
import type.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static List<Expression> run(List<Token> tokens) {
        List<Expression> expressions = new ArrayList<>();

        while (!tokens.isEmpty()) {
            try {
                ExpressionResult result = parseExpression(tokens);
                expressions.add(result.expression());
                tokens = sublist(tokens, result.tokensConsumed());
            } catch (ParseException | IndexOutOfBoundsException e) {
                e.printStackTrace();
                return null;
            }
        }

        return expressions;
    }

    private static ExpressionResult parseExpression(List<Token> tokens) throws ParseException {
        if (tokens.isEmpty()) return null; // this should never happen

        Token first = tokens.get(0);

        // case: number, boolean, or identifier
        if (first.getType() == Token.Type.NUMBER) {
            return new ExpressionResult(new LiteralExpression(Expression.Type.NUMBER), 1);
        } else if (first.getType() == Token.Type.BOOL) {
            return new ExpressionResult(new LiteralExpression(Expression.Type.BOOLEAN), 1);
        } else if (first.getType() == Token.Type.IDENTIFIER) {
            return new ExpressionResult(new LiteralExpression(first.getValue()), 1);
        }

        matchToken(Token.Type.PAREN_OPEN, first);

        Token.Type command = tokens.get(1).getType();
        tokens = sublist(tokens, 2);

        ExpressionResult result = switch(command) {
            case PLUS, MINUS, MULTIPLY, DIVIDE -> parseBinaryExpression(tokens, Expression.Type.ARITHMETIC);
            case EQUALS, LESS_THAN, GREATER_THAN -> parseBinaryExpression(tokens, Expression.Type.RELATIONAL);
            case AND, OR -> parseBinaryExpression(tokens, Expression.Type.LOGICAL);
            case NOT -> parseUnaryExpression(tokens);
            case IF -> parseConditionalExpression(tokens);
            case APP -> parseBinaryExpression(tokens, Expression.Type.APP);
            case FUN -> parseFunctionExpression(tokens);
            case BEGIN -> parseBinaryExpression(tokens, Expression.Type.BEGIN);
            case SET -> parseSetExpression(tokens);
            case OBJECT -> parseObjectExpression(tokens);
            case FIELD -> parseFieldExpression(tokens);
            case LET -> parseLetExpression(tokens);
            default -> null;
        };

        tokens = sublist(tokens, result.tokensConsumed());
        matchToken(Token.Type.PAREN_CLOSE, tokens.get(0));
        return new ExpressionResult(result.expression(), 2 + result.tokensConsumed() + 1);
    }

    private static ExpressionResult parseBinaryExpression(List<Token> tokens, Expression.Type type) throws ParseException {
        // parse left
        ExpressionResult leftResult = parseExpression(tokens);
        tokens = sublist(tokens, leftResult.tokensConsumed());

        // parse right
        ExpressionResult rightResult = parseExpression(tokens);

        BinaryExpression expression = new BinaryExpression(type, leftResult.expression(), rightResult.expression());
        return new ExpressionResult(expression, leftResult.tokensConsumed() + rightResult.tokensConsumed());
    }

    private static ExpressionResult parseUnaryExpression(List<Token> tokens) throws ParseException {
        // parse operand
        ExpressionResult operand = parseExpression(tokens);

        UnaryExpression expression = new UnaryExpression(operand.expression());
        return new ExpressionResult(expression, operand.tokensConsumed());
    }

    private static ExpressionResult parseConditionalExpression(List<Token> tokens) throws ParseException {
        // parse condition
        ExpressionResult condition = parseExpression(tokens);
        tokens = sublist(tokens, condition.tokensConsumed());

        // parse consequent
        ExpressionResult consequent = parseExpression(tokens);
        tokens = sublist(tokens, consequent.tokensConsumed());

        // parse alternate
        ExpressionResult alternate = parseExpression(tokens);

        ConditionalExpression expression = new ConditionalExpression(condition.expression(), consequent.expression(), alternate.expression());
        return new ExpressionResult(expression, condition.tokensConsumed() + consequent.tokensConsumed() + alternate.tokensConsumed());
    }

    private static ExpressionResult parseFunctionExpression(List<Token> tokens) throws ParseException {
        // parse characters
        matchToken(Token.Type.PAREN_OPEN, tokens.get(0));
        matchToken(Token.Type.IDENTIFIER, tokens.get(1));
        String id = tokens.get(1).getValue();
        matchToken(Token.Type.COLON, tokens.get(2));
        tokens = sublist(tokens, 3);

        // parse type
        TypeResult type = parseType(tokens);
        tokens = sublist(tokens, type.tokensConsumed());

        matchToken(Token.Type.PAREN_CLOSE, tokens.get(0));
        tokens = sublist(tokens, 1);

        // parse body
        ExpressionResult body = parseExpression(tokens);

        int totalConsumed = 3 + type.tokensConsumed() + 1 + body.tokensConsumed();
        FunctionExpression expression = new FunctionExpression(id, type.primitive(), body.expression());
        return new ExpressionResult(expression, totalConsumed);
    }

    private static ExpressionResult parseSetExpression(List<Token> tokens) throws ParseException {
        // parse identifier
        matchToken(Token.Type.IDENTIFIER, tokens.get(0));
        String id = tokens.get(0).getValue();
        tokens = sublist(tokens, 1);

        // parse expression
        ExpressionResult expression = parseExpression(tokens);

        SetExpression setExpression = new SetExpression(id, expression.expression());
        return new ExpressionResult(setExpression, 1 + expression.tokensConsumed());
    }

    private static ExpressionResult parseObjectExpression(List<Token> tokens) throws ParseException {
        // parse list of values
        ParseValueListResult result = parseValueList(tokens);

        return new ExpressionResult(new ObjectExpression(result.values()), result.tokensConsumed());
    }

    private static ExpressionResult parseFieldExpression(List<Token> tokens) throws ParseException {
        // parse object expression
        ExpressionResult expression = parseExpression(tokens);
        tokens = sublist(tokens, expression.tokensConsumed());

        // parse identifier
        matchToken(Token.Type.IDENTIFIER, tokens.get(0));
        String id = tokens.get(0).getValue();

        FieldExpression fieldExpression = new FieldExpression(expression.expression(), id);
        return new ExpressionResult(fieldExpression, expression.tokensConsumed() + 1);
    }

    private static ExpressionResult parseLetExpression(List<Token> tokens) throws ParseException {
        matchToken(Token.Type.PAREN_OPEN, tokens.get(0));
        tokens = sublist(tokens, 1);

        // parse list of values
        ParseValueListResult result = parseValueList(tokens);
        tokens = sublist(tokens, result.tokensConsumed());

        matchToken(Token.Type.PAREN_CLOSE, tokens.get(0));
        tokens = sublist(tokens, 1);

        // parse body expression
        ExpressionResult expressionResult = parseExpression(tokens);

        LetExpression letExpression = new LetExpression(result.values(), expressionResult.expression());
        int totalConsumed = 1 + result.tokensConsumed() + 1 + expressionResult.tokensConsumed();
        return new ExpressionResult(letExpression, totalConsumed);
    }

    private static void matchToken(Token.Type expected, Token token) throws ParseException {
        if (token.getType() != expected) throw new ParseException("Type mismatch: expected " + expected + " but got " + token.getType());
    }

    private static TypeResult parseType(List<Token> tokens) throws ParseException {
        // similar to parse expression
        if (tokens.isEmpty()) return null;

        Token first = tokens.get(0);

        if (first.getType() == Token.Type.TYPE_NUM || first.getType() == Token.Type.TYPE_BOOL || first.getType() == Token.Type.TYPE_VOID) {
            return new TypeResult(new TruePrimitive(convertTokenToPrimitive(first.getType())), 1);
        }

        matchToken(Token.Type.PAREN_OPEN, first);

        tokens = sublist(tokens, 1);
        TypeResult result = switch (tokens.get(0).getType()) {
            case ARROW -> parseArrowType(sublist(tokens, 1));
            case OBJECT -> parseObjectType(sublist(tokens, 1));
            default -> null;
        };

        tokens = sublist(tokens, result.tokensConsumed());
        matchToken(Token.Type.PAREN_CLOSE, tokens.get(0));
        return new TypeResult(result.primitive(), 2 + result.tokensConsumed() + 1);
    }

    private static TypeResult parseArrowType(List<Token> tokens) throws ParseException {
        // parse argument
        TypeResult argument = parseType(tokens);
        tokens = sublist(tokens, argument.tokensConsumed());

        // parse result
        TypeResult result = parseType(tokens);

        ArrowType type = new ArrowType(argument.primitive(), result.primitive());
        return new TypeResult(type, argument.tokensConsumed() + result.tokensConsumed());
    }

    private static TypeResult parseObjectType(List<Token> tokens) throws ParseException {
        List<Attribute> attributes = new ArrayList<>();
        int totalConsumed = 0;
        while (tokens.get(0).getType() != Token.Type.PAREN_CLOSE) {
            matchToken(Token.Type.BRACKET_OPEN, tokens.get(0));
            matchToken(Token.Type.IDENTIFIER, tokens.get(1));
            String id = tokens.get(1).getValue();
            tokens = sublist(tokens, 2);

            TypeResult typeResult = parseType(tokens);
            int tempConsumed = 2 + typeResult.tokensConsumed();
            tokens = sublist(tokens, tempConsumed);

            matchToken(Token.Type.BRACKET_CLOSE, tokens.get(0));
            tempConsumed++;
            tokens = sublist(tokens, 1);
            totalConsumed += tempConsumed;
            attributes.add(new Attribute(id, typeResult.primitive()));
        }

        return new TypeResult(new ObjectType(attributes), totalConsumed);
    }

    private static ParseValueListResult parseValueList(List<Token> tokens) throws ParseException {
        List<Value> variables = new ArrayList<>();
        int totalConsumed = 0;
        while (tokens.get(0).getType() != Token.Type.PAREN_CLOSE) {
            matchToken(Token.Type.BRACKET_OPEN, tokens.get(0));
            matchToken(Token.Type.IDENTIFIER, tokens.get(1));
            String id = tokens.get(1).getValue();
            tokens = sublist(tokens, 2);

            ExpressionResult expressionResult = parseExpression(tokens);
            int tempConsumed = 2 + expressionResult.tokensConsumed();
            tokens = sublist(tokens, expressionResult.tokensConsumed());

            matchToken(Token.Type.BRACKET_CLOSE, tokens.get(0));
            tempConsumed++;
            tokens = sublist(tokens, 1);
            totalConsumed += tempConsumed;
            variables.add(new Value(id, expressionResult.expression()));
        }

        return new ParseValueListResult(variables, totalConsumed);
    }

    private static List<Token> sublist(List<Token> tokens, int index) throws ParseException {
        if (index > tokens.size()) throw new ParseException("Ran out of tokens");
        return tokens.subList(index, tokens.size());
    }

    private static Primitive.Type convertTokenToPrimitive(Token.Type type) {
        return switch (type) {
            case TYPE_NUM -> Primitive.Type.NUMBER;
            case TYPE_BOOL -> Primitive.Type.BOOLEAN;
            case TYPE_VOID -> Primitive.Type.VOID;
            default -> null;
        };
    }
}
