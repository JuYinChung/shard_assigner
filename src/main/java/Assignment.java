import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Assignment {
    @JsonProperty("id")
    private String id;
    @JsonProperty("collection")
    private String index;
    @JsonProperty("shard")
    private String shardName;

    public Assignment(String id, String index, String shardName) {
        this.id = id;
        this.index = index;
        this.shardName = shardName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getShardName() {
        return shardName;
    }

    public void setShardName(String shardName) {
        this.shardName = shardName;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id='" + id + '\'' +
                ", index='" + index + '\'' +
                ", shardName='" + shardName + '\'' +
                '}';
    }
}
