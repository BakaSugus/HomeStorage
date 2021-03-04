package dlna;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

public class BrowseRegistryListener extends DefaultRegistryListener {
    private String info;
    private String resourceUri;
    private DLNAService dlnaService;

    public BrowseRegistryListener(DLNAService dlnaService, String info, String resourceUri) {
        this.dlnaService = dlnaService;
        this.info = info;
        this.resourceUri = resourceUri;
    }

    @Override
    public void deviceAdded(Registry registry, final Device device) {
        super.deviceAdded(registry, device);
        if (device.getDetails().getFriendlyName().equals(info)){
            dlnaService.play(device,resourceUri);
        }
    }


    @Override
    public void deviceRemoved(Registry registry, final Device device) {
        super.deviceRemoved(registry, device);
    }
}