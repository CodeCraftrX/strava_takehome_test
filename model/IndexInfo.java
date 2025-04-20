package model;

public class IndexInfo {

    private String name;
    private long sizeBytes;
    private int shards;

    /**
     * Constructs a fully defined IndexInfo object.
     *
     * @param name      Name of the index.
     * @param sizeBytes Storage size in bytes.
     * @param shards    Count of primary shards.
     */
    public IndexInfo(String name, long sizeBytes, int shards) {
        this.name = name;
        this.sizeBytes = sizeBytes;
        this.shards = shards;
    }

    /**
     * Gets the index name.
     *
     * @return Index name.
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the number of primary shards.
     *
     * @return Shard count.
     */
    public int getShards(){
        return shards;
    }

    /**
     * Gets the storage size in bytes.
     *
     * @return Size in bytes.
     */
    public long getSizeBytes(){
        return sizeBytes;
    }

    /**
     * Converts the byte size to gigabytes — human readable format
     *
     * @return Size in gigabytes.
     */
    public double getSizeInGB() {
        return sizeBytes / (1_000_000_000.0);
    }

    /**
     * Calculates the balance ratio 
     * @return Balance ratio (GB per shard).
     */
    public double getBalanceRatio() {
        return getSizeInGB() / shards;
    }

    /**
     * Calculates the  recommendation for shard count based on a fixed threshold (30 GB per shard).
     * @return Recommended shard count.
     */
    public int getRecommendedShardCount() {
        int result = (int) (getSizeInGB() / 30.0);
        return Math.max(1, result); //Ensures at least one shard is allocated.
    }    
    
}