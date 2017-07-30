import com.satori.rtm.*;
import com.satori.rtm.model.*;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SubscribeToOpenChannel {
    static final String endpoint = "wss://open-data.api.satori.com";
    static final String appkey = "783ecdCcb8c5f9E66A56cBFeeeB672C3";
    static final String channel = "github-events";
    static long firstT = -1;
    static boolean myflag = true;
    static BlockingQueue<AnyJson> buffer1 = new LinkedBlockingQueue<AnyJson>();
    //static BlockingQueue<String> buffer2 = new LinkedBlockingQueue<String>();
    static JsonToSnapshot a = new JsonToSnapshot("thread 1");
    //static SnapshotToP b = new SnapshotToP("thread 2");
    static int events1=0;
    static int events2=0;
    static long filename=0;
    static StringBuilder mytmpstr=new StringBuilder();
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner=new Scanner(System.in);
        final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                .setListener(new RtmClientAdapter() {
                    @Override
                    public void onEnterConnected(RtmClient client) {
                        System.out.println("Connected to Satori RTM!");
                        firstT = System.currentTimeMillis();
                    }
                })
                .build();
        SubscriptionAdapter listener = new SubscriptionAdapter() {
            @Override
            public void onSubscriptionData(SubscriptionData data) {
                //System.out.println(System.currentTimeMillis());
                for (AnyJson json : data.getMessages()) {
                    if(System.currentTimeMillis()-firstT>=600000)
                    {
                        filename=System.currentTimeMillis();
                        Fclass fc=new Fclass();
                        buffer1.add(fc);
                        firstT=System.currentTimeMillis();
                    }
                    buffer1.add(json);
                    events1++;
                }
            }
        };
        int tmp=0;
        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);
        client.start();
        a.start();
        //b.start();
    }
    static class JsonToSnapshot implements Runnable {
        private Thread t;
        private String threadName;

        JsonToSnapshot(String name) {
            threadName = name;
            System.out.println("Creating " + threadName);
        }
        public void run() {
            System.out.println("Running " + threadName);
            try {
                while (true) {
                    if(buffer1.isEmpty())
                        continue;
                    AnyJson tmp = buffer1.take();
                    if(tmp instanceof Fclass)
                    {
                        System.out.println("#");
                        FileWriter writer= null;
                        try {
                            String tm=String.format("/home/hosseinkh/Desktop/logs/%d",filename);
                            writer = new FileWriter(new File(tm));
                            writer.write(mytmpstr.toString());
                            writer.flush();
                            mytmpstr=new StringBuilder();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    snapshot tmp2 = tmp.convertToType(snapshot.class);
                    tmp2.time=System.currentTimeMillis();
                    mytmpstr.append(tmp2.Tostring()+'\n');
                }
            } catch (InterruptedException e) {
                System.out.println("JsonToSnapshot Interrupted");
            }
            System.out.println(threadName + " exiting.");
        }

        public Thread getT() {
            return t;
        }

        public void start() {
            System.out.println("Starting " + threadName);
            if (t == null) {
                t = new Thread(this, threadName);
                t.start();
            }
        }
    }
}
