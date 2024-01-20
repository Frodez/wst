package info.wst.jna.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({ "Sid", "Attributes" })
public class SidAndAttribute extends Structure {

    public SidAndAttribute() {
        super();
    }

    public SidAndAttribute(Pointer pointer) {
        super(pointer);
        read();
    }

    public Pointer Sid;

    public int Attributes;

    public static class ByReference extends SidAndAttribute implements Structure.ByReference {

        public ByReference() {
            super();
        }

        public ByReference(Pointer pointer) {
            super(pointer);
        }

    }

    public static class ByValue extends SidAndAttribute implements Structure.ByReference {

        public ByValue() {
            super();
        }

        public ByValue(Pointer pointer) {
            super(pointer);
        }
        
    }

}
