package info.wst.jna.structs;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WTypes.LPWSTR;

@Structure.FieldOrder({ "count", "binaries" })
public class InetFirewallAcBinary extends Structure {

    public InetFirewallAcBinary() {
        super();
    }

    public InetFirewallAcBinary(Pointer pointer) {
        super(pointer);
        read();
    }

    public int count;

    public Pointer binaries;

    public List<String> binariesToStringList() {
        Pointer pointer = binaries;
        List<String> list = new ArrayList<>(count);
        int structSize = Native.getNativeSize(LPWSTR.class);
        for (int i = 0; i < count; i++) {
            LPWSTR lpwstr = new LPWSTR(pointer.getPointer(i * structSize));
            String str = lpwstr.toString();
            list.add(str);
        }
        return list;
    }

    public static class ByReference extends InetFirewallAcBinary implements Structure.ByReference {

        public ByReference() {
            super();
        }

        public ByReference(Pointer pointer) {
            super(pointer);
        }

    }

    public static class ByValue extends InetFirewallAcBinary implements Structure.ByReference {

        public ByValue() {
            super();
        }

        public ByValue(Pointer pointer) {
            super(pointer);
        }

    }

}
