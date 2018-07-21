package com.walker.sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.LeafReaderContext;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.script.CompiledScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.LeafSearchScript;
import org.elasticsearch.script.ScriptEngineService;
import org.elasticsearch.script.SearchScript;
import org.elasticsearch.search.lookup.LeafSearchLookup;
import org.elasticsearch.search.lookup.SearchLookup;

public class IdsSortPluginHandle  implements ScriptEngineService {
    private final static Logger logger = LogManager.getLogger(IdsSortPluginHandle.class);

    @Override
    public String getType() {
        return "expert_scripts";
    }

    @Override
    public Object compile(String scriptName, String scriptSource, Map<String, String> params) {
        if ("ids_sort".equals(scriptSource)) {
            return scriptSource;
        }
        throw new IllegalArgumentException("Unknown script name " + scriptSource);
    }

    @Override
    public SearchScript search(CompiledScript compiledScript, SearchLookup lookup, @Nullable Map<String, Object> vars) {

        /**
         * 校验输入参数，DSL中params 参数列表
         */
        final List<String> ids;
        if (vars == null || !vars.containsKey("ids")) {
            ids = new ArrayList<>();
        } else {
            ids = Arrays.asList(XContentMapValues.nodeStringArrayValue(vars.get("ids")));
        }
        logger.info("接收到id集合  {}", ids);
        return new SearchScript() {
            @Override
            public LeafSearchScript getLeafSearchScript(LeafReaderContext context) throws IOException {
                final LeafSearchLookup leafLookup = lookup.getLeafSearchLookup(context);

                return new LeafSearchScript() {
                    @Override
                    public void setDocument(int doc) {
                        if (leafLookup != null) {
                            leafLookup.setDocument(doc);
                        }
                    }

                    @Override
                    public double runAsDouble() {
                        /**
                         * 获取document中字段内容
                         */
						String item_number = String.valueOf(leafLookup.doc().get("item_number").get(0));
						String goods_id = String.valueOf(leafLookup.doc().get("goods_id").get(0));
						if (ids.contains(item_number)) {
							logger.info("查询到集合中元素 {},索引 {}", item_number, ids.indexOf(item_number));
							return ids.indexOf(item_number);
						} else if (ids.contains(goods_id)) {
							logger.info("查询到集合中元素 {},索引 {}", goods_id, ids.indexOf(goods_id));
							return ids.indexOf(goods_id);
						} else {
							return Double.MAX_VALUE;
						}
                    }
                };
            }

            @Override
            public boolean needsScores() {
                return false;
            }
        };
    }

    @Override
    public ExecutableScript executable(CompiledScript compiledScript, @Nullable Map<String, Object> params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInlineScriptEnabled() {
        return true;
    }

    @Override
    public void close() {
    }
}

