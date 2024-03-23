package type;

public abstract class Primitive {

    public enum Type {
        NUMBER,
        BOOLEAN,
        VOID,
        ARROW,
        OBJECT,
    }

    protected final Type type;

    public Primitive(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Primitive primitive = (Primitive) o;
        if (type != primitive.type) return false;
        return equals(primitive);
    }

    protected abstract boolean equals(Primitive p);
}