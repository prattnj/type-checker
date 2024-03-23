package type;

import java.util.List;

public class ObjectType extends Primitive {

    private final List<Attribute> attributes;

    public ObjectType(List<Attribute> attributes) {
        super(Type.OBJECT);
        this.attributes = attributes;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    protected boolean equals(Primitive p) {
        ObjectType that = (ObjectType) p;
        if (this.attributes.size() != that.attributes.size()) return false;
        for (int i = 0; i < this.attributes.size(); i++) {
            Attribute thisAttr = this.attributes.get(i);
            Attribute thatAttr = that.attributes.get(i);
            if (!thisAttr.type().equals(thatAttr.type()) || !thisAttr.identifier().equals(thatAttr.identifier())) {
                return false;
            }
        }
        return true;
    }
}
