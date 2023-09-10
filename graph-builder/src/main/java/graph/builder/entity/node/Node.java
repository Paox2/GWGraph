package graph.builder.entity.node;

import graph.builder.common.NodeType;
import graph.builder.entity.edge.Edge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Node interface contains the generic variables and functions.
 */
@Data
@AllArgsConstructor
public class Node {

    /**
     * The unique id.
     *
     */
    protected String id;

    /**
     * The node type.
     *
     * @see NodeType
     */
    protected String nodeType;

    /**
     * A list of edges points to this node.
     */
    protected List<String> inList;

    /**
     * A list of edges start from this node.
     */
    protected List<String> outList;

    /**
     * The commit if user want to add some attributes to the node.
     */
    protected Map<String, String> commit;


    /**
     * add new edge in "In" list.
     *
     * @param in
     */
    public void addInEdge(@NonNull Edge in) {
        if (!inList.contains(in.getId())) {
            inList.add(in.getId());
        }
    }

    /**
     * add new edge in "Out" list.
     *
     * @param out
     */
    public void addOutEdge(@NonNull Edge out) {
        if (!outList.contains(out.getId())) {
            outList.add(out.getId());
        }
    }

    /**
     * Remove the edge in "in" list.
     *
     * @param in
     */
    public void removeInEdge(@NonNull Edge in) {
        inList.remove(in.getId());
    }

    /**
     * Remove the edge in "in" list.
     *
     * @param out
     */
    public void removeOutEdge(@NonNull Edge out) {
        outList.remove(out.getId());
    }

    /**
     * Return the size of "In" set.
     *
     * @return - size of "In" set.
     */
    public int inSize() {
        return inList.size();
    }

    /**
     * Return the size of "Out" set.
     *
     * @return - size of "Out" set.
     */
    public int outSize() {
        return outList.size();
    }

    /**
     * Get the value of corresponding value in the node.
     *
     * @param key
     * @return
     */
    public String getCommitFor(String key) {
        return commit.get(key);
    }

    /**
     * Update the value for commit, if the key does not exists in the commit, it will create the key in the commit
     * and save the 'newValue' as the value.
     *
     * @param key
     * @param newValue
     * @return - the previous value associated with key, or null if there was no mapping for key.
     */
    public String changeCommitFor(String key, String newValue) {
        return commit.put(key, newValue);
    }


    /**
     * Put the <key, value> pair input the commit map, it the key exists the input value will replace the old value and return.
     * Otherwiser, return null.
     *
     * @param key
     * @param value
     * @return - the previous value associated with key, or null if there was no mapping for key.
     */
    public String addCommit(String key, String value) {
        return commit.put(key, value);
    }

    /**
     * Return the key in commit.
     *
     * @param key
     * @return
     */
    public String deleteCommit(String key) {
        return commit.remove(key);
    }
}
