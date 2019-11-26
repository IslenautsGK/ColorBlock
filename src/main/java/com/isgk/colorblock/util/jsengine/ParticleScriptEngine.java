package com.isgk.colorblock.util.jsengine;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.isgk.colorblock.client.ClientProxy;
import com.isgk.colorblock.util.client.component.ClientMessageUtil;
import com.isgk.colorblock.util.commoninterface.IExecutable;
import com.isgk.colorblock.util.matrix.Matrix;

public class ParticleScriptEngine implements IExecutable {

	private ScriptEngine se;
	private String script;

	public ParticleScriptEngine(String script) {
		this(ParticleScriptEngineManager.INSTANCE.getScriptEngine(), script);
	}

	public ParticleScriptEngine(ScriptEngine se, String script) {
		this.se = se;
		this.script = script;
	}

	@Override
	public void put(String key, Object value) {
		if (value instanceof Number) {
			se.put(key, ((Number) value).doubleValue());
		} else if (value instanceof Matrix) {
			se.put(key, ((Matrix) value).getNumber());
		} else {
			ClientMessageUtil.addChatMessage("Put Args Error:" + value.toString());
			throw new RuntimeException();
		}
	}

	@Override
	public Object get(String key) {
		Object obj = se.get(key);
		if (obj instanceof Matrix) {
			return ((Matrix) obj).getNumber();
		} else if (obj instanceof Number) {
			return ((Number) obj).doubleValue();
		}
		ClientMessageUtil.addChatMessage("Get Args Error:" + obj.toString());
		throw new RuntimeException();
	}

	@Override
	public Object invoke() {
		try {
			return se.eval(script);
		} catch (ScriptException e) {
			ClientProxy.log.error(e.toString());
			return null;
		}
	}

}
