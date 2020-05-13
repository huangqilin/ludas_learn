package consistanthash;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性hash实现
 *
 * @author
 * <p>
 * 2015年1月22日 下午2:34:05
 */
public class ConsistentHash {

    //圆环  用treemap的主要原因是可以排序
    private TreeMap<Long, Node> circle = new TreeMap<>();

    //真实结点
//    private List<Node> realNodes = new ArrayList<Node>();

    public static void main(String[] args) {
        ConsistentHash h = new ConsistentHash();
        h.addNode(new Node(1));
        h.addNode(new Node(2));
        h.addNode(new Node(3));
        h.addNode(new Node(4));
        h.addNode(new Node(5));
        h.addNode(new Node(6));
        h.addNode(new Node(7));
        h.addNode(new Node(8));
        h.addNode(new Node(9));

        for (int i = 0; i < 50; i++) {
            h.getNode("" + i);
        }
    }


    public void addNode(Node node) {
//        realNodes.add(node);

        Long nodeKey = md5(node.toString());
        System.out.println(node.toString() + " md5:" + nodeKey);
        circle.put(nodeKey, node);
    }

    public void removeNode(Node node) {
//        realNodes.remove(node);

        Long nodeKey = md5(node.toString());
        circle.remove(nodeKey);
    }

    /**
     * @param key
     * @return
     */
    public Node getNode(String key) {
        //treemap 转成 排序好的map
        Long keyMd5 = md5(key);
        SortedMap<Long, Node> sortedMap = circle.tailMap(keyMd5);
        Long k = null;

        //没命中则选择第一个
        if (sortedMap.isEmpty()) {
            k = (circle.firstKey());
            System.out.println("not hit");
        } else {
            k = (sortedMap.firstKey());
        }
        Node node = circle.get(k);

        //正常情况下 md5(key) < md5(node)
        System.out.println(key + "(" + keyMd5 + ") --->" + node.toString() + "(" + md5(node.toString()) + ")");
        return node;
    }

    private long md5(String key) {
        try {
            byte[] bKey = MessageDigest.getInstance("MD5").digest(key.getBytes());
            long res = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16) | ((long) (bKey[1] & 0xFF) << 8) | bKey[0] & 0xFF;
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    static class Node {
        private int nodeNum;

        public Node(int num) {
            this.nodeNum = num;
        }

        @Override
        public String toString() {
            return "真实结点:" + nodeNum;
        }
    }
}