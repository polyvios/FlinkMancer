/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flinkmancer;

import java.util.HashSet;
import java.util.Set;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 *
 * @author Hergal
 */
public class Features {
    public static class T1
            implements FlatMapFunction<Tuple2<Node, Node>, Tuple2<Tuple2<Long, Long>, Integer>> {
        private int layer;
        private int inc;
        private int out;
        T1(int i) {
            this.layer = i;
            this.inc = 2 * i - 2;
            this.out = 2 * i - 1;
        }

        T1() {
            this.layer = 1;
            this.inc = 0;
            this.out = 1;
        }
        @Override
        public void flatMap(Tuple2<Node, Node> in, Collector<Tuple2<Tuple2<Long, Long>, Integer>> out) {

            Set<Long> groupedSet = new HashSet<>();
            Node key1 = null;
            Node key2 = null;
            int count = 0;
            // add all neighbors in set

            key1 = in.f0;
            key2 = in.f1;
            groupedSet = key1.getSet(this.inc);
            groupedSet.retainAll(key2.getSet(this.inc));
            count = groupedSet.size();
            out.collect(new Tuple2<>(new Tuple2<>(key1.getId(), key2.getId()), count));

        }
    }

    public static class T2
            implements FlatMapFunction<Tuple2<Node, Node>, Tuple2<Tuple2<Long, Long>, Integer>> {
        private int layer;
        private int inc;
        private int out;

        T2(int i) {
            this.layer = i;
            this.inc = 2 * i - 2;
            this.out = 2 * i - 1;
        }

        T2() {
            this.layer = 1;
            this.inc = 0;
            this.out = 1;
        }
        @Override
        public void flatMap(Tuple2<Node, Node> in, Collector<Tuple2<Tuple2<Long, Long>, Integer>> out) {

            Set<Long> groupedSet = new HashSet<>();
            Node key1 = null;
            Node key2 = null;
            int count = 0;
            // add all neighbors in set

            key1 = in.f0;
            key2 = in.f1;
            groupedSet = key1.getSet(this.out);
            groupedSet.retainAll(key2.getSet(this.out));
            count = groupedSet.size();
            out.collect(new Tuple2<>(new Tuple2<>(key1.getId(), key2.getId()), count));

        }
    }

    public static class T3
            implements FlatMapFunction<Tuple2<Node, Node>, Tuple2<Tuple2<Long, Long>, Integer>> {
        private int layer;
        private int inc;
        private int out;

        T3(int i) {
            this.layer = i;
            this.inc = 2 * i - 2;
            this.out = 2 * i - 1;
        }

        T3() {
            this.layer = 1;
            this.inc = 0;
            this.out = 1;
        }
        @Override
        public void flatMap(Tuple2<Node, Node> in, Collector<Tuple2<Tuple2<Long, Long>, Integer>> out) {

            Set<Long> groupedSet = new HashSet<>();
            Node key1 = null;
            Node key2 = null;
            int count = 0;
            // add all neighbors in set

            key1 = in.f0;
            key2 = in.f1;
            groupedSet = key1.getSet(this.out);
            groupedSet.retainAll(key2.getSet(this.inc));
            count = groupedSet.size();
            out.collect(new Tuple2<>(new Tuple2<>(key1.getId(), key2.getId()), count));

        }
    }

    public static class T4
            implements FlatMapFunction<Tuple2<Node, Node>, Tuple2<Tuple2<Long, Long>, Integer>> {
        private int layer;
        private int inc;
        private int out;

        T4(int i) {
            this.layer = i;
            this.inc = 2 * i - 2;
            this.out = 2 * i - 1;
        }

        T4() {
            this.layer = 1;
            this.inc = 0;
            this.out = 1;
        }
        @Override
        public void flatMap(Tuple2<Node, Node> in, Collector<Tuple2<Tuple2<Long, Long>, Integer>> out) {

            Set<Long> groupedSet = new HashSet<>();
            Node key1 = null;
            Node key2 = null;
            int count = 0;
            // add all neighbors in set

            key1 = in.f0;
            key2 = in.f1;
            groupedSet = key1.getSet(this.inc);
            groupedSet.retainAll(key2.getSet(this.out));
            count = groupedSet.size();
            out.collect(new Tuple2<>(new Tuple2<>(key1.getId(), key2.getId()), count));

        }
    }

    public static class T5
            implements FlatMapFunction<Tuple2<Node, Node>, Tuple2<Tuple2<Long, Long>, Integer>> {
        private int layer;
        private int inc;
        private int out;

        T5(int i) {
            this.layer = i;
            this.inc = 2 * i - 2;
            this.out = 2 * i - 1;
        }

        T5() {
            this.layer = 1;
            this.inc = 0;
            this.out = 1;
        }
        @Override
        public void flatMap(Tuple2<Node, Node> in, Collector<Tuple2<Tuple2<Long, Long>, Integer>> out) {

            Set<Long> groupedSet = new HashSet<>();
            Set<Long> tempSet = new HashSet<>();
            Node key1 = null;
            Node key2 = null;
            int count = 0;
            // add all neighbors in set

            key1 = in.f0;
            key2 = in.f1;
            groupedSet = key1.getSet(this.out);
            tempSet = key2.getSet(this.inc);
            tempSet.retainAll(key2.getSet(this.out));
            groupedSet.retainAll(tempSet);
            count = groupedSet.size();
            out.collect(new Tuple2<>(new Tuple2<>(key1.getId(), key2.getId()), count));

        }
    }

    public static class T6
            implements FlatMapFunction<Tuple2<Node, Node>, Tuple2<Tuple2<Long, Long>, Integer>> {
        private int layer;
        private int inc;
        private int out;

        T6(int i) {
            this.layer = i;
            this.inc = 2 * i - 2;
            this.out = 2 * i - 1;
        }

        T6() {
            this.layer = 1;
            this.inc = 0;
            this.out = 1;
        }
        @Override
        public void flatMap(Tuple2<Node, Node> in, Collector<Tuple2<Tuple2<Long, Long>, Integer>> out) {

            Set<Long> groupedSet = new HashSet<>();
            Set<Long> tempSet = new HashSet<>();
            Node key1 = null;
            Node key2 = null;
            int count = 0;
            // add all neighbors in set

            key1 = in.f0;
            key2 = in.f1;
            tempSet = key1.getSet(this.inc);
            tempSet.retainAll(key1.getSet(this.out));
            groupedSet = key2.getSet(this.out);
            groupedSet.retainAll(tempSet);
            count = groupedSet.size();
            out.collect(new Tuple2<>(new Tuple2<>(key1.getId(), key2.getId()), count));

        }
    }

    public static class T7
            implements FlatMapFunction<Tuple2<Node, Node>, Tuple2<Tuple2<Long, Long>, Integer>> {
        private int layer;
        private int inc;
        private int out;

        T7(int i) {
            this.layer = i;
            this.inc = 2 * i - 2;
            this.out = 2 * i - 1;
        }

        T7() {
            this.layer = 1;
            this.inc = 0;
            this.out = 1;
        }
        @Override
        public void flatMap(Tuple2<Node, Node> in, Collector<Tuple2<Tuple2<Long, Long>, Integer>> out) {

            Set<Long> groupedSet = new HashSet<>();
            Set<Long> tempSet = new HashSet<>();
            Node key1 = null;
            Node key2 = null;
            int count = 0;
            // add all neighbors in set

            key1 = in.f0;
            key2 = in.f1;
            groupedSet = key1.getSet(this.inc);
            tempSet = key2.getSet(this.inc);
            tempSet.retainAll(key2.getSet(this.out));
            groupedSet.retainAll(tempSet);
            count = groupedSet.size();
            out.collect(new Tuple2<>(new Tuple2<>(key1.getId(), key2.getId()), count));

        }
    }

    public static class T8
            implements FlatMapFunction<Tuple2<Node, Node>, Tuple2<Tuple2<Long, Long>, Integer>> {
        private int layer;
        private int inc;
        private int out;

        T8(int i) {
            this.layer = i;
            this.inc = 2 * i - 2;
            this.out = 2 * i - 1;
        }

        T8() {
            this.layer = 1;
            this.inc = 0;
            this.out = 1;
        }
        @Override
        public void flatMap(Tuple2<Node, Node> in, Collector<Tuple2<Tuple2<Long, Long>, Integer>> out) {

            Set<Long> groupedSet = new HashSet<>();
            Set<Long> tempSet = new HashSet<>();
            Node key1 = null;
            Node key2 = null;
            int count = 0;
            // add all neighbors in set

            key1 = in.f0;
            key2 = in.f1;
            tempSet = key1.getSet(this.inc);
            tempSet.retainAll(key1.getSet(this.out));
            groupedSet = key2.getSet(this.inc);
            groupedSet.retainAll(tempSet);
            count = groupedSet.size();
            out.collect(new Tuple2<>(new Tuple2<>(key1.getId(), key2.getId()), count));


        }
    }

    public static class T9
            implements FlatMapFunction<Tuple2<Node, Node>, Tuple2<Tuple2<Long, Long>, Integer>> {
        private int layer;
        private int inc;
        private int out;

        T9(int i) {
            this.layer = i;
            this.inc = 2 * i - 2;
            this.out = 2 * i - 1;
        }

        T9() {
            this.layer = 1;
            this.inc = 0;
            this.out = 1;
        }
        @Override
        public void flatMap(Tuple2<Node, Node> in, Collector<Tuple2<Tuple2<Long, Long>, Integer>> out) {
            Set<Long> groupedSet = new HashSet<>();
            Set<Long> tempSet1 = new HashSet<>();
            Set<Long> tempSet2 = new HashSet<>();
            Node key1 = null;
            Node key2 = null;
            int count = 0;
            // add all neighbors in set

            key1 = in.f0;
            key2 = in.f1;
            tempSet1 = key1.getSet(this.inc);
            tempSet1.retainAll(key1.getSet(this.out));
            tempSet2 = key2.getSet(this.inc);
            tempSet2.retainAll(key2.getSet(this.out));
            groupedSet = tempSet1;
            groupedSet.retainAll(tempSet2);
            count = groupedSet.size();
            out.collect(new Tuple2<>(new Tuple2<>(key1.getId(), key2.getId()), count));

        }
    }

}
