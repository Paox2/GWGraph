package crawler.entity;

import crawler.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ScriptCodeBlock {
    /**
     * basic info
     */
    private String id;
    private String type;
    private String src; // if the script is loaded from a URL
    private String content;
    private String relatedHTMLId;
    private byte isDeleted;

    /**
     * loading mechanism
     */
    private byte async;
    private byte defer; // how the script is executed

    /**
     * Script can be complex and function calls by different caller can cause different effect.
     * Therefore, this is used to represent the effect for different elements caused by caller where the
     * Pair<String, String> is caller id and caller type.v.
     * If the key is <null,null> which means the list of effected is triggered default (i.e., when page loaded).
     * Otherwise, it is triggered by the key (i.e., the caller).
     */
    private Map<Pair<String, String>, List<Effected>> interaction;

    @Getter
    @AllArgsConstructor
    public static class Effected {
        private String effectElementId;
        private String effectElementType;

        // Operation,
        private Pair<String, List<String>> ops;

        public Effected() {}

        public boolean equal(Effected effected) {
            return this.effectElementId.equals(effected.getEffectElementId())
                    && this.effectElementType.equals(effected.getEffectElementType());
        }
    }

    /**
     * No args constructor.
     */
    public ScriptCodeBlock() {
        interaction = new HashMap<>();
        interaction.put(new Pair<>(null, null), new ArrayList<>());
        isDeleted = (byte) 0;
    }

    /**
     *
     * @param callerId
     * @param callerType
     * @param effectedList
     */
    public void addInteraction(String callerId, String callerType, List<Effected> effectedList) {
        Pair<String, String> caller = new Pair<>(callerId, callerType);
        interaction.put(caller, effectedList);
    }

    public void addDefaultInteraction(String effectElementId, String effectElementType, String op, List<String> attributeChange) {
        Pair<String, List<String>> effectOp = new Pair<>(op, attributeChange);
        Effected effected = new Effected(effectElementId, effectElementType, effectOp);
        Pair<String, String> caller = new Pair<>(null, null);
        interaction.get(caller).add(effected);
    }
}
