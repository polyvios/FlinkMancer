/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flinkmancer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.math3.util.Combinations;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 *
 * @author Hergal
 */
public class Features {

    public static class Feat implements FlatMapFunction<Tuple2<Node, Node>, Tuple2<String, String>> {

        @Override
        public void flatMap(Tuple2<Node, Node> in, Collector<Tuple2<String, String>> out) {
            Integer[] feats;
            feats = new Integer[100];
            int incoming = 0;
            int outgoing = 1;
            int index = 0;
            int numOfSets = in.f0.getSetsCount();
            int temp;
            int layers = (int) numOfSets / 2;
            Node u = in.f0;
            Node v = in.f1;
            //Features 1-10
            feats[index++] = u.getSet(1).size();
            feats[index++] = u.getSet(3).size();
            feats[index++] = u.getSet(5).size();
            feats[index++] = u.getSet(7).size();
            feats[index++] = v.getSet(0).size();
            feats[index++] = v.getSet(2).size();
            feats[index++] = v.getSet(4).size();
            feats[index++] = v.getSet(6).size();
            feats[index++] = u.diffrenetOut();
            feats[index++] = v.differentIn();

            //Features 11- 46 (T)
            for (int i = 0; i < layers; i++) {
                Set<Long> incSetU = u.getSet(incoming);
                Set<Long> incSetV = v.getSet(incoming);
                Set<Long> outSetU = u.getSet(outgoing);
                Set<Long> outSetV = v.getSet(outgoing);
                Set<Long> temp1;//temp1
                Set<Long> temp2;//temp2
                //T1
                temp1 = incSetU;
                temp1.retainAll(incSetV);
                feats[index++] = temp1.size();
                //T2
                temp1 = outSetU;
                temp1.retainAll(outSetV);
                feats[index++] = temp1.size();
                //T3
                temp1 = outSetU;
                temp1.retainAll(incSetV);
                feats[index++] = temp1.size();
                //T4
                temp1 = incSetU;
                temp1.retainAll(outSetV);
                feats[index++] = temp1.size();
                //T5
                temp1 = outSetU;
                temp2 = incSetV;
                temp2.retainAll(outSetV);
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();
                //T6
                temp1 = incSetU;
                temp1.retainAll(outSetU);
                temp2 = outSetV;
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();
                //T7
                temp1 = incSetU;
                temp2 = incSetV;
                temp2.retainAll(outSetV);
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();
                //T8
                temp1 = incSetU;
                temp1.retainAll(outSetU);
                temp2 = incSetV;
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();
                //T9
                temp1 = incSetU;
                temp1.retainAll(outSetU);
                temp2 = incSetV;
                temp2.retainAll(outSetV);
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();

                //Go to next layer
                incoming = incoming + 2;
                outgoing = outgoing + 2;
            }

            //Features 47-100 (P)
            Combinations c = new Combinations(layers, 2);
            Iterator<int[]> k = c.iterator();
            String test = "";
            ArrayList<Tuple2<Integer, Integer>> PPairs = new ArrayList<>();
            //pairs created [1, 2], [1, 3], [2, 3], [1, 4], [2, 4], [3, 4]
            while (k.hasNext()) {
                int[] t = k.next();
                Tuple2<Integer, Integer> t2 = new Tuple2<>(t[0] + 1, t[1] + 1);
                PPairs.add(t2);
            }
            for (int i = 0; i < PPairs.size(); i++) {
                Tuple2<Integer, Integer> pair = PPairs.get(i);
                int layerIncOne = 2 * pair.f0 - 2;
                int layerOutOne = 2 * pair.f0 - 1;
                int layerIncTwo = 2 * pair.f1 - 2;
                int layerOutTwo = 2 * pair.f1 - 1;

                Set<Long> setOneU = u.getSet(layerIncOne);
                setOneU.addAll(u.getSet(layerOutOne));
                Set<Long> setTwoU = u.getSet(layerIncTwo);
                setTwoU.addAll(u.getSet(layerOutTwo));

                Set<Long> setOneV = v.getSet(layerIncOne);
                setOneV.addAll(v.getSet(layerOutOne));
                Set<Long> setTwoV = v.getSet(layerIncTwo);
                setTwoV.addAll(v.getSet(layerOutTwo));

                Set<Long> temp1;//temp1
                Set<Long> temp2;//temp2

                //P1
                temp1 = setOneU;
                temp1.retainAll(setTwoU);
                temp2 = setOneV;
                temp2.retainAll(setTwoV);
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();

                //P2
                temp1 = setOneU;
                temp1.retainAll(setTwoU);
                temp2 = setOneV;
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();

                //P3
                temp1 = setOneU;
                temp1.retainAll(setTwoU);
                temp2 = setTwoV;
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();

                //P4
                temp1 = setOneU;
                temp2 = setOneV;
                temp2.retainAll(setTwoV);
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();

                //P5
                temp1 = setTwoU;
                temp2 = setOneV;
                temp2.retainAll(setTwoV);
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();

                //P6
                temp1 = setOneU;
                temp2 = setOneV;
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();

                //P7
                temp1 = setOneU;
                temp2 = setTwoV;
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();

                //P8
                temp1 = setTwoU;
                temp2 = setOneV;
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();

                //P9
                temp1 = setTwoU;
                temp2 = setTwoV;
                temp1.retainAll(temp2);
                feats[index++] = temp1.size();

            }

            String edge = in.f0.getId().toString() + ", " + in.f1.getId().toString();

            /* alternative builder to remove spaces 
            StringBuilder builder = new StringBuilder();
            for (Integer value : feats) {
                builder.append(",");
                builder.append(value);

            }

            String features = builder.toString();
             */
            String features = Arrays.toString(feats)
                    .replace("[", " ")
                    .replace("]", "");

            out.collect(new Tuple2<>(edge, features));
        }
    }

}
