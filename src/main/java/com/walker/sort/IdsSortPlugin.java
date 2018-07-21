package com.walker.sort;


import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.ScriptPlugin;
import org.elasticsearch.script.ScriptEngineService;


public class IdsSortPlugin extends Plugin implements ScriptPlugin {

    @Override
    public ScriptEngineService getScriptEngineService(Settings settings) {
        return new IdsSortPluginHandle();
    }
}
