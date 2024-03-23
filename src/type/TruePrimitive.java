package type;

public class TruePrimitive extends Primitive {
    public TruePrimitive(Type type) {
        super(type);
    }

    @Override
    protected boolean equals(Primitive p) {
        return this.type == p.type;
    }
}
