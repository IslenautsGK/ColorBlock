package com.isgk.colorblock.util.jsengine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public enum ParticleScriptEngineManager {

	INSTANCE;

	private ScriptEngine scriptEngine;

	private ParticleScriptEngineManager() {
		this.scriptEngine = new ScriptEngineManager(null).getEngineByName("nashorn");
	}

	public ScriptEngine getScriptEngine() {
		return scriptEngine;
	}

}
