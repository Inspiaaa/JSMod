

/*
Module:
{
	id: "", 	// Guaranteed
	name: "",	// Guaranteed
	desc: "",	// Guaranteed

	onTick: () => void,
	onRender: (partialTick) => void,
	onKeyDown: (keyCode) => void,
	onKeyUp: (keyCode) => void,

	isActive: false,			// Guaranteed
	activeByDefault: false,		// Guaranteed
	activationKey: KEY_NONE,

	saveState: () => Object,
	loadState: (Object) => void
	cleanState: () => void
}
*/

// TODO: Maybe call deactivate before quitting

function _createEngine() {
	"use strict";

	function* _makeIdGenerator() {
		var idCounter = 0

		while (true) {
			yield idCounter++
		}
	}

	var _idGenerator = _makeIdGenerator();
	function nextId() { return idGenerator.next().value }

	var self = {}

	function activateModule(module) {
		Chat.msg(Chat.GREEN + Chat.BOLD + "Activating " + module.name)
		module.isActive = true
		
		if ("onActivate" in module) {
			module.onActivate()
		}

		if ("onTick" in module) {
			onTickListeners.add(module.onTick)
		}

		if ("onRender" in module) {
			onRenderListeners.add(module.onRender)
		}

		if ("onKeyDown" in module) {
			onKeyDownListeners.add(module.onKeyDown)
		}

		if ("onKeyUp" in module) {
			onKeyUpListeners.add(module.onKeyUp)
		}
	}

	function deactivateModule(module) {
		Chat.msg(Chat.RED + "Deactivating " + module.name)
		module.isActive = false
		
		if ("onDeactivate" in module) {
			module.onDeactivate()
		}

		if ("onTick" in module) {
			onTickListeners.delete(module.onTick)
		}

		if ("onRender" in module) {
			onRenderListeners.delete(module.onRender)
		}

		if ("onKeyDown" in module) {
			onKeyDownListeners.delete(module.onKeyDown)
		}

		if ("onKeyUp" in module) {
			onKeyUpListeners.delete(module.onKeyUp)
		}
	}

	function toggleModule(module) {
		if (module.isActive) {
			deactivateModule(module)
		}
		else {
			activateModule(module)
		}
	}
	
	function setModuleActive(module, isActive) {
		if (isActive != module.isActive) {
			toggleModule(module)
		}
	}

	function setDefault(module, key, value) {
		if (! (key in module)) {
			module[key] = value
		}
	}

	function loadModule(moduleFunc) {
		var module = moduleFunc()

		module.isActive = false

		if (! ("id" in module)) {
			if ("name" in module) {
				module.id = module.name
			}
			else {
				module.id = "#" + nextId().toString()
			}
		}

		setDefault(module, "name", "Module")
		setDefault(module, "desc", "")
		setDefault(module, "activeByDefault", false)

		if ("activationKey" in module) {
			if (! keyToActivate.has(module.activationKey)) {
				keyToActivate.set(module.activationKey, [])
			}
			keyToActivate.get(module.activationKey).push(module)
		}

		if (module.activeByDefault) {
			activateModule(module)
		}

		idToModule.set(module.id, module)
		funcToModule.set(moduleFunc, module)
	}

	function loadModules() {
		modulesToLoad.forEach(loadModule);

		// Clear the list
		modulesToLoad = []
	}

	var idToModule = new Map()
	var funcToModule = new Map()

	var modulesToLoad = []

	var onTickListeners = new Set()
	var onRenderListeners = new Set()
	var onKeyDownListeners = new Set()
	var onKeyUpListeners = new Set()
	
	// Map of KeyCodes (int) to Array of Modules [Object, ...]
	var keyToActivate = new Map()

	self.registerModule = function(func) {
		modulesToLoad.push(func)
	}

	// TODO: Create function to unregister module

	self.onTick = function() {
		for (var listener of onTickListeners) {
			listener()
		}
	}

	self.onKeyDown = function(keyCode) {
		if (keyToActivate.has(keyCode)) {
			keyToActivate.get(keyCode).forEach(module => toggleModule(module))
		}

		for (var listener of onKeyDownListeners) {
			listener(keyCode);
		}
	}

	self.onKeyUp = function(keyCode) {
		for (var listener of onKeyUpListeners) {
			listener(keyCode);
		}
	};

	self.onInit = function() {
		loadModules()
	}

	self.saveState = function() {
		var moduleStates = {}
		var moduleActive = {}

		idToModule.forEach(module => {
			if ("saveState" in module) {
				moduleStates[module.id] = module.saveState()
			}

			moduleActive[module.id] = module.isActive
		})

		return JSON.stringify({
			moduleStates: moduleStates,
			moduleActive: moduleActive
		})
	}

	self.loadState = function(stateStr) {
		try {
			var state = JSON.parse(stateStr)
			
			Object.getOwnPropertyNames(state.moduleActive).forEach(id => {
				if (idToModule.has(id)) {
					setModuleActive(idToModule.get(id), state.moduleActive[id])
				}
			})

			Object.getOwnPropertyNames(state.moduleStates).forEach(id => {
				if (idToModule.has(id)) {
					var module = idToModule.get(id)

					try {
						if ("loadState" in module) {
							module.loadState(state.moduleStates[id])
						}
					}
					catch (error) {
						// Error loading the previous state back in
						// Probable cause: The previous state is no longer compatible with the new code
						// => Clean the state in case it has only partly loaded

						if ("cleanState" in module) {
							module.cleanState()
						}

						console.error("Error loading previous state for " + module.id)
						console.error("Probable cause: The previous state is no longer compatible with the new code.")
						console.error(error.toString())
					}
				}
			})
		}
		catch (error) {
			Console.error("Error loading previous state")
			Console.error(error.toString())
		}
	}

	self.activate = function(moduleFunc) {
		activateModule(funcToModule.get(moduleFunc))
	}

	self.deactivate = function(moduleFunc) {
		deactivateModule(funcToModule.get(moduleFunc))
	}

	self.toggle = function(moduleFunc) {
		toggleModule(funcToModule.get(moduleFunc))
	}

	return self
}


const Engine = _createEngine()

const saveState = Engine.saveState
const loadState = Engine.loadState

const onInit = Engine.onInit

const onKeyDown = Engine.onKeyDown
const onKeyUp = Engine.onKeyUp

const onTick = Engine.onTick
