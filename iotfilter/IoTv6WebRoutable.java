package net.floodlightcontroller.iotfilter;

import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class IoTv6WebRoutable implements RestletRoutable {
    /**
     * Create the Restlet router and bind to the proper resources.
     */
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/{switch}/json", IoTResource.class);
        router.attach("/disableDuplicates", IoTResDisableDuplicates.class);
        /*router.attach("/json/store", StaticFlowEntryPusherResource.class);
        router.attach("/json/delete", StaticFlowEntryDeleteResource.class);
        router.attach("/clear/{switch}/json", ClearStaticFlowEntriesResource.class);
        router.attach("/list/{switch}/json", ListStaticFlowEntriesResource.class);*/
        return router;
    }
 
    /**
     * Set the base path for the Topology
     */
    @Override
    public String basePath() {
        return "/wm/iot";
    }
}
