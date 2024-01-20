package info.wst.models;

public class AppContainer {

    private String containerName;

    private String displayName;

    private String workingDirectory;

    private String sid;

    private Boolean isolated;

    public AppContainer() {
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public boolean isIsolated() {
        return isolated;
    }

    public void setIsolated(Boolean isolated) {
        this.isolated = isolated;
    }

    @Override
    public String toString() {
        return "AppContainer [containerName=" + containerName + ", displayName=" + displayName + ", workingDirectory="
                + workingDirectory + ", sid=" + sid + ", isolated=" + isolated + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((containerName == null) ? 0 : containerName.hashCode());
        result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
        result = prime * result + ((workingDirectory == null) ? 0 : workingDirectory.hashCode());
        result = prime * result + ((sid == null) ? 0 : sid.hashCode());
        result = prime * result + (isolated ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AppContainer other = (AppContainer) obj;
        if (containerName == null) {
            if (other.containerName != null)
                return false;
        } else if (!containerName.equals(other.containerName))
            return false;
        if (displayName == null) {
            if (other.displayName != null)
                return false;
        } else if (!displayName.equals(other.displayName))
            return false;
        if (workingDirectory == null) {
            if (other.workingDirectory != null)
                return false;
        } else if (!workingDirectory.equals(other.workingDirectory))
            return false;
        if (sid == null) {
            if (other.sid != null)
                return false;
        } else if (!sid.equals(other.sid))
            return false;
        if (isolated != other.isolated)
            return false;
        return true;
    }

}
