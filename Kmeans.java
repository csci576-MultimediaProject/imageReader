import java.util.ArrayList;
import java.util.Random;

public class Kmeans {

    private final int           NUM_CLUSTERS = 2;                        // Total
                                                                          // clusters
    private final int           TOTAL_DATA   = 300;                      // Total
                                                                          // data
                                                                          // points

    private ArrayList<Data>     dataSet      = new ArrayList<Data>();
    private ArrayList<Centroid> centroids    = new ArrayList<Centroid>();

    // [300][256] gray scale 256 levels, 300 pictures
    public void kmeans(int y[][]) {
        // Randomly choose two cluster
        Random rand = new Random();
        int cluster1 = rand.nextInt(300);
        int cluster2 = rand.nextInt(300);
        System.out.println(cluster1);
        System.out.println(cluster2);
        centroids.add(new Centroid(y[cluster1])); // lowest set.
        centroids.add(new Centroid(y[cluster2])); // highest set.
        //
        // // find two individuals furthest apart, the smallest sum is the min
        // System.out.println(y.length);
        // int row_sum[] = new int[y.length];
        // for (int i = 0; i < y.length; i++) {
        // row_sum[i] = 0;
        // }
        // for (int i = 0; i < y.length; i++) {
        // for (int j = 0; j < y[i].length; j++) {
        // row_sum[i] += y[i][j];
        // }
        // }
        // // brute force find min and max
        // int min = 0, max = 0, min_level = -1, max_level = -1;
        // // initial
        // min = row_sum[0];
        // max = row_sum[0];
        // min_level = 0;
        // max_level = 0;
        // for (int i = 0; i < row_sum.length; i++) {
        // if (row_sum[i] < min) {
        // min = row_sum[i];
        // min_level = i;
        // }
        // if (row_sum[i] > max) {
        // max = row_sum[i];
        // max_level = i;
        // }
        // }

        // 分類
        kMeanCluster(y);
    }

    // private static void initialize() {
    // System.out.println("Centroids initialized at:");
    // centroids.add(new Centroid(1.0, 1.0)); // lowest set.
    // centroids.add(new Centroid(5.0, 7.0)); // highest set.
    // System.out.println("     ("
    // + centroids.get(0).X()
    // + ", "
    // + centroids.get(0).Y()
    // + ")");
    // System.out.println("     ("
    // + centroids.get(1).X()
    // + ", "
    // + centroids.get(1).Y()
    // + ")");
    // System.out.print("\n");
    // return;
    // }

    private void kMeanCluster(int y[][]) {
        final double bigNumber = Math.pow(10, 10); // some big number that's
        // sure to be larger than our
        // data range.
        double minimum = bigNumber; // The minimum value to beat.
        double distance = 0.0; // The current minimum value.
        int sampleNumber = 0;
        int cluster = 0;
        boolean isStillMoving = true;
        Data newData = null;

        // Add in new data, one at a time, recalculating centroids with each new
        // one.
        while (dataSet.size() < TOTAL_DATA) {
            newData = new Data(y[sampleNumber]);
            dataSet.add(newData);
            minimum = bigNumber;
            for (int i = 0; i < NUM_CLUSTERS; i++) {
                distance = dist(newData, centroids.get(i));
                if (distance < minimum) {
                    minimum = distance;
                    cluster = i;
                }
            }
            newData.cluster(cluster);

            // calculate new centroids.
            for (int i = 0; i < NUM_CLUSTERS; i++) {
                // int totalX = 0;
                // int totalY = 0;
                int totalInCluster = 0; // how many in a cluster
                int[] total = new int[256];
                // initial
                for (int j = 0; j < total.length; j++) {
                    total[j] = 0;
                }
                for (int j = 0; j < dataSet.size(); j++) {
                    if (dataSet.get(j).cluster() == i) { // if it belongs to
                                                         // cluster i
                        int[] data = dataSet.get(j).pos();
                        for (int k = 0; k < data.length; k++) {
                            total[k] += data[k];
                        }
                        // totalX += dataSet.get(j).X();
                        // totalY += dataSet.get(j).Y();
                        totalInCluster++;
                    }
                }
                if (totalInCluster > 0) {
                    for (int j = 0; j < total.length; j++) {
                        total[j] = total[j] / totalInCluster;
                    }
                    centroids.get(i).pos(total);
                    // centroids.get(i).X(totalX / totalInCluster);
                    // centroids.get(i).Y(totalY / totalInCluster);
                }
            }
            sampleNumber++;
        }

        // Now, keep shifting centroids until equilibrium occurs.
        while (isStillMoving) {
            // calculate new centroids.
            for (int i = 0; i < NUM_CLUSTERS; i++) {
                int totalInCluster = 0; // how many in a cluster
                int[] total = new int[256];
                // initial
                for (int j = 0; j < total.length; j++) {
                    total[j] = 0;
                }
                for (int j = 0; j < dataSet.size(); j++) {
                    if (dataSet.get(j).cluster() == i) {
                        // totalX += dataSet.get(j).X();
                        // totalY += dataSet.get(j).Y();
                        int[] data = dataSet.get(j).pos();
                        for (int k = 0; k < data.length; k++) {
                            total[k] += data[k];
                        }
                        totalInCluster++;
                    }
                }
                if (totalInCluster > 0) {
                    for (int j = 0; j < total.length; j++) {
                        total[j] = total[j] / totalInCluster;
                    }
                    centroids.get(i).pos(total);
                    // centroids.get(i).X(totalX / totalInCluster);
                    // centroids.get(i).Y(totalY / totalInCluster);
                }
            }

            // Assign all data to the new centroids
            isStillMoving = false;

            for (int i = 0; i < dataSet.size(); i++) {
                Data tempData = dataSet.get(i);
                minimum = bigNumber;
                for (int j = 0; j < NUM_CLUSTERS; j++) {
                    distance = dist(tempData, centroids.get(j));
                    if (distance < minimum) {
                        minimum = distance;
                        cluster = j;
                    }
                }
                // tempData.cluster(cluster);
                if (tempData.cluster() != cluster) {
                    tempData.cluster(cluster);
                    isStillMoving = true;
                }
            }
        }
        return;
    }

    /**
     * // Calculate Euclidean distance.
     * 
     * @param d
     *            - Data object.
     * @param c
     *            - Centroid object.
     * @return - double value.
     */
    private double dist(Data d, Centroid c) {
        int[] pos_data = d.pos();
        int[] pos_centroid = c.pos();
        int sum_square = 0;
        if (pos_data.length != pos_centroid.length) {
            System.out.println("different");
            return 0;
        }
        for (int i = 0; i < pos_data.length; i++) {
            sum_square += Math.pow(pos_data[i] - pos_centroid[i], 2);
        }
        return Math.sqrt(sum_square);
    }

    private static class Data {
        private int[] pos;
        private int   mCluster = 0;

        public Data() {
        }

        public Data(int[] pos) {
            this.pos(pos);
        }

        public void pos(int[] pos) {
            this.pos = pos;
        }

        public int[] pos() {
            return this.pos;
        }

        public void cluster(int clusterNumber) {
            this.mCluster = clusterNumber;
        }

        public int cluster() {
            return this.mCluster;
        }
    }

    private static class Centroid {
        private int[] pos;

        public Centroid() {
        }

        public Centroid(int[] pos) {
            this.pos = pos;
            return;
        }

        public void pos(int[] pos) {
            this.pos = pos;
        }

        public int[] pos() {
            return this.pos;
        }

        // public void X(double newX) {
        // this.mX = newX;
        // return;
        // }
        //
        // public double X() {
        // return this.mX;
        // }
        //
        // public void Y(double newY) {
        // this.mY = newY;
        // return;
        // }
        //
        // public double Y() {
        // return this.mY;
        // }
    }

    public static void main(String[] args) {
        int[][] array = new int[300][256];
        Kmeans t = new Kmeans();
        t.kmeans(array);
    }
}