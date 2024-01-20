package info.wst.jna.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({ "count", "capabilities" })
public class InetFirewallAcCapability extends Structure {

    public InetFirewallAcCapability() {
        super();
    }

    public InetFirewallAcCapability(Pointer pointer) {
        super(pointer);
        read();
    }

    public int count;

    public SidAndAttribute.ByReference capabilities;

    public static class ByReference extends InetFirewallAcCapability implements Structure.ByReference {

        public ByReference() {
            super();
        }

        public ByReference(Pointer pointer) {
            super(pointer);
        }

    }

    public static class ByValue extends InetFirewallAcCapability implements Structure.ByReference {

        public ByValue() {
            super();
        }

        public ByValue(Pointer pointer) {
            super(pointer);
        }
        
    }

}
