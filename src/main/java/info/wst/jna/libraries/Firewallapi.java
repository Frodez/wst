package info.wst.jna.libraries;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.ptr.PointerByReference;

import info.wst.jna.structs.SidAndAttribute;

public interface Firewallapi extends Library {

    int NETISO_FLAG_FORCE_COMPUTE_BINARIES = 1;
    int NETISO_FLAG_MAX = 2;

    Firewallapi INSTANCE = Native.load("Firewallapi", Firewallapi.class);

    int NetworkIsolationEnumAppContainers(int Flags, DWORDByReference pdwNumPublicAppCs, PointerByReference ppPublicAppCs);
    void NetworkIsolationFreeAppContainers(Pointer pPublicAppCs);

    int NetworkIsolationGetAppContainerConfig(DWORDByReference pdwNumPublicAppCs, PointerByReference ppAppContainerSids);

    int NetworkIsolationSetAppContainerConfig(int dwNumPublicAppCs, SidAndAttribute[] pAppContainerSids);
}
