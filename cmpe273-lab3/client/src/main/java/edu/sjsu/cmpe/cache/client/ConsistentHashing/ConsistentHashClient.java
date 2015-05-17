package edu.sjsu.cmpe.cache.client.ConsistentHashing;

import com.google.common.collect.Lists;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import edu.sjsu.cmpe.cache.client.CacheService.ServiceInterfaceCache;
import edu.sjsu.cmpe.cache.client.CacheService.CacheServiceDistributed;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aravind on 5/5/2015.
 */

public class ConsistentHashClient {

    private static String server1="http://localhost:3000";
    private static  String server2="http://localhost:3001";
    private static String server3="http://localhost:3002";
    static char values[]={'a','b','c','d','e','f','g','h','i','j'};
    private static final Funnel<CharSequence> strFunnel = Funnels.stringFunnel(Charset.defaultCharset());
    private static final Funnel<Integer> intFunnel = Funnels.integerFunnel();
    public static void main(String[] args) throws Exception {
        System.out.println("Running the Consistent Hashing cache client..");

        List<String> nodes = Lists.newArrayList();

        List<String> servers = new ArrayList<String>();

        servers.add(server1);
        servers.add(server2);
        servers.add(server3);

        System.out.println("Getting values from different servers..");
        System.out.println("-----------------------------------------");
        System.out.println("Server                | Get(Key)=>Value");
        System.out.println("-----------------------------------------");
        for(int i=0;i<10;i++){
            ConsistentHashing<Integer, String> h = new ConsistentHashing(Hashing.murmur3_128(),intFunnel,strFunnel,servers);

            String distributedServerInstance=h.get(new Integer(i));

            ServiceInterfaceCache cacheServer = new CacheServiceDistributed(distributedServerInstance);
            //cacheServer.put(i + 1, String.valueOf(values[i]));
            cacheServer.get(i + 1);
            int j=i+1;
            System.out.println(distributedServerInstance+" | Get("+j+")=>"+cacheServer.get(i+1));


        }
        System.out.println("-----------------------------------------");
        System.out.println("Existing Cache Client...");

    }

}