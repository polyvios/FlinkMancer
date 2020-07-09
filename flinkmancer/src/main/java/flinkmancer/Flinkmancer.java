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
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple8;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
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
        Configuration configuration = env.getConfiguration();
        // env.getConfig().setParallelism(4);
        //configuration.setString("taskmanager.numberOfTaskSlots", "1");
        // create type info
        final int cores;
        final int outcores;
        final String outpath;
        final String path;

        try {
            final ParameterTool params = ParameterTool.fromArgs(args);
            cores = params.getInt("cores");
            path = params.get("path");
            outpath = params.has("outpath") ? params.get("outpath") : "src/data/results/BIGfeatures.csv";
            outcores = params.has("outcores") ? params.getInt("outcores") : cores;
            env.setParallelism(cores); // xwris den trexei logo heap size p moirazontai ta task. dn exw to flink-conf.yaml
        } catch (Exception e) {
            System.err.println("No cores specified. Please run 'flinkmancer*.jar "
                    + "--cores <cores> --path <path> --outcores <outcores> --outpath<outpath> ', where cores is the number of parallelism "
                    + "and path is path/to/file/ ");
            return;
        }



        
        // System.out.println(configuration.toString());
        String followPath = path + "followers/";
        String replyPath = path + "reply/";
        String retweetPath = path + "retweet/";
        String quotePath = path + "quote/";

        //Read follow dataset
        DataSet<Tuple2<Long, Long>> followSet = env.readCsvFile(followPath)
                .fieldDelimiter(" ")
                .includeFields(true, true)
                .types(Long.class, Long.class);

        DataSet<Tuple2<Long, Set<Long>>> inc_follow = followSet.groupBy(1).reduceGroup(new GroupReduceSecond());
        DataSet<Tuple2<Long, Set<Long>>> out_follow = followSet.groupBy(0).reduceGroup(new GroupReduceFirst());
        //System.out.println("Incoming follow set");
        //inc_follow.print();
        // System.out.println("Outgoing follow set");
        //out_follow.print();
        // System.out.println("All follow set");
        DataSet<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> all_follow = inc_follow.fullOuterJoin(out_follow).where("f0").equalTo("f0").with(new OuterJoin());
        //all_follow.print();


        //Read reply set
        DataSet<Tuple2<Long, Long>> replySet = env.readCsvFile(replyPath)
                .fieldDelimiter(" ")
                .includeFields(true, true)
                .types(Long.class, Long.class);

        DataSet<Tuple2<Long, Set<Long>>> inc_reply = replySet.groupBy(1).reduceGroup(new GroupReduceSecond());
        DataSet<Tuple2<Long, Set<Long>>> out_reply = replySet.groupBy(0).reduceGroup(new GroupReduceFirst());
        // System.out.println("Incoming reply set");
        //inc_reply.print();
       // System.out.println("Outgoing reply set");
        //out_reply.print();
        //System.out.println("All reply set");
        DataSet<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> all_reply = inc_reply.fullOuterJoin(out_reply).where("f0").equalTo("f0").with(new OuterJoin());
        //all_reply.print();

        //Read retweet set
        DataSet<Tuple2<Long, Long>> retweetSet = env.readCsvFile(retweetPath)
                .fieldDelimiter(" ")
                .includeFields(true, true)
                .types(Long.class, Long.class);

        DataSet<Tuple2<Long, Set<Long>>> inc_retweet = retweetSet.groupBy(1).reduceGroup(new GroupReduceSecond());
        DataSet<Tuple2<Long, Set<Long>>> out_retweet = retweetSet.groupBy(0).reduceGroup(new GroupReduceFirst());
        //System.out.println("Incoming retweet set");
        //inc_retweet.print();
       // System.out.println("Outgoing retweet set");
        //out_retweet.print();
        //System.out.println("All retweet set");
        DataSet<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> all_retweet = inc_retweet.fullOuterJoin(out_retweet).where("f0").equalTo("f0").with(new OuterJoin());
        //all_retweet.print();

        //Read quote set
        DataSet<Tuple2<Long, Long>> quoteSet = env.readCsvFile(quotePath)
                .fieldDelimiter(" ")
                .includeFields(true, true)
                .types(Long.class, Long.class);

        DataSet<Tuple2<Long, Set<Long>>> inc_quote = quoteSet.groupBy(1).reduceGroup(new GroupReduceSecond());
        DataSet<Tuple2<Long, Set<Long>>> out_quote = quoteSet.groupBy(0).reduceGroup(new GroupReduceFirst());
        //System.out.println("Incoming quote set");
        //inc_quote.print();
        //System.out.println("Outgoing quote set");
        //out_quote.print();
       // System.out.println("All quote set");
        DataSet<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> all_quote = inc_quote.fullOuterJoin(out_quote).where("f0").equalTo("f0").with(new OuterJoin());
        //all_quote.print();

        DataSet<Tuple2<Long, Tuple4<Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> followXreply = all_follow.fullOuterJoin(all_reply).where("f0").equalTo("f0").with(new OuterJoinTuple4());
        DataSet<Tuple2<Long, Tuple4<Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> retweetXquote = all_retweet.fullOuterJoin(all_quote).where("f0").equalTo("f0").with(new OuterJoinTuple4());
        DataSet<Tuple2<Long, Tuple8<Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>, Set<Long>>>> all = followXreply.fullOuterJoin(retweetXquote).where("f0").equalTo("f0").with(new OuterJoinTuple8());
        DataSet<Node> vertices = all.groupBy(0).reduceGroup(new NodeCreator());



        DataSet<Tuple2<Node, Node>> Vpairs = vertices.cross(vertices);  //takes more than 15 mins.
        //Vpairs.print();


        DataSet<Tuple2<String, String>> features = Vpairs.flatMap(new Features.Feat());
        DataSet<Tuple2<Integer, Integer>> redres = features.flatMap(new ReduceSize());
        redres.writeAsCsv(outpath, "\n", ",", FileSystem.WriteMode.OVERWRITE).setParallelism(outcores); //comment this for reduced output
        //features.writeAsCsv(outpath, "\n", ",", FileSystem.WriteMode.OVERWRITE).setParallelism(outcores); //uncomment this for reduced output
        env.execute();



    }

    //helper function to reduce the ouput
    public static class ReduceSize implements FlatMapFunction<Tuple2<String, String>, Tuple2<Integer, Integer>> {

        static int counter;

        @Override
        public void flatMap(Tuple2<String, String> in, Collector<Tuple2<Integer, Integer>> out) {
            String s1 = in.f0;
            String s2 = in.f1;
            counter++;

            out.collect(new Tuple2<>(counter, (s1.length() + s2.length())));

        }
    }
    /*
    * used in reduceGroup. groups by first
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
    /*
       *    Transform to Node
     */
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

    /*
    * OuterJoin for Single sets
     */
    public static class OuterJoin
            implements FlatJoinFunction<Tuple2<Long, Set<Long>>, Tuple2<Long, Set<Long>>, Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> {

        @Override
        public void join(Tuple2<Long, Set<Long>> dataset1, Tuple2<Long, Set<Long>> dataset2, Collector<Tuple2<Long, Tuple2<Set<Long>, Set<Long>>>> out) {
            Set<Long> s1 = new HashSet<>();
            Set<Long> s2 = new HashSet<>();
            Long key = null;

            if (dataset1 != null) {
                key = dataset1.f0;
                s1 = dataset1.f1;

            }
            if (dataset2 != null) {
                key = dataset2.f0;
                s2 = dataset2.f1;
            }
            if (dataset1 != null || dataset2 != null) {
                out.collect(new Tuple2<>(key, (new Tuple2<>(s1, s2))));
            }
        }
    }

    /*
    * OuterJoin for 4 Sets
     */
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
    /*
    * Outerjoin for 8 sets
     */
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

}

