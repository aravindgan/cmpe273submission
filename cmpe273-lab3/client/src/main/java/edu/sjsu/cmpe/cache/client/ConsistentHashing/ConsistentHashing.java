package edu.sjsu.cmpe.cache.client.ConsistentHashing;

import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
/**
 * Created by aravind on 5/5/2015.
 */

public class ConsistentHashing<K, N> {

	private final HashFunction hashFunction;
	private final SortedMap<Long, N> ring = new TreeMap<Long, N>();
	private Funnel<N> nodeFunnel;
	private Funnel<K> keyFunnel;

	public ConsistentHashing(HashFunction hashFunction, Funnel<K> keyFunnel, Funnel<N> nodeFunnel, Collection<N> nodes) {
		this.hashFunction = hashFunction;
		this.nodeFunnel = nodeFunnel;
		this.keyFunnel = keyFunnel;
		for (N node : nodes) {
			add(node);
		}
	}

    /*
        add a node to the ring for consistent hashing
    */
	public boolean add(N node) { 
		ring.put(hashFunction.newHasher().putObject(node, nodeFunnel).hash().asLong(), node); 
		return true;
	}

    /*
        remove a node from the ring for consistent hashing
    */
	public boolean remove(N node) {
		return node == ring.remove(hashFunction.newHasher().putObject(node, nodeFunnel).hash().asLong()); 
	}

    /*
        get a node of the ring as per key value provided
        we use tailmap to get only the higher then current value or the initial value if not node has no key
    */
	public N get(K key) { 
		Long hash = hashFunction.newHasher().putObject(key, keyFunnel).hash().asLong();
		if (!ring.containsKey(hash)) {
			SortedMap<Long, N> tailMap = ring.tailMap(hash);
			hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
		}
		return ring.get(hash);
	}
}
