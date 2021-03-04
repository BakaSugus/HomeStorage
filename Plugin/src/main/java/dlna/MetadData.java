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
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.VideoItem;
import org.seamless.util.MimeType;

import javax.activation.MimeTypeParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MetadData {
    private static final String DIDL_LITE_FOOTER = "</DIDL-Lite>";
    private static final String DIDL_LITE_HEADER = "<?xml version=\"1.0\"?>" +
            "<DIDL-Lite " + "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" " +
            "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " + "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
            "xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">";
    private static final int IMAGE_TYPE = 0;
    private static final int VIDEO_TYPE = 1;
    private static final int AUDIO_TYPE = 2;

    public static String pushMediaToRender(String url, String id, String name, String duration, int ItemType) throws MimeTypeParseException {
        long size = 0;
        Res res = new Res(new MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), size, url);

        String creator = "unknow";
        switch (ItemType) {
            case IMAGE_TYPE:
                return createItemMetadata(new ImageItem(id, "0", name, creator, res));
            case VIDEO_TYPE:
                return createItemMetadata(new VideoItem(id, "0", name, creator, res));
            case AUDIO_TYPE:
                return createItemMetadata(new AudioItem(id, "0", name, creator, res));
        }
        return null;
    }

    private static String createItemMetadata(DIDLObject item) {
        StringBuilder metadata = new StringBuilder();
        metadata.append(DIDL_LITE_HEADER);

        metadata.append(String.format("<item id=\"%s\" parentID=\"%s\" restricted=\"%s\">", item.getId(), item.getParentID(), item.isRestricted() ? "1" : "0"));

        metadata.append(String.format("<dc:title>%s</dc:title>", item.getTitle()));
        String creator = item.getCreator();
        if (creator != null) {
            creator = creator.replaceAll("<", "_");
            creator = creator.replaceAll(">", "_");
        }
        metadata.append(String.format("<upnp:artist>%s</upnp:artist>", creator));
        metadata.append(String.format("<upnp:class>%s</upnp:class>", item.getClazz().getValue()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date now = new Date();
        String time = sdf.format(now);
        metadata.append(String.format("<dc:date>%s</dc:date>", time));

        Res res = item.getFirstResource();
        if (res != null) {
            // protocol info
            String protocolinfo = "";
            ProtocolInfo pi = res.getProtocolInfo();
            if (pi != null) {
                protocolinfo = String.format("protocolInfo=\"%s:%s:%s:%s\"", pi.getProtocol(), pi.getNetwork(), pi.getContentFormatMimeType(), pi
                        .getAdditionalInfo());
            }

            // resolution, extra info, not adding yet
            String resolution = "";
            if (res.getResolution() != null && res.getResolution().length() > 0) {
                resolution = String.format("resolution=\"%s\"", res.getResolution());
            }

            // duration
            String duration = "";
            if (res.getDuration() != null && res.getDuration().length() > 0) {
                duration = String.format("duration=\"%s\"", res.getDuration());
            }

            // res begin
            //            metadata.append(String.format("<res %s>", protocolinfo)); // no resolution & duration yet
            metadata.append(String.format("<res %s %s %s>", protocolinfo, resolution, duration));

            // url
            String url = res.getValue();
            metadata.append(url);

            // res end
            metadata.append("</res>");
        }
        metadata.append("</item>");

        metadata.append(DIDL_LITE_FOOTER);

        return metadata.toString();
    }

}
