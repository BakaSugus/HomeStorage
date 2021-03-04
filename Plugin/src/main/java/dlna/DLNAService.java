package dlna;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;

import javax.activation.MimeTypeParseException;

import socket.Request;
import tool.Ext;

public class DLNAService {
    private String info;
    private String uri;
    private UpnpService upnpService;
    private ControlPoint mControlPoint;

    public DLNAService(Request request) {
        upnpService = new UpnpServiceImpl();
        mControlPoint = upnpService.getControlPoint();
        this.info = request.getParam("info");
        this.uri = request.getParam("uri");
    }

    public void searchAndConn() {
        BrowseRegistryListener sRegistryListener = new BrowseRegistryListener(this, info, uri);
        upnpService.getRegistry().addListener(sRegistryListener);
        mControlPoint.search();
    }

    public void play(Device device, String uri) {
        final ServiceType AV_TRANSPORT_SERVICE = new UDAServiceType("AVTransport");
        final Service avtService = device.findService(AV_TRANSPORT_SERVICE);
        try {
            int item;
            if (Ext.GetTypeByFileName(uri).equals("Music")) item = 2;
            else if (Ext.GetTypeByFileName(uri).equals("Video")) item = 0;
            else item = 1;
            String metadata = MetadData.pushMediaToRender(uri, "id", "name", "0", item);
            mControlPoint.execute(new SetAVTransportURI(avtService, uri, metadata) {
                @Override
                public void success(ActionInvocation invocation) {
                    super.success(invocation);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                }
            });
        } catch (MimeTypeParseException e) {
            e.printStackTrace();
        }

    }

}
