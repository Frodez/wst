package info.wst.jna;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.ptr.PointerByReference;

import info.wst.jna.libraries.Firewallapi;
import info.wst.jna.structs.InetFirewallAppContainer;
import info.wst.jna.structs.SidAndAttribute;
import info.wst.models.AppContainer;
import info.wst.utils.CommonException;
import info.wst.utils.ErrorUtil;
import info.wst.utils.LogUtil;

public class API {

    static {
        if (!com.sun.jna.Platform.isWindows()) {
            ErrorUtil.reportError("This app only supports windows");
            System.exit(0);
        } else if (Firewallapi.INSTANCE == null) {
            ErrorUtil.reportError("Dll load failed");
            System.exit(0);
        }
    }

    public static void assertInPrivilegedProcess() {
        if (!Advapi32Util.isCurrentProcessElevated()) {
            String message = LogUtil.formatLogMessage("error.admin_required");
            throw new CommonException(message);
        }
    }

    public static List<AppContainer> getAppContainers() {
        Map<String, SidAndAttribute> notIsolatedAppContainerConfig = getNotIsolatedAppContainerConfig();

        List<AppContainer> appContainerList = getInetFirewallAppContainers();

        for (AppContainer appContainer : appContainerList) {
            boolean isolated = !notIsolatedAppContainerConfig.containsKey(appContainer.getSid());
            appContainer.setIsolated(isolated);
        }
        return appContainerList;
    }

    public static void disableIsolation(List<String> appContainerSidList) {
        assertInPrivilegedProcess();
        Map<String, SidAndAttribute> notIsolatedAppContainerConfig = getNotIsolatedAppContainerConfig();

        for (String appContainerSid : appContainerSidList) {
            if (!notIsolatedAppContainerConfig.containsKey(appContainerSid)) {
                SidAndAttribute sidAndAttributes = new SidAndAttribute();
                sidAndAttributes.Attributes = 0;
                sidAndAttributes.Sid = new PSID(Advapi32Util.convertStringSidToSid(appContainerSid)).getPointer();

                notIsolatedAppContainerConfig.put(appContainerSid, sidAndAttributes);
            }
        }

        int dwNumPublicAppCs = notIsolatedAppContainerConfig.size();
        SidAndAttribute[] pAppContainerSids = null;
        if (dwNumPublicAppCs > 0) {
            pAppContainerSids = (SidAndAttribute[]) new SidAndAttribute().toArray(dwNumPublicAppCs);
            int i = 0;
            for (SidAndAttribute sidAndAttribute : notIsolatedAppContainerConfig.values()) {
                pAppContainerSids[i].Attributes = sidAndAttribute.Attributes;
                pAppContainerSids[i].Sid = sidAndAttribute.Sid;
                i++;
            }
        }

        int result = Firewallapi.INSTANCE.NetworkIsolationSetAppContainerConfig(
                dwNumPublicAppCs, pAppContainerSids);
        if (WinError.ERROR_SUCCESS != result) {
            throw new CommonException(formatMessage(result));
        }

    }

    public static void enableIsolation(List<String> appContainerSidList) {
        assertInPrivilegedProcess();
        Map<String, SidAndAttribute> notIsolatedAppContainerConfig = getNotIsolatedAppContainerConfig();

        for (String appContainerSid : appContainerSidList) {
            notIsolatedAppContainerConfig.remove(appContainerSid);
        }

        int dwNumPublicAppCs = notIsolatedAppContainerConfig.size();
        SidAndAttribute[] pAppContainerSids = null;
        if (dwNumPublicAppCs > 0) {
            pAppContainerSids = (SidAndAttribute[]) new SidAndAttribute().toArray(dwNumPublicAppCs);
            int i = 0;
            for (SidAndAttribute sidAndAttribute : notIsolatedAppContainerConfig.values()) {
                pAppContainerSids[i].Attributes = sidAndAttribute.Attributes;
                pAppContainerSids[i].Sid = sidAndAttribute.Sid;
                i++;
            }
        }

        int result = Firewallapi.INSTANCE.NetworkIsolationSetAppContainerConfig(
                dwNumPublicAppCs, pAppContainerSids);
        if (WinError.ERROR_SUCCESS != result) {
            throw new CommonException(formatMessage(result));
        }
    }

    private static List<AppContainer> getInetFirewallAppContainers() {
        List<AppContainer> appContainerList = new ArrayList<>();

        WinDef.DWORDByReference pdwNumPublicAppCs = new WinDef.DWORDByReference();
        PointerByReference ppPublicAppCs = new PointerByReference();

        int result = Firewallapi.INSTANCE.NetworkIsolationEnumAppContainers(
                Firewallapi.NETISO_FLAG_MAX, pdwNumPublicAppCs, ppPublicAppCs);
        if (WinError.ERROR_SUCCESS != result) {
            throw new CommonException(formatMessage(result));
        }
        try {
            int num = pdwNumPublicAppCs.getValue().intValue();
            if (num > 0) {
                InetFirewallAppContainer[] inetFirewallAppContainers = (InetFirewallAppContainer[]) new InetFirewallAppContainer(
                        ppPublicAppCs.getValue()).toArray(num);
                for (InetFirewallAppContainer inetFirewallAppContainer : inetFirewallAppContainers) {
                    if (inetFirewallAppContainer.appContainerName == null ||
                            inetFirewallAppContainer.displayName == null ||
                            inetFirewallAppContainer.workingDirectory == null ||
                            inetFirewallAppContainer.appContainerSid == null) {
                        continue;
                    }
                    AppContainer appContainer = new AppContainer();
                    appContainer.setContainerName(inetFirewallAppContainer.appContainerName.toString());
                    appContainer.setDisplayName(inetFirewallAppContainer.displayName.toString());
                    appContainer.setWorkingDirectory(inetFirewallAppContainer.workingDirectory.toString());
                    appContainer.setSid(Advapi32Util
                            .convertSidToStringSid(new PSID(inetFirewallAppContainer.appContainerSid)));

                    appContainerList.add(appContainer);
                }
            }
        } finally {
            Firewallapi.INSTANCE.NetworkIsolationFreeAppContainers(ppPublicAppCs.getValue());
        }
        return appContainerList;
    }

    private static Map<String, SidAndAttribute> getNotIsolatedAppContainerConfig() {
        Map<String, SidAndAttribute> notIsolatedAppContainerConfig = new HashMap<>();
        WinDef.DWORDByReference pdwNumPublicAppCs = new WinDef.DWORDByReference();
        PointerByReference ppAppContainerSids = new PointerByReference();

        int result = Firewallapi.INSTANCE.NetworkIsolationGetAppContainerConfig(pdwNumPublicAppCs, ppAppContainerSids);
        if (WinError.ERROR_SUCCESS != result) {
            throw new CommonException(formatMessage(result));
        }
        int num = pdwNumPublicAppCs.getValue().intValue();
        if (num > 0) {
            Pointer pAppContainerSids = ppAppContainerSids.getPointer().getPointer(0);
            for (int i = 0; i < num; i++) {
                SidAndAttribute sidAndAttribute = new SidAndAttribute();
                sidAndAttribute.Sid = pAppContainerSids.getPointer(i * 16);
                sidAndAttribute.Attributes = pAppContainerSids.getInt(i * 16 + 8);
                String sid = Advapi32Util.convertSidToStringSid(new PSID(sidAndAttribute.Sid));
                notIsolatedAppContainerConfig.put(sid, sidAndAttribute);
            }
        }

        return notIsolatedAppContainerConfig;
    }

    private static String formatMessage(int code) {
        return Kernel32Util.formatMessage(W32Errors.HRESULT_FROM_WIN32(code));
    }

}
