
package hu.gdf.thesis.model.config;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import hu.gdf.thesis.backend.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;

@Generated("jsonschema2pojo")
public class Config {

    @SerializedName("server")
    @Expose
    private Server server;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

}
