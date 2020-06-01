/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flinkmancer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple8;
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
        env.setParallelism(4); // xwris den trexei logo heap size p moirazontai ta task. dn exw to flink-conf.yaml

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
        // inc_follow.print();
        System.out.println("Outgoing follow set");
        // out_follow.print();
        System.out.println("All follow set");
        DataSet<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> all_follow = inc_follow.fullOuterJoin(out_follow).where("f0").equalTo("f0").with(new OuterJoin());
        // all_follow.print();


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
        System.out.println("All reply set");
        DataSet<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> all_reply = inc_reply.fullOuterJoin(out_reply).where("f0").equalTo("f0").with(new OuterJoin());
        //all_reply.print();

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
        System.out.println("All retweet set");
        DataSet<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> all_retweet = inc_retweet.fullOuterJoin(out_retweet).where("f0").equalTo("f0").with(new OuterJoin());
        //all_retweet.print();

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
        System.out.println("All quote set");
        DataSet<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> all_quote = inc_quote.fullOuterJoin(out_quote).where("f0").equalTo("f0").with(new OuterJoin());
        //all_quote.print();
        DataSet<Tuple2<Long, Tuple4<Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> followXreply = all_follow.fullOuterJoin(all_reply).where("f0").equalTo("f0").with(new OuterJoinTuple4());
        //followXreply.print();

        DataSet<Tuple2<Long, Tuple4<Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> retweetXquote = all_retweet.fullOuterJoin(all_quote).where("f0").equalTo("f0").with(new OuterJoinTuple4());
        //retweetXquote.print();

        DataSet<Tuple2<Long, Tuple8<Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> all = followXreply.fullOuterJoin(retweetXquote).where("f0").equalTo("f0").with(new OuterJoinTuple8());
        //all.print();
        DataSet<Node> vertices = all.groupBy(0).reduceGroup(new NodeCreator());
        // vertices.print();
        //DataSet<Tuple2<Node, Node>> Vpairs = vertices.cross(vertices);  takes more than 15 mins. 
        //Vpairs.print();

        /*
        DataSet<Tuple2<Long, Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>>> followXreply = all_follow.fullOuterJoin(all_reply).where("f0").equalTo("f0").with(new OuterJoinTuple2());
        //followXreply.print();
        DataSet<Tuple2<Long, Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>>> retweetXquote = all_retweet.fullOuterJoin(all_quote).where("f0").equalTo("f0").with(new OuterJoinTuple2());
        //retweetXquote.print();

        DataSet<Tuple2<Long, Tuple2<Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>, Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>>>> all = followXreply.fullOuterJoin(retweetXquote).where("f0").equalTo("f0").with(new OuterJoinTuple22());
        System.out.println("All set");
        // all.print();

         */
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

    public static class NodeCreator
            implements GroupReduceFunction<Tuple2<Long, Tuple8<Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>>>, Node> {

        @Override
        public void reduce(Iterable<Tuple2<Long, Tuple8<Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> in, Collector<Node> out) {

            ArrayList<Set<Long>> ali = new ArrayList<>();
            Long key = null;

            // add all strings of the group to the set
            for (Tuple2<Long, Tuple8<Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>>> t : in) {
                key = t.f0;
                ali.add(t.f1.f0);     //follow in - reply - retweet - quote
                ali.add(t.f1.f1);     //follow out
                ali.add(t.f1.f2);     //reply in
                ali.add(t.f1.f3);     //reply out
                ali.add(t.f1.f4);     //retweet in
                ali.add(t.f1.f5);     //retweet out
                ali.add(t.f1.f6);     //quote in
                ali.add(t.f1.f7);     //quote out

            }

            out.collect(new Node(key, ali));

        }
    }


    public static class OuterJoin
            implements FlatJoinFunction<Tuple2<Long, Set<Long>>, Tuple2<Long, Set<Long>>, Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> {

        @Override
        public void join(Tuple2<Long, Set<Long>> dataset1, Tuple2<Long, Set<Long>> dataset2, Collector<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> out) {
            if (dataset1 != null && dataset2 != null) {
                Long key = dataset1.f0;

            Set<Long> s1 = dataset1.f1;
            Set<Long> s2 = dataset2.f1;


                out.collect(new Tuple2<>(key, (new Tuple2<>(s1, s2))));
            } else if (dataset1 != null) {
                Long key = dataset1.f0;
                Set<Long> s1 = dataset1.f1;
                out.collect(new Tuple2<>(key, (new Tuple2<>(s1, new HashSet<>()))));
            } else if (dataset2 != null) {
                Long key = dataset2.f0;
                Set<Long> s2 = dataset2.f1;
                out.collect(new Tuple2<>(key, (new Tuple2<>(new HashSet<>(), s2))));
            }

        }
    }

    public static class OuterJoinTuple4
            implements FlatJoinFunction<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>, Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>, Tuple2<Long, Tuple4<Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> {

        @Override
        public void join(Tuple2<Long, Tuple2<Set<Long>, Set<Long>>> dataset1, Tuple2<Long, Tuple2<Set<Long>, Set<Long>>> dataset2, Collector<Tuple2<Long, Tuple4<Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> out) {
            Set<Long> s1 = new HashSet<>();
            Set<Long> s2 = new HashSet<>();
            Set<Long> s3 = new HashSet<>();
            Set<Long> s4 = new HashSet<>();
            Long key = null;

            if (dataset1 != null) {
                if (dataset1.f1 != null) {
                    key = dataset1.f0;
                    s1 = dataset1.f1.f0;
                    s2 = dataset1.f1.f1;
                }
            }
            if (dataset2 != null) {
                if (dataset2.f1 != null) {
                    key = dataset2.f0;
                    s3 = dataset2.f1.f0;
                    s4 = dataset2.f1.f1;
                }
            }
            if (dataset1 != null || dataset2 != null) {
                out.collect(new Tuple2<>(key, (new Tuple4<>(s1, s2, s3, s4))));
            }

        }
    }

    public static class OuterJoinTuple8
            implements FlatJoinFunction<Tuple2<Long, Tuple4<Set<Long>, Set<Long>, Set<Long>, Set<Long>>>, Tuple2<Long, Tuple4<Set<Long>, Set<Long>, Set<Long>, Set<Long>>>, Tuple2<Long, Tuple8<Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> {

        @Override
        public void join(Tuple2<Long, Tuple4<Set<Long>, Set<Long>, Set<Long>, Set<Long>>> dataset1, Tuple2<Long, Tuple4<Set<Long>, Set<Long>, Set<Long>, Set<Long>>> dataset2, Collector<Tuple2<Long, Tuple8<Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> out) {
            Set<Long> s1 = new HashSet<>();
            Set<Long> s2 = new HashSet<>();
            Set<Long> s3 = new HashSet<>();
            Set<Long> s4 = new HashSet<>();
            Set<Long> s5 = new HashSet<>();
            Set<Long> s6 = new HashSet<>();
            Set<Long> s7 = new HashSet<>();
            Set<Long> s8 = new HashSet<>();
            Long key = null;

            if (dataset1 != null) {
                if (dataset1.f1 != null) {
                    key = dataset1.f0;
                    s1 = dataset1.f1.f0;
                    s2 = dataset1.f1.f1;
                    s3 = dataset1.f1.f2;
                    s4 = dataset1.f1.f3;
                }
            }
            if (dataset2 != null) {
                if (dataset2.f1 != null) {
                    key = dataset2.f0;
                    s5 = dataset2.f1.f0;
                    s6 = dataset2.f1.f1;
                    s7 = dataset2.f1.f2;
                    s8 = dataset2.f1.f3;
                }
            }
            if (dataset1 != null || dataset2 != null) {
                out.collect(new Tuple2<>(key, (new Tuple8<>(s1, s2, s3, s4, s5, s6, s7, s8))));
            }

        }
    }
    /*
    
    public static class OuterJoinTuple2
            implements FlatJoinFunction<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>, Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>, Tuple2<Long, Tuple2<Tuple2<Set<Long>, Set<Long>>,Tuple2<Set<Long>, Set<Long>>>>> {

        @Override
        public void join(Tuple2<Long, Tuple2<Set<Long>, Set<Long>>> dataset1, Tuple2<Long, Tuple2<Set<Long>, Set<Long>>> dataset2, Collector<Tuple2<Long, Tuple2<Tuple2<Set<Long>, Set<Long>>,Tuple2<Set<Long>, Set<Long>>>>> out) {
            if (dataset1 != null && dataset2 != null) {
                Long key = dataset1.f0;
                Tuple2<Set<Long>,Set<Long>> s1 = dataset1.f1;
                Tuple2<Set<Long>,Set<Long>> s2 = dataset2.f1;
                out.collect(new Tuple2<>(key, (new Tuple2<>(s1, s2))));
            } else if (dataset1 != null) {
                Long key = dataset1.f0;
                Tuple2<Set<Long>,Set<Long>> s1 = dataset1.f1;
                Set<Long> t1 = new HashSet<>();
                Set<Long> t2 = new HashSet<>();
                Tuple2<Set<Long>,Set<Long>> t3 = new Tuple2<>(t1,t2);
                out.collect(new Tuple2<>(key, (new Tuple2<>(s1, t3))));
            } else if (dataset2 != null) {
                Long key = dataset2.f0;
                Tuple2<Set<Long>,Set<Long>> s2 = dataset2.f1;
                Set<Long> t1 = new HashSet<>();
                Set<Long> t2 = new HashSet<>();
                Tuple2<Set<Long>,Set<Long>> t3 = new Tuple2<>(t1,t2);
                out.collect(new Tuple2<>(key, (new Tuple2<>(t3, s2))));
            }

        }
    }

    public static class OuterJoinTuple22
            implements FlatJoinFunction<Tuple2<Long, Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>>, Tuple2<Long, Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>>, Tuple2<Long, Tuple2<Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>, Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>>>> {

        @Override
        public void join(Tuple2<Long, Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>> dataset1, Tuple2<Long, Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>> dataset2, Collector<Tuple2<Long, Tuple2<Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>, Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>>>>> out) {
            if (dataset1 != null && dataset2 != null) {
                Long key = dataset1.f0;
                Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>> s1 = dataset1.f1;
                Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>> s2 = dataset2.f1;
                out.collect(new Tuple2<>(key, (new Tuple2<>(s1, s2))));
            } else if (dataset1 != null) {
                Long key = dataset1.f0;
                Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>> s1 = dataset1.f1;
                Set<Long> t1 = new HashSet<>();
                Set<Long> t2 = new HashSet<>();
                Tuple2<Set<Long>, Set<Long>> t3 = new Tuple2<>(t1, t2);
                Tuple2<Set<Long>, Set<Long>> t4 = new Tuple2<>(t1, t2);
                Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>> t5 = new Tuple2<>(t3, t4);
                out.collect(new Tuple2<>(key, (new Tuple2<>(s1, t5))));
            } else if (dataset2 != null) {
                Long key = dataset2.f0;
                Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>> s2 = dataset2.f1;
                Set<Long> t1 = new HashSet<>();
                Set<Long> t2 = new HashSet<>();
                Tuple2<Set<Long>, Set<Long>> t3 = new Tuple2<>(t1, t2);
                Tuple2<Set<Long>, Set<Long>> t4 = new Tuple2<>(t1, t2);
                Tuple2<Tuple2<Set<Long>, Set<Long>>, Tuple2<Set<Long>, Set<Long>>> t5 = new Tuple2<>(t3, t4);
                out.collect(new Tuple2<>(key, (new Tuple2<>(t5, s2))));
            }

        }
    }
    */
}
