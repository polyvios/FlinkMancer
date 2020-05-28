/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flinkmancer;

import java.util.HashSet;
import java.util.Set;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 *
 * @author Dvogiatz
 */
public class Flinkmancer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        //BasicConfigurator.configure(); uncomment gia log4j
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();


        String followPath = "src/data/followers/";
        String replyPath = "src/data/reply/";
        String retweetPath = "src/data/retweet/";
        String quotePath = "src/data/quote/";

        //Read follow dataset
        DataSet<Tuple2<Long, Long>> followSet = env.readCsvFile(followPath)
                .fieldDelimiter(" ")
                .includeFields(true, true)
                .types(Long.class, Long.class);

        DataSet<Tuple2<Long, Set<Long>>> inc_follow = followSet.groupBy(0).reduceGroup(new GroupReduceFirst());
        DataSet<Tuple2<Long, Set<Long>>> out_follow = followSet.groupBy(1).reduceGroup(new GroupReduceSecond());
        System.out.println("Incoming follow set");
        inc_follow.print();
        System.out.println("Outgoing follow set");
        out_follow.print();


        //Read reply set
        DataSet<Tuple2<Long, Long>> replySet = env.readCsvFile(replyPath)
                .fieldDelimiter(" ")
                .includeFields(true, true)
                .types(Long.class, Long.class);

        DataSet<Tuple2<Long, Set<Long>>> inc_reply = replySet.groupBy(0).reduceGroup(new GroupReduceFirst());
        DataSet<Tuple2<Long, Set<Long>>> out_reply = replySet.groupBy(1).reduceGroup(new GroupReduceSecond());
        System.out.println("Incoming reply set");
        //inc_reply.print();
        System.out.println("Outgoing reply set");
        //out_reply.print();

        //Read retweet set
        DataSet<Tuple2<Long, Long>> retweetSet = env.readCsvFile(retweetPath)
                .fieldDelimiter(" ")
                .includeFields(true, true)
                .types(Long.class, Long.class);

        DataSet<Tuple2<Long, Set<Long>>> inc_retweet = retweetSet.groupBy(0).reduceGroup(new GroupReduceFirst());
        DataSet<Tuple2<Long, Set<Long>>> out_retweet = retweetSet.groupBy(1).reduceGroup(new GroupReduceSecond());
        System.out.println("Incoming retweet set");
        //inc_retweet.print();
        System.out.println("Outgoing retweet set");
        //out_retweet.print();

        //Read quote set
        DataSet<Tuple2<Long, Long>> quoteSet = env.readCsvFile(quotePath)
                .fieldDelimiter(" ")
                .includeFields(true, true)
                .types(Long.class, Long.class);

        DataSet<Tuple2<Long, Set<Long>>> inc_quote = quoteSet.groupBy(0).reduceGroup(new GroupReduceFirst());
        DataSet<Tuple2<Long, Set<Long>>> out_quote = quoteSet.groupBy(1).reduceGroup(new GroupReduceSecond());
        System.out.println("Incoming quote set");
        //inc_quote.print();
        System.out.println("Outgoing quote set");
        //out_quote.print();

    }
    /*
    * used in reduceGroup to create the list of neighbors
     */
    public static class GroupReduceFirst
            implements GroupReduceFunction<Tuple2<Long, Long>, Tuple2<Long, Set<Long>>> {

        @Override
        public void reduce(Iterable<Tuple2<Long, Long>> in, Collector<Tuple2<Long, Set<Long>>> out) {

            Set<Long> groupedSet = new HashSet<>();
            Long key = null;

            // add all neighbors in set
            for (Tuple2<Long, Long> t : in) {
                key = t.f0;
                groupedSet.add(t.f1);
            }

            out.collect(new Tuple2<>(key, groupedSet));

        }
    }
    /*
    * used in reduceGroup. same as above only changed to take group by second.
     */
    public static class GroupReduceSecond
            implements GroupReduceFunction<Tuple2<Long, Long>, Tuple2<Long, Set<Long>>> {

        @Override
        public void reduce(Iterable<Tuple2<Long, Long>> in, Collector<Tuple2<Long, Set<Long>>> out) {

            Set<Long> groupedSet = new HashSet<>();
            Long key = null;

            // add all neighbors in set
            for (Tuple2<Long, Long> t : in) {
                key = t.f1;
                groupedSet.add(t.f0);
            }

            out.collect(new Tuple2<>(key, groupedSet));

        }
    }

}
