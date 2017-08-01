import com.satori.rtm.*;
import com.satori.rtm.model.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    static long firstTime = -1;
    static BlockingQueue<AnyJson> buffer1 = new LinkedBlockingQueue<AnyJson>();
    static int events1 = 0;
    static long filename = 0;

    public static void main(String[] args) throws InterruptedException {
        final String endpoint = "wss://open-data.api.satori.com";
        final String appkey = "783ecdCcb8c5f9E66A56cBFeeeB672C3";
        final String channel = "github-events";
        JsonToSnapshot a = new JsonToSnapshot("thread 1");
        Scanner scanner = new Scanner(System.in);
        Map<Long, Integer> DevsMap = new HashMap<Long, Integer>();
        Map<Long, Integer> RepMap = new HashMap<Long, Integer>();
        final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                .setListener(new RtmClientAdapter() {
                    @Override
                    public void onEnterConnected(RtmClient client) {
                        System.out.println("Connected to Satori RTM!");
                        firstTime = System.currentTimeMillis();
                        filename = System.currentTimeMillis();
                    }
                })
                .build();
        SubscriptionAdapter listener = new SubscriptionAdapter() {
            @Override
            public void onSubscriptionData(SubscriptionData data) {
                for (AnyJson json : data.getMessages()) {
                    if (System.currentTimeMillis() - firstTime >= 600000) {
                        Splitter splitter = new Splitter();
                        buffer1.add(splitter);
                        firstTime = System.currentTimeMillis();
                    }
                    buffer1.add(json);
                    events1++;
                }
            }
        };
        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);
        client.start();
        a.start();
        while(true)
        {
            long x,y;
            x=scanner.nextLong();
            y=scanner.nextLong();
            long t1=System.currentTimeMillis();
            if(x>y){
                long tm=x;
                x=y;
                y=tm;
            }
            x=x*1000*60;
            y=y*1000*60;
            x=t1-x;
            y=t1-y;
            File tm=new File("/home/hosseinkh/Desktop/Data");
            String s[]=tm.list();
            for(int i=0;i<s.length;i++)
            {
                long t=Long.parseLong(s[i]);
                if(t<x && t>y-600000)
                {
                    Scanner scanner1;
                    File file=new File("/home/hosseinkh/Desktop/Data/"+s[i]);
                    try {
                        scanner1=new Scanner(file);
                        while(scanner1.hasNext())
                        {
                           // System.out.println(scanner1.nextLong()+" "+scanner1.nextLong()+" "+scanner1.nextLong());
                            long tmpTime=scanner1.nextLong();
                            if(tmpTime<x && tmpTime>y)
                            {
                                Long DevId=scanner1.nextLong();
                                if(DevsMap.containsKey(DevId))
                                {
                                    int h=DevsMap.get(DevId)+1;
                                    DevsMap.put(DevId,h);
                                }
                                else DevsMap.put(DevId,1);

                                Long RepId=scanner1.nextLong();
                                if(RepMap.containsKey(RepId))
                                {
                                    int h=RepMap.get(RepId)+1;
                                    RepMap.put(RepId,h);
                                }
                                else RepMap.put(RepId,1);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println("found:  "+t);
                }
            }
            int max[] = {0,0,0,0,0,0,0,0,0,0,0};
            long mid[] = {0,0,0,0,0,0,0,0,0,0,0};
            System.out.println("\nNumbers Of Events:" +(events1)+'\n');
            System.out.println("in "+DevsMap.size()+" Devlopers :\n");
            for (Long id : DevsMap.keySet()) {
                max[10]=DevsMap.get(id);
                mid[10]=id;
                for(int i=0;i<11;i++) {
                    for(int j=0;j<10-i;j++)
                    {
                        if(max[j]<max[j+1])
                        {
                            int tmp=max[j];
                            max[j]=max[j+1];
                            max[j+1]=tmp;
                            long tm2=mid[j];
                            mid[j]=mid[j+1];
                            mid[j+1]=tm2;
                        }
                    }
                }
            }
            try{
                PrintWriter writer = new PrintWriter(String.format("/home/hosseinkh/Desktop/logs/Dev%d.txt",System.currentTimeMillis() ), "UTF-8");
                writer.println(DevsMap.size()+" Devlopers :\n");
                for(int i=0;i<10;i++)
                {
                    writer.println("id: "+mid[i]+" events: "+max[i]);
                    System.out.println("id: "+mid[i]+" events: "+max[i]);
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("can not open or write file!");
            }

            int max2[] = {0,0,0,0,0,0,0,0,0,0,0};
            long mid2[]={0,0,0,0,0,0,0,0,0,0,0};
            System.out.println("\nin "+RepMap.size()+" Repositories :\n");
            for (long id : RepMap.keySet()) {
                max2[10]= RepMap.get(id);;
                mid2[10]=id;
                for(int i=0;i<11;i++) {
                    for(int j=0;j<10-i;j++)
                    {
                        if(max2[j]<max2[j+1])
                        {
                            int tmp=max2[j];
                            max2[j]=max2[j+1];
                            max2[j+1]=tmp;
                            long tm2=mid2[j];
                            mid2[j]=mid2[j+1];
                            mid2[j+1]=tm2;
                        }
                    }
                }
            }
            try{
                PrintWriter writer = new PrintWriter(String.format("/home/hosseinkh/Desktop/logs/Rep%d",System.currentTimeMillis() ), "UTF-8");
                writer.println("\n"+RepMap.size()+" Repositories :\n");
                for(int i=0;i<10;i++)
                {
                    writer.println("id: "+mid2[i]+" events: "+max2[i]);
                    System.out.println("id: "+mid2[i]+" events: "+max2[i]);
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("can not open or write file!");
            }
        }

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
            StringBuilder mytmpstr = new StringBuilder();
            try {
                while (true) {
                    if (buffer1.isEmpty())
                        continue;
                    AnyJson tmp = buffer1.take();
                    if (tmp instanceof Splitter) {
                        System.out.println("#");
                        FileWriter writer = null;
                        try {
                            String tm = String.format("/home/hosseinkh/Desktop/Data/%d", filename);
                            writer = new FileWriter(new File(tm));
                            writer.write(mytmpstr.toString());
                            writer.flush();
                            filename=System.currentTimeMillis();
                            mytmpstr = new StringBuilder();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    snapshot snapshot = tmp.convertToType(snapshot.class);
                    snapshot.time = System.currentTimeMillis();
                    mytmpstr.append(snapshot.toString() + '\n');
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
