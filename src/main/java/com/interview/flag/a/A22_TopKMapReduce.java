package com.interview.flag.a;


/**
 * Created by stefanie on 1/28/15.
 *  
 * http://blog.pivotal.io/pivotal/products/how-hadoop-mapreduce-can-transform-how-you-build-top-ten-lists
 */
public class A22_TopKMapReduce {
    /**
    public static class topKMapper extends Mapper<Object, IntWritable, NullWritable, IntWritable> {
        static final int K = 10;
        private TreeSet<Integer> topKSet = new TreeSet();

        public void map(Object key, IntWritable value, Context context) throws IOException, InterruptedException {

            // Split on tab to get the cat name and the weight
            String v[] = value.toString().split("\t");
            Double weight = Double.parseDouble(v[1]);

            topKSet.add(value.get());

            if (topKSet.size() > K) {
                topKSet.remove(topKSet.first());
            }
        }

        protected void cleanup(Context context) throws IOException, InterruptedException {
            for (Integer number : topKSet) {
                context.write(NullWritable.get(), number);
            }
        }
    }

    public static class topKReducer extends Reducer<NullWritable, Text, NullWritable, Text> {

        public void reduce(NullWritable key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            TreeSet<Integer> topKSet = new TreeSet();

            for (IntWritable number : values) {
                topKSet.add(number.get());
                if (topKSet.size() > 10) {
                    topKSet.remove(topKSet.first());
                }
            }

            for (Integer number : topKSet) {
                context.write(NullWritable.get(), number);
            }
        }
    }
    */
}
