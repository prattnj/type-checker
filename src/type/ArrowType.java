package type;

public class ArrowType extends Primitive {

    private final Primitive argument;
    private final Primitive result;

    public ArrowType(Primitive argument, Primitive result) {
        super(Type.ARROW);
        this.argument = argument;
        this.result = result;
    }

    public Primitive getArgument() {
        return argument;
    }

    public Primitive getResult() {
        return result;
    }

    @Override
    protected boolean equals(Primitive p) {
        ArrowType that = (ArrowType) p;
        return this.argument.equals(that.argument) && this.result.equals(that.result);
    }
}
