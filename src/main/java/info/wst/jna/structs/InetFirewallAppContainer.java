package info.wst.jna.structs;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;

@Structure.FieldOrder({"appContainerSid", "userSid", "appContainerName", "displayName", 
"description", "capabilities", "binaries", "workingDirectory", "packageFullName"})
public class InetFirewallAppContainer extends Structure {

    public InetFirewallAppContainer() {
        super();
    }

    public InetFirewallAppContainer(Pointer pointer){
        super(pointer);
        read();
    }

    public Pointer appContainerSid;

    public Pointer userSid;

    public WString appContainerName;

    public WString displayName;

    public WString description;

    public InetFirewallAcCapability capabilities;

    public InetFirewallAcBinary binaries;

    public WString workingDirectory;

    public WString packageFullName;

    public static class ByReference extends InetFirewallAppContainer implements Structure.ByReference {

        public ByReference() {
            super();
        }

        public ByReference(Pointer pointer) {
            super(pointer);
        }

    }

    public static class ByValue extends InetFirewallAppContainer implements Structure.ByReference {

        public ByValue() {
            super();
        }

        public ByValue(Pointer pointer) {
            super(pointer);
        }
        
    }

}
