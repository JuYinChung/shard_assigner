package assigner;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class Shard {
	@JsonProperty("collection")
	private String index;
	private int shardId;
	@JsonProperty("shard")
	private String shardName;
	@JsonProperty("size")
	private long size;
	private boolean isPrimary = true;

	public Shard(String index, String shardName, long size) {
		this.index = index;
		this.shardName = shardName;
		this.shardId = Integer.parseInt(shardName.substring(5, shardName.length()));
		this.size = size;
	}

	public Shard() {
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
		this.shardId = Integer.parseInt(shardName.substring(5, shardName.length()));
	}

	public int getShardId() {
		return shardId;
	}

	public void setShardId(final int shardId) {
		this.shardId = shardId;
	}

	public long getSize() {
		return size;
	}

	public void setSize(final long size) {
		this.size = size;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean primary) {
		isPrimary = primary;
	}

	public void setReplica() {
		isPrimary = false;
	}

	@Override
    public String toString() {
        return "Shard{" +
                "index=" + index +
                ", shardId=" + shardId +
                ", size=" + size +
				", isPrimary=" + isPrimary +
                '}';
    }

	@Override
	protected Shard clone() {
		return new Shard(this.index, this.shardName, this.size);
	}
}